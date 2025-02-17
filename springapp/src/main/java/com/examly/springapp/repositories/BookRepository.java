package com.examly.springapp.repositories;

import com.examly.springapp.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByGenre(String genre);
}
