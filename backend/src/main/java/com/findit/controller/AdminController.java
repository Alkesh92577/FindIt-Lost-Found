package com.findit.controller;

import com.findit.model.Item;
import com.findit.repo.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController @RequestMapping("/api/admin")
public class AdminController {
  private final ItemRepository items;
  public AdminController(ItemRepository items) { this.items=items; }
  @GetMapping("/items") public java.util.List<Item> all() { return items.findAll(); }
  public record StatusRequest(String status) {}
  @PatchMapping("/items/{id}/status") public Map<String,Object> status(@PathVariable Long id, @RequestBody StatusRequest r) {
    Item i=items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    try { i.status=Item.ItemStatus.valueOf(r.status().toUpperCase()); }
    catch(Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid status"); }
    items.save(i); return Map.of("id",i.id,"status",i.status);
  }
}
