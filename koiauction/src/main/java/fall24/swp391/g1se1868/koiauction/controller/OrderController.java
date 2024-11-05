
package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<?> addOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            Order order = orderService.addOrder(orderRequest.getAuctionID(), orderRequest, userId);
            if(order != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse("Add order successful"));
            }else {
                return ResponseEntity.ok(new StringResponse("Add order failed"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable Integer orderId,
            @RequestBody OrderRequest orderRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            Order updatedOrder = orderService.updateOrder(orderId, orderRequest, userId);
            return ResponseEntity.ok(new StringResponse("Order updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }


    @GetMapping()
    public List<OrderResponse> getOrdersByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return orderService.getOrdersByUser(userId);
    }
}

