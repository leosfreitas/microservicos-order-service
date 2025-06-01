package store.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import store.product.ProductOut;

@Service
@EnableCaching
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    @Cacheable(value = "products", key = "#id")
    private ProductOut findProductById(String id) {
        String url = "http://product:8080/product/" + id;
        try {
            ResponseEntity<ProductOut> response = restTemplate.getForEntity(url, ProductOut.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }
    }

    @CachePut(value = "orders", key = "#result.id()")
    @CacheEvict(value = "ordersByAccount", key = "#idAccount")
    public OrderOut create(OrderIn orderIn, String idAccount) {
        if (orderIn.items() == null || orderIn.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must have at least one item");
        }
        
        Order order = OrderParser.to(orderIn, idAccount);
        double orderTotal = 0.0;
        
        List<OrderItem> orderItems = new ArrayList<>();
        List<ProductOut> products = new ArrayList<>();
        
        for (OrderItemIn itemIn : orderIn.items()) {
            if (itemIn.quantity() == null || itemIn.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantity must be greater than zero");
            }
            
            ProductOut product = findProductById(itemIn.idProduct());
            products.add(product);
            
            double itemTotal = product.price() * itemIn.quantity();
            
            OrderItem orderItem = OrderItem.builder()
                .idProduct(itemIn.idProduct())
                .quantity(itemIn.quantity())
                .total(itemTotal)
                .build();
            
            orderItems.add(orderItem);
            orderTotal += itemTotal;
        }
        
        order.total(orderTotal);
        
        OrderModel savedOrder = orderRepository.save(new OrderModel(order));
        order = savedOrder.to();
        
        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            item.idOrder(order.id());
            OrderItemModel savedItem = orderItemRepository.save(new OrderItemModel(item));
            savedItems.add(savedItem.to());
        }
        
        return OrderParser.toWithItems(order, savedItems, products);
    }

    @Cacheable(value = "ordersByAccount", key = "#idAccount")
    public List<OrderSummaryOut> findAllByAccount(String idAccount) {
        List<OrderSummaryOut> result = new ArrayList<>();
        for (OrderModel orderModel : orderRepository.findByIdAccount(idAccount)) {
            result.add(OrderParser.toSummary(orderModel.to()));
        }
        return result;
    }
    
    @Cacheable(value = "orders", key = "#id")
    public OrderOut findByIdAndAccount(String id, String idAccount) {
        OrderModel orderModel = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        Order order = orderModel.to();
        
        if (!order.idAccount().equals(idAccount)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemModel itemModel : orderItemRepository.findByIdOrder(id)) {
            items.add(itemModel.to());
        }
        
        List<ProductOut> products = new ArrayList<>();
        for (OrderItem item : items) {
            ProductOut product = findProductById(item.idProduct());
            if (product != null) {
                products.add(product);
            }
        }
        
        return OrderParser.toWithItems(order, items, products);
    }
}