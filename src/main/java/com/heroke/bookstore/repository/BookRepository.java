package com.heroke.bookstore.repository;


import com.heroke.bookstore.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    @Query(value = "SELECT * FROM BOOKS WHERE USERS_ID =  :id", nativeQuery = true)
    List<Book> findAllByBookOwner(@Param("id") Long id);

    List<Book> findBookByBookAuthor(String bookAuthor);

    Optional<Book> findById(Long id);


}
