package com.findit.config;

import com.findit.model.Item;
import com.findit.model.User;
import com.findit.repo.ItemRepository;
import com.findit.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;

@Configuration
public class SeedData {
  @Bean CommandLineRunner seed(UserRepository users, ItemRepository items, PasswordEncoder encoder) {
    return args -> {
      User admin=users.findByEmailIgnoreCase("alkesh6420@gmail.com").orElseGet(()->{
        User u=new User(); u.name="FindIt Admin"; u.email="alkesh6420@gmail.com"; u.passwordHash=encoder.encode("Choudhary"); u.role=User.Role.ADMIN; return users.save(u);
      });
      User demo=users.findByEmailIgnoreCase("demo@findit.local").orElseGet(()->{
        User u=new User(); u.name="Demo User"; u.email="demo@findit.local"; u.passwordHash=encoder.encode("Demo123!"); u.role=User.Role.USER; return users.save(u);
      });
      if(items.count()==0) {
        Item a=new Item(); a.user=demo; a.type=Item.ItemType.LOST; a.title="Black wallet"; a.category="Wallet"; a.description="Black leather wallet with a few cards. Please keep card details private."; a.color="Black"; a.locationLabel="Central bus stand"; a.eventDate=LocalDate.now().minusDays(1); items.save(a);
        Item b=new Item(); b.user=demo; b.type=Item.ItemType.FOUND; b.title="Blue water bottle"; b.category="Accessories"; b.description="Blue reusable bottle found near the library entrance."; b.color="Blue"; b.locationLabel="City library"; b.eventDate=LocalDate.now(); items.save(b);
        Item c=new Item(); c.user=admin; c.type=Item.ItemType.FOUND; c.title="Keys with red keychain"; c.category="Keys"; c.description="A small bunch of keys with a red keychain. Claimants should describe the hidden key details."; c.color="Silver / red"; c.locationLabel="Community park"; c.eventDate=LocalDate.now().minusDays(2); items.save(c);
      }
    };
  }
}
