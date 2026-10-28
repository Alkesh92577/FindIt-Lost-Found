package com.findit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="chat_messages", indexes={@Index(name="idx_chat_claim_created", columnList="claim_id,created_at")})
public class ChatMessage {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="claim_id", nullable=false) public Claim claim;
  @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="sender_id", nullable=false) public User sender;
  @Column(nullable=false, length=2000) public String content;
  @Column(name="created_at", nullable=false) public LocalDateTime createdAt=LocalDateTime.now();
}
