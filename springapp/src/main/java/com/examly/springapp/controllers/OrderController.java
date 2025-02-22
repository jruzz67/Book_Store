package com.examly.springapp.controllers;

import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping
    public ResponseEntity<Ordertable> createOrder(@RequestBody Ordertable order) {
        Ordertable createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Ordertable>> getOrderById(@PathVariable Long id) {
        Optional<Ordertable> order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    @GetMapping
    public ResponseEntity<List<Ordertable>> getAllOrders() {
        List<Ordertable> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Ordertable> updateOrder(@PathVariable Long id, @RequestBody Ordertable orderDetails) {
        Ordertable updatedOrder = orderService.updateOrder(id, orderDetails);
        return ResponseEntity.ok(updatedOrder);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}