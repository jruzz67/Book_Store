package com.examly.springapp.controllers;

import com.examly.springapp.entities.Review;
import com.examly.springapp.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @PathVariable Long bookId,
            @RequestParam Long userId,
            @RequestParam int rating,
            @RequestParam String comment) {
        Review createdReview = reviewService.createReview(bookId, userId, rating, comment);
        return ResponseEntity.status(201).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<Page<Review>> getReviewsByBookId(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy) {
        Page<Review> reviews = reviewService.getReviewsByBookId(bookId, page, size, sortBy);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}/moderate")
    public ResponseEntity<Review> moderateReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @RequestParam boolean approved) {
        Review review = reviewService.moderateReview(reviewId, userId, approved);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}