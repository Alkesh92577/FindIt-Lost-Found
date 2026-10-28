package com.findit.controller;

import com.findit.model.ChatMessage;
import com.findit.model.Claim;
import com.findit.repo.ChatMessageRepository;
import com.findit.repo.ClaimRepository;
import com.findit.service.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/claims/{claimId}/messages")
public class ChatController {
  private final ClaimRepository claims; private final ChatMessageRepository messages; private final CurrentUser currentUser;
  public ChatController(ClaimRepository claims, ChatMessageRepository messages, CurrentUser currentUser) { this.claims=claims; this.messages=messages; this.currentUser=currentUser; }
  private Claim permitted(Long id) {
    var u=currentUser.require(); Claim c=claims.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Claim not found"));
    if(!c.item.user.id.equals(u.id) && !c.claimant.id.equals(u.id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This conversation is not yours");
    if(c.status!=Claim.ClaimStatus.APPROVED) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Chat is available only after claim approval");
    return c;
  }
  @GetMapping public List<Map<String,Object>> list(@PathVariable Long claimId) {
    permitted(claimId); return messages.findByClaimIdOrderByCreatedAtAsc(claimId).stream().map(this::view).toList();
  }
  public record SendRequest(@NotBlank @Size(max=2000) String content) {}
  @PostMapping public Map<String,Object> send(@PathVariable Long claimId, @Valid @RequestBody SendRequest r) {
    Claim c=permitted(claimId); var u=currentUser.require(); ChatMessage m=new ChatMessage(); m.claim=c; m.sender=u; m.content=r.content().trim();
    if(m.content.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Message cannot be empty");
    return view(messages.save(m));
  }
  private Map<String,Object> view(ChatMessage m) { Map<String,Object> x=new LinkedHashMap<>(); x.put("id",m.id); x.put("claimId",m.claim.id); x.put("senderId",m.sender.id); x.put("senderName",m.sender.name); x.put("content",m.content); x.put("createdAt",m.createdAt); return x; }
}
