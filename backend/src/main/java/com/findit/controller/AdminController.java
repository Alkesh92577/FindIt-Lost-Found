
package com.findit.controller;

import com.findit.model.Item;
import com.findit.repo.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ItemRepository items;

    public AdminController(ItemRepository items) {
        this.items = items;
    }

    @GetMapping("/items")
    public List<Item> all() {
        return items.findAll();
    }

    public record StatusRequest(String status) {}

    @PatchMapping("/items/{id}/status")
    public Map<String, Object> status(
            @PathVariable Long id,
            @RequestBody StatusRequest r) {

        Item item = items.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item not found"));

        try {
            item.status = Item.ItemStatus.valueOf(
                    r.status().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid status");
        }

        items.save(item);

        return Map.of("id", item.id, "status", item.status);
    }

    @DeleteMapping("/items/{id}")
    @Transactional
    public Map<String, Object> delete(@PathVariable Long id) {

        Item item = items.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item not found"));

        items.delete(item);

        return Map.of(
                "success", true,
                "message", "Listing deleted successfully",
                "id", id
        );
    }
}
