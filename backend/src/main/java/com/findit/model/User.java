package com.findit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @Column(nullable=false, length=100) public String name;
  @Column(nullable=false, unique=true, length=190) public String email;
  @Column(name="password_hash", nullable=false) public String passwordHash;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=10) public Role role = Role.USER;
  @Column(name="created_at", nullable=false) public LocalDateTime createdAt = LocalDateTime.now();
  public enum Role { USER, ADMIN }
}
