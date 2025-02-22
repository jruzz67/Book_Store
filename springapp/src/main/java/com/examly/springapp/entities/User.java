
package com.examly.springapp.entities;

import javax.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Book> books;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ordertable> orders;
    public List<Book> getBooks() {
        return books;
    }
    public void setBooks(List<Book> books) {
        this.books = books;
        for (Book book : books) {
            book.setUser(this);
        }
    }
}
    