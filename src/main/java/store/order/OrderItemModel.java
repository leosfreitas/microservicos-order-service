// OrderItemModel.java
package store.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "order_item")
@Setter @Accessors(fluent = true)
@NoArgsConstructor
public class OrderItemModel {

    @Id
    @Column(name = "id_order_item")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "id_order")
    private String idOrder;

    @Column(name = "id_product")
    private String idProduct;

    @Column(name = "num_quantity")
    private Integer quantity;

    @Column(name = "db_total")
    private Double total;

    public OrderItemModel(OrderItem item) {
        this.id = item.id();
        this.idOrder = item.idOrder();
        this.idProduct = item.idProduct();
        this.quantity = item.quantity();
        this.total = item.total();
    }

    public OrderItem to() {
        return OrderItem.builder()
            .id(this.id)
            .idOrder(this.idOrder)
            .idProduct(this.idProduct)
            .quantity(this.quantity)
            .total(this.total)
            .build();
    }
}