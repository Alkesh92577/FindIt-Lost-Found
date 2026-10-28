package com.findit.controller;

import com.findit.model.Item;
import com.findit.model.User;
import com.findit.repo.ItemRepository;
import com.findit.service.CurrentUser;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController @RequestMapping("/api/items")
public class ItemController {
  public record ItemRequest(@NotBlank String type, @NotBlank String title, @NotBlank String category, @NotBlank String description, String color, @NotBlank String locationLabel, @NotBlank String eventDate) {}
  private final ItemRepository items; private final CurrentUser currentUser; private final Path uploadPath;
  public ItemController(ItemRepository items, CurrentUser currentUser, @Value("${findit.upload-dir}") String uploadDir) {
    this.items=items; this.currentUser=currentUser; this.uploadPath=Paths.get(uploadDir).toAbsolutePath().normalize();
    try { Files.createDirectories(uploadPath); } catch(Exception e) { throw new RuntimeException(e); }
  }
  @GetMapping public List<Map<String,Object>> list(@RequestParam(required=false) String type, @RequestParam(required=false) String category, @RequestParam(required=false) String q) {
    Item.ItemType t=null;
    if(type!=null && !type.isBlank()) try { t=Item.ItemType.valueOf(type.toUpperCase()); } catch(Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"type must be LOST or FOUND"); }
    return items.search(t, blankToNull(category), blankToNull(q), Item.ItemStatus.HIDDEN).stream().map(this::view).toList();
  }
  @GetMapping("/{id}") public Map<String,Object> get(@PathVariable Long id) { return view(items.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND))); }
  @PostMapping(consumes={"multipart/form-data"}) public Map<String,Object> create(@ModelAttribute ItemRequest r, @RequestParam(required=false) MultipartFile image) {
    User u=currentUser.require(); Item i=new Item(); fill(i,r); i.user=u;
    if(image!=null && !image.isEmpty()) {
      if(image.getSize()>5*1024*1024) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Image must be 5MB or smaller");
      String ct=image.getContentType();
      if(ct==null || !List.of("image/jpeg","image/png","image/webp").contains(ct)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use JPG, PNG or WebP image");
      try { String ext=ct.equals("image/png")?".png":ct.equals("image/webp")?".webp":".jpg"; String name=UUID.randomUUID()+ext; Files.copy(image.getInputStream(),uploadPath.resolve(name),StandardCopyOption.REPLACE_EXISTING); i.imagePath="/uploads/"+name; }
      catch(Exception e) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Image upload failed"); }
    }
    return view(items.save(i));
  }
  @GetMapping("/mine") public List<Map<String,Object>> mine() { return items.findByUserIdOrderByCreatedAtDesc(currentUser.require().id).stream().map(this::view).toList(); }
  private void fill(Item i, ItemRequest r) {
    try { i.type=Item.ItemType.valueOf(r.type().toUpperCase()); i.eventDate=LocalDate.parse(r.eventDate()); }
    catch(Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid type or date"); }
    i.title=r.title().trim(); i.category=r.category().trim(); i.description=r.description().trim(); i.color=r.color(); i.locationLabel=r.locationLabel().trim(); i.status=Item.ItemStatus.OPEN;
  }
  private String blankToNull(String s) { return s==null||s.isBlank()?null:s.trim(); }
  private Map<String,Object> view(Item i) {
    Map<String,Object> m=new LinkedHashMap<>(); m.put("id",i.id); m.put("type",i.type); m.put("title",i.title); m.put("category",i.category); m.put("description",i.description); m.put("color",i.color); m.put("locationLabel",i.locationLabel); m.put("eventDate",i.eventDate); m.put("imagePath",i.imagePath); m.put("status",i.status); m.put("createdAt",i.createdAt); m.put("reporterName",i.user.name); return m;
  }
}
