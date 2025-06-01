package store.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import store.product.ProductOut;

public class OrderParser {

    public static Order to(OrderIn in, String idAccount) {
        if (in == null) return null;
        
        return Order.builder()
            .idAccount(idAccount)
            .date(LocalDateTime.now())
            .items(in.items().stream().map(OrderParser::to).toList())
            .build();
    }
    
    public static OrderItem to(OrderItemIn in) {
        if (in == null) return null;
        
        return OrderItem.builder()
            .idProduct(in.idProduct())
            .quantity(in.quantity())
            .build();
    }

    // Para GET /order/{id} e POST /order (com items)
    public static OrderOut toWithItems(Order order, List<OrderItem> items, List<ProductOut> products) {
        if (order == null) return null;
        
        List<OrderItemOut> itemsOut = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            ProductOut product = products.get(i);
            
            OrderItemOut itemOut = OrderItemOut.builder()
                .id(item.id())
                .product(product)
                .quantity(item.quantity())
                .total(item.total())
                .build();
            
            itemsOut.add(itemOut);
        }
        
        return OrderOut.builder()
            .id(order.id())
            .date(order.date())
            .items(itemsOut)
            .total(order.total())
            .build();
    }
    
    // Para GET /order (lista resumida, sem items)
    public static OrderSummaryOut toSummary(Order order) {
        if (order == null) return null;
        
        return OrderSummaryOut.builder()
            .id(order.id())
            .date(order.date())
            .total(order.total())
            .build();
    }
}