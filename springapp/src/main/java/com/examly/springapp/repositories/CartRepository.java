package com.examly.springapp.repositories;

import com.examly.springapp.entities.Cart;
import com.examly.springapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndIsDefaultTrue(User user);
    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndName(User user, String name);
}