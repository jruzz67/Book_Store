package com.examly.springapp.repositories;

import com.examly.springapp.entities.Ordertable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Ordertable, Long> {
}
