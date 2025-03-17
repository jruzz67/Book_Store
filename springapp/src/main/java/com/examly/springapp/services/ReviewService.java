package com.examly.springapp.services;

import com.examly.springapp.entities.Book;
import com.examly.springapp.entities.Review;
import com.examly.springapp.entities.User;
import com.examly.springapp.repositories.BookRepository;
import com.examly.springapp.repositories.ReviewRepository;
import com.examly.springapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public Review createReview(Long bookId, Long userId, int rating, String comment) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setApproved(false);
        review.setCreatedAt(LocalDateTime.now()); // Set the creation timestamp

        return reviewRepository.save(review);
    }

    public Page<Review> getReviewsByBookId(Long bookId, int page, int size, String sortBy) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        Sort sort = Sort.unsorted();
        if ("rating".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("date".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return reviewRepository.findByBookAndApprovedTrue(book, PageRequest.of(page, size, sort));
    }

    public Review moderateReview(Long reviewId, Long userId, boolean approved) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        Book book = review.getBook();
        User author = book.getUser();

        if (!author.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the book's author can approve or reject reviews");
        }

        review.setApproved(approved);
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        User reviewAuthor = review.getUser();
        Book book = review.getBook();
        User bookAuthor = book.getUser();

        if (!reviewAuthor.getId().equals(userId) && !bookAuthor.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the review author or book's author can delete this review");
        }

        reviewRepository.delete(review);
    }
}