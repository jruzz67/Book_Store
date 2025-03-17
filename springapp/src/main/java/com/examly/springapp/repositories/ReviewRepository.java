package com.examly.springapp.repositories;

import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBookAndApprovedTrue(Book book, Pageable pageable);
}