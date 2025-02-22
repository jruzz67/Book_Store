package com.examly.springapp.services;

import com.examly.springapp.entities.Ordertable;
import com.examly.springapp.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
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
        Optional<Ordertable> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Ordertable order = existingOrder.get();
            order.setOrderDate(orderDetails.getOrderDate());
            order.setTotalAmount(orderDetails.getTotalAmount());
            order.setStatus(orderDetails.getStatus());
            return orderRepository.save(order);
        }
        return null;
    }
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
