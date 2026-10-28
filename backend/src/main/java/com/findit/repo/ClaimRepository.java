package com.findit.repo;
import com.findit.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClaimRepository extends JpaRepository<Claim, Long> {
  List<Claim> findByClaimantIdOrderByCreatedAtDesc(Long claimantId);
  boolean existsByItemIdAndClaimantIdAndStatus(Long itemId, Long claimantId, Claim.ClaimStatus status);
  List<Claim> findByItemUserIdOrderByCreatedAtDesc(Long ownerId);
}
