package store.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderResource implements OrderController {

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<OrderOut> create(OrderIn orderIn, String idAccount) {
        OrderOut created = orderService.create(orderIn, idAccount);
        return ResponseEntity.created(null).body(created);
    }

    @Override
    public ResponseEntity<List<OrderSummaryOut>> findAll(String idAccount) {
        return ResponseEntity
            .ok()
            .body(orderService.findAllByAccount(idAccount));
    }
    
    @Override
    public ResponseEntity<OrderOut> findById(String id, String idAccount) {
        return ResponseEntity
            .ok()
            .body(orderService.findByIdAndAccount(id, idAccount));
    }
}