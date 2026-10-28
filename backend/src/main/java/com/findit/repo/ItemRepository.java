package com.findit.repo;
import com.findit.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ItemRepository extends JpaRepository<Item, Long> {
  @Query("select i from Item i where i.status <> :hidden and (:type is null or i.type=:type) and (:category is null or lower(i.category)=lower(:category)) and (:q is null or lower(i.title) like lower(concat('%',:q,'%')) or lower(i.description) like lower(concat('%',:q,'%')) or lower(i.locationLabel) like lower(concat('%',:q,'%'))) order by i.createdAt desc")
  List<Item> search(@Param("type") Item.ItemType type, @Param("category") String category, @Param("q") String q, @Param("hidden") Item.ItemStatus hidden);
  List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);
}
