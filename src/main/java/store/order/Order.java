// Order.java
package store.order;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String id;
    private String idAccount;
    private LocalDateTime date;
    private List<OrderItem> items;
    private Double total;
    
}