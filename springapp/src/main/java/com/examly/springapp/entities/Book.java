package com.examly.springapp.entities;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private double price;
    public void setUser(User user) {
        throw new UnsupportedOperationException("Unimplemented method 'setUser'");
    }
    public Book getUser() {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }
    public String getUsername() {
        throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
    }
    public String getTitle(){
        return this.title;
    }
    public String getAuthor(){
        return this.author;
    }                                
}
