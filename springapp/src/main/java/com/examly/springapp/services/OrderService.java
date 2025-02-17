package com.examly.springapp.services;

import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Ordertable createOrder(Ordertable order) {
        return orderRepository.save(order);
    }

    public Optional<Ordertable> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Ordertable> getAllOrders() {
        return orderRepository.findAll();
    }

    public Ordertable updateOrder(Long id, Ordertable orderDetails) {
        return orderRepository.findById(id).map(order -> {
            order.setTotalAmount(orderDetails.getTotalAmount());
            order.setStatus(orderDetails.getStatus());
            return orderRepository.save(order);
        }).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
