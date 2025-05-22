// OrderParser.java
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

    // MÉTODO CORRIGIDO - removeu toda a lógica dos items
    public static OrderOut to(Order order, List<OrderItem> items, List<ProductOut> products) {
        if (order == null) return null;
        
        // Como OrderOut não tem mais items, só retorna os dados básicos
        return OrderOut.builder()
            .id(order.id())
            .date(order.date())
            .total(order.total())
            .build();
    }
    
    public static OrderOut toSummary(Order order) {
        if (order == null) return null;
        
        return OrderOut.builder()
            .id(order.id())
            .date(order.date())
            .total(order.total())
            .build();
    }
}