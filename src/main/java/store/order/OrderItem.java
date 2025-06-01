// OrderItem.java
package store.order;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Data 
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id;
    private String idOrder;
    private String idProduct;
    private Integer quantity;
    private Double total;
    
}