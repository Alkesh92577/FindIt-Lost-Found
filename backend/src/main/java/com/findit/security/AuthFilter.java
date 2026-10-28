package com.findit.security;

import com.findit.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class AuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserRepository users;
  public AuthFilter(JwtService jwt, UserRepository users) { this.jwt=jwt; this.users=users; }
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String header=req.getHeader("Authorization");
    if(header!=null && header.startsWith("Bearer ")) {
      try {
        String email=jwt.subject(header.substring(7));
        users.findByEmailIgnoreCase(email).ifPresent(user -> {
          var auth=new UsernamePasswordAuthenticationToken(user.email, null, List.of(new SimpleGrantedAuthority("ROLE_"+user.role.name())));
          SecurityContextHolder.getContext().setAuthentication(auth);
        });
      } catch(Exception ignored) { SecurityContextHolder.clearContext(); }
    }
    chain.doFilter(req,res);
  }
}
