// OrderItemRepository.java
package store.order;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItemModel, String> {
    
    List<OrderItemModel> findByIdOrder(String idOrder);
    
}