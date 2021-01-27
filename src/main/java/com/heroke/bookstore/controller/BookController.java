package com.heroke.bookstore.controller;

import com.heroke.bookstore.exception.ResourceNotFoundException;
import com.heroke.bookstore.model.Book;
import com.heroke.bookstore.model.User;
import com.heroke.bookstore.repository.BookRepository;
import com.heroke.bookstore.repository.UserRepository;
import com.heroke.bookstore.security.CurrentUser;
import com.heroke.bookstore.security.UserPrincipal;
import com.heroke.bookstore.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/book")
public class BookController {
    private final BookRepository bookRepository;
    private UserRepository userRepository;
    private BookService bookService;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public BookController(BookRepository bookRepository, UserRepository userRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
    }

    @GetMapping
    public Iterable<Book> booksList() {
        return bookRepository.findAll();
    }

    @GetMapping("/getAllUserBooks")
    public List<Book> getAllUserBooks(@CurrentUser UserPrincipal userPrincipal) {
        System.out.println("Proint all Users books () -> "+bookService.getAllUserBooks(userPrincipal.getId()));
        return bookService.getAllUserBooks(userPrincipal.getId());
    }

    @GetMapping("/getBookById/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book id", "id", id));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/add")
    public Book addBook(@RequestBody Book book, @CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User id", "id", userPrincipal.getId()));
        book.setBookOwner(user);
        return bookRepository.save(book);
    }

    @PutMapping("/updateBookByIdd/{id}")
    public Book updateBook(@PathVariable("id") Book bookFromDB, @RequestBody Book bookFromUser) {
        BeanUtils.copyProperties(bookFromUser, bookFromDB, "id");
        return bookRepository.save(bookFromUser);
    }

    @DeleteMapping("/deleteById/{id}")
    public void deleteBook(@PathVariable("id") Book book) {
        bookRepository.delete(book);
    }
}
