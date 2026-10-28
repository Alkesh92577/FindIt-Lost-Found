package com.findit.service;

import com.findit.model.User;
import com.findit.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUser {
  private final UserRepository users;
  public CurrentUser(UserRepository users) { this.users=users; }
  public User require() {
    Authentication a=SecurityContextHolder.getContext().getAuthentication();
    if(a==null || !a.isAuthenticated() || !(a.getPrincipal() instanceof String email)) throw new RuntimeException("Please log in");
    return users.findByEmailIgnoreCase(email).orElseThrow(()->new RuntimeException("User not found"));
  }
}
