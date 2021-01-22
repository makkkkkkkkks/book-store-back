package com.heroke.bookstore.controller;

import com.heroke.bookstore.exception.ResourceNotFoundException;
import com.heroke.bookstore.model.User;
import com.heroke.bookstore.payload.UserSummary;
import com.heroke.bookstore.repository.UserRepository;
import com.heroke.bookstore.security.CurrentUser;
import com.heroke.bookstore.security.UserPrincipal;
import com.heroke.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    @GetMapping("/user/allUsers/{name}")
    @PreAuthorize("hasRole('USER')")
    public List<User> getAllUsers(@PathVariable String name) {
        return userService.getAllUsers(name);
    }

    @GetMapping("/messages/get_active_room/")
    public ResponseEntity<?> getActiveRoom(@CurrentUser UserPrincipal userPrincipal) {
        System.out.println("User id is () -> " + userPrincipal.getId() );
        List<Optional<User>> users = userService.getAllUsersWhoStartChat(userPrincipal.getId());
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/users/summaries", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllUserSummaries(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService
                .findAll()
                .stream()
                .filter(user -> !user.getName().equals(userPrincipal.getUsername()))
                .map(this::convertTo));
    }

    private UserSummary convertTo(User user) {
        return UserSummary
                .builder()
                .id(user.getId())
                .username(user.getName())
                .name(user.getName())
                .profilePicture(user.getImageUrl())
                .build();
    }
}
