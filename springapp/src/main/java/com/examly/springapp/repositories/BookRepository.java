package com.examly.springapp.repositories;

import com.examly.springapp.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByGenre(String genre);
     @Query("SELECT b FROM Book b WHERE b.title = :title")
         List<Book> findBooksByTitle(@Param("title") String title);
}
