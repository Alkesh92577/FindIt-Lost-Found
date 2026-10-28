package com.findit.controller;

import com.findit.model.User;
import com.findit.repo.UserRepository;
import com.findit.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  public record RegisterRequest(@NotBlank @Size(max=100) String name, @Email @NotBlank String email, @NotBlank @Size(min=8,max=72) String password) {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
  public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) { this.users=users; this.encoder=encoder; this.jwt=jwt; }
  @PostMapping("/register") public Map<String,Object> register(@Valid @RequestBody RegisterRequest r) {
    if(users.existsByEmailIgnoreCase(r.email())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
    User u=new User(); u.name=r.name().trim(); u.email=r.email().trim().toLowerCase(); u.passwordHash=encoder.encode(r.password()); u.role=User.Role.USER;
    users.save(u); return response(u);
  }
  @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody LoginRequest r) {
    User u=users.findByEmailIgnoreCase(r.email()).filter(x->encoder.matches(r.password(),x.passwordHash)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
    return response(u);
  }
  @GetMapping("/me") public Map<String,Object> me() {
    var a=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    if(a==null || !(a.getPrincipal() instanceof String email)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    User u=users.findByEmailIgnoreCase(email).orElseThrow(); return userView(u);
  }
  private Map<String,Object> response(User u) { return Map.of("token",jwt.create(u),"user",userView(u)); }
  private Map<String,Object> userView(User u) { return Map.of("id",u.id,"name",u.name,"email",u.email,"role",u.role.name()); }
}
