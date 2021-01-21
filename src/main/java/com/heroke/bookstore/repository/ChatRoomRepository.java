package com.heroke.bookstore.repository;

import com.heroke.bookstore.model.chat.ChatRoom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

    @Query(value = "SELECT * FROM CHAT_ROOM WHERE SENDER_ID =  :id", nativeQuery = true)
    List<ChatRoom> getAllRecipientIDs(@Param("id") Long id);

}
