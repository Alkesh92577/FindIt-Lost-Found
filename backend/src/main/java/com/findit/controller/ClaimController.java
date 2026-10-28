package com.findit.controller;

import com.findit.model.Claim;
import com.findit.model.Item;
import com.findit.repo.ClaimRepository;
import com.findit.repo.ItemRepository;
import com.findit.service.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
public class ClaimController {
  public record ClaimRequest(@NotBlank String message, @NotBlank String proofDetails) {}
  private final ClaimRepository claims; private final ItemRepository items; private final CurrentUser currentUser;
  public ClaimController(ClaimRepository claims, ItemRepository items, CurrentUser currentUser) { this.claims=claims; this.items=items; this.currentUser=currentUser; }
  @PostMapping("/api/items/{id}/claims") public Map<String,Object> create(@PathVariable Long id, @Valid @RequestBody ClaimRequest r) {
    var user=currentUser.require(); Item item=items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    if(item.user.id.equals(user.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot claim your own listing");
    if(item.status==Item.ItemStatus.RETURNED || item.status==Item.ItemStatus.CLOSED || item.status==Item.ItemStatus.HIDDEN) throw new ResponseStatusException(HttpStatus.CONFLICT,"This listing is not accepting claims");
    if(claims.existsByItemIdAndClaimantIdAndStatus(id,user.id,Claim.ClaimStatus.PENDING)) throw new ResponseStatusException(HttpStatus.CONFLICT,"You already have a pending claim");
    Claim c=new Claim(); c.item=item; c.claimant=user; c.message=r.message().trim(); c.proofDetails=r.proofDetails().trim(); c.status=Claim.ClaimStatus.PENDING;
    item.status=Item.ItemStatus.CLAIM_PENDING; items.save(item); return view(claims.save(c));
  }
  @GetMapping("/api/me/claims") public List<Map<String,Object>> mine() { return claims.findByClaimantIdOrderByCreatedAtDesc(currentUser.require().id).stream().map(this::view).toList(); }

  @GetMapping("/api/me/incoming-claims") public List<Map<String,Object>> incoming() {
    var user=currentUser.require();
    return claims.findByItemUserIdOrderByCreatedAtDesc(user.id).stream().map(c -> {
      Map<String,Object> m=new LinkedHashMap<>(view(c)); m.put("claimantName",c.claimant.name); m.put("claimantId",c.claimant.id); return m;
    }).toList();
  }
  @PatchMapping("/api/claims/{id}/decision") public Map<String,Object> decide(@PathVariable Long id, @Valid @RequestBody DecisionRequest r) {
    var user=currentUser.require(); Claim c=claims.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Claim not found"));
    if(!c.item.user.id.equals(user.id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the finder can review this claim");
    if(c.status!=Claim.ClaimStatus.PENDING) throw new ResponseStatusException(HttpStatus.CONFLICT,"Claim has already been reviewed");
    if(!r.status().equals("APPROVED") && !r.status().equals("REJECTED")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Decision must be APPROVED or REJECTED");
    c.status=Claim.ClaimStatus.valueOf(r.status());
    if(c.status==Claim.ClaimStatus.APPROVED) c.item.status=Item.ItemStatus.MATCHED;
    else if(c.item.status==Item.ItemStatus.CLAIM_PENDING) c.item.status=Item.ItemStatus.OPEN;
    items.save(c.item); claims.save(c); return view(c);
  }
  public record DecisionRequest(@NotBlank String status) {}
  private Map<String,Object> view(Claim c) { return Map.of("id",c.id,"itemId",c.item.id,"itemTitle",c.item.title,"message",c.message,"proofDetails",c.proofDetails,"status",c.status,"createdAt",c.createdAt); }
}
