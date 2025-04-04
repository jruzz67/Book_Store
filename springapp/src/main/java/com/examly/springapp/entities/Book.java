package com.examly.springapp.entities;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String description;
    private Double price;

    @ManyToOne
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    private Integer numberOfReviews;
    private Double averageRating;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public Integer getNumberOfReviews() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        return (int) reviews.stream()
                .filter(Review::isApproved)
                .count();
    }

    public void setNumberOfReviews(Integer numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .filter(Review::isApproved)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public void recalculateMetrics() {
        this.numberOfReviews = getNumberOfReviews();
        this.averageRating = getAverageRating();
    }
}