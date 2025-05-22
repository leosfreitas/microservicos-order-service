// OrderService.java
package store.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import store.product.ProductOut;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private RestTemplate restTemplate;

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

    public OrderOut create(OrderIn orderIn, String idAccount) {
        // Validações
        if (orderIn.items() == null || orderIn.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must have at least one item");
        }
        
        // Preparar o pedido
        Order order = OrderParser.to(orderIn, idAccount);
        double orderTotal = 0.0;
        
        // Recuperar produtos e calcular totais
        List<OrderItem> orderItems = new ArrayList<>();
        List<ProductOut> products = new ArrayList<>();
        
        for (OrderItemIn itemIn : orderIn.items()) {
            // Validação da quantidade
            if (itemIn.quantity() == null || itemIn.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantity must be greater than zero");
            }
            
            // Buscar produto usando RestTemplate
            ProductOut product = findProductById(itemIn.idProduct());
            products.add(product);
            
            // Calcular total do item
            double itemTotal = product.price() * itemIn.quantity();
            
            // Criar item do pedido
            OrderItem orderItem = OrderItem.builder()
                .idProduct(itemIn.idProduct())
                .quantity(itemIn.quantity())
                .total(itemTotal)
                .build();
            
            orderItems.add(orderItem);
            orderTotal += itemTotal;
        }
        
        // Definir o total do pedido
        order.total(orderTotal);
        
        // Salvar o pedido
        OrderModel savedOrder = orderRepository.save(new OrderModel(order));
        order = savedOrder.to();
        
        // Salvar os itens do pedido
        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            item.idOrder(order.id());
            OrderItemModel savedItem = orderItemRepository.save(new OrderItemModel(item));
            savedItems.add(savedItem.to());
        }
        
        // Montar resposta
        return OrderParser.to(order, savedItems, products);
    }

    public List<OrderOut> findAllByAccount(String idAccount) {
        List<OrderOut> result = new ArrayList<>();
        for (OrderModel orderModel : orderRepository.findByIdAccount(idAccount)) {
            result.add(OrderParser.toSummary(orderModel.to()));
        }
        return result;
    }
    
    public OrderOut findByIdAndAccount(String id, String idAccount) {
        // Buscar o pedido
        OrderModel orderModel = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        Order order = orderModel.to();
        
        // Verificar se o pedido pertence ao usuário atual
        if (!order.idAccount().equals(idAccount)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        
        // Buscar os itens do pedido
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemModel itemModel : orderItemRepository.findByIdOrder(id)) {
            items.add(itemModel.to());
        }
        
        // Buscar os produtos usando RestTemplate
        List<ProductOut> products = new ArrayList<>();
        for (OrderItem item : items) {
            ProductOut product = findProductById(item.idProduct());
            if (product != null) {
                products.add(product);
            }
        }
        
        // Montar resposta
        return OrderParser.to(order, items, products);
    }
}