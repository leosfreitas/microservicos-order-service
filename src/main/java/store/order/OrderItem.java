// OrderItem.java
package store.order;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@Data @Accessors(fluent = true)
public class OrderItem {

    private String id;
    private String idOrder;
    private String idProduct;
    private Integer quantity;
    private Double total;
    
}