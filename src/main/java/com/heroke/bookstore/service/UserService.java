package com.heroke.bookstore.service;

import com.heroke.bookstore.model.User;
import com.heroke.bookstore.model.chat.ChatRoom;
import com.heroke.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;

    @Autowired
    public UserService(UserRepository userRepository, ChatRoomService chatRoomService) {
        this.userRepository = userRepository;
        this.chatRoomService = chatRoomService;
    }

    public List<User> getAllUsers(String name) {
        List<User> users = userRepository.getAllByName(name);
        return users;
    }

    public List<Optional<User>> getAllUsersWhoStartChat(Long id) {
        List<ChatRoom> chatRooms = chatRoomService.getAllActiveChatRoomById(id);
        List<Optional<User>> users = new ArrayList<>();
        Long recipientId;
        for (int i = 0; i < chatRooms.size(); i++) {
            recipientId = Long.parseLong(chatRooms.get(i).getRecipientId());
            users.add(userRepository.findById(recipientId));
        }
        return users;
    }
    public List<User> findAll(){
        return userRepository.findAll();
    }
}
