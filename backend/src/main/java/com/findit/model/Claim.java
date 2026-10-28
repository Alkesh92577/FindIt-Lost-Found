package com.findit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="claims")
public class Claim {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="item_id", nullable=false) public Item item;
  @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="claimant_id", nullable=false) public User claimant;
  @Column(nullable=false, length=1500) public String message;
  @Column(name="proof_details", nullable=false, length=1500) public String proofDetails;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=12) public ClaimStatus status = ClaimStatus.PENDING;
  @Column(name="created_at", nullable=false) public LocalDateTime createdAt = LocalDateTime.now();
  public enum ClaimStatus { PENDING, APPROVED, REJECTED, CANCELLED }
}
