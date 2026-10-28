package com.findit.repo;
import com.findit.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
  List<ChatMessage> findByClaimIdOrderByCreatedAtAsc(Long claimId);
}
