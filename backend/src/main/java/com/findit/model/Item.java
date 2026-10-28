package com.findit.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="items")
public class Item {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="user_id", nullable=false) public User user;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=8) public ItemType type;
  @Column(nullable=false, length=140) public String title;
  @Column(nullable=false, length=60) public String category;
  @Column(nullable=false, columnDefinition="TEXT") public String description;
  @Column(length=60) public String color;
  @Column(name="location_label", nullable=false, length=180) public String locationLabel;
  @Column(name="event_date", nullable=false) public LocalDate eventDate;
  @Column(name="image_path", length=500) public String imagePath;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) public ItemStatus status = ItemStatus.OPEN;
  @Column(name="created_at", nullable=false) public LocalDateTime createdAt = LocalDateTime.now();
  public enum ItemType { LOST, FOUND }
  public enum ItemStatus { OPEN, MATCHED, CLAIM_PENDING, RETURNED, CLOSED, HIDDEN }
}
