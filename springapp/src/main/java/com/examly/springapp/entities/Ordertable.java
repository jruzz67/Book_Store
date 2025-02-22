package com.examly.springapp.entities;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ordertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
        name = "order_books",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books;
}