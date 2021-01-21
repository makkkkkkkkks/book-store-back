package com.heroke.bookstore.service;

import com.heroke.bookstore.model.Book;
import com.heroke.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    public final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllUserBooks(Long id) {
        System.out.println("BOOK - > " + bookRepository.findAllByBookOwner(id));
        return bookRepository.findAllByBookOwner(id);
    }
}
