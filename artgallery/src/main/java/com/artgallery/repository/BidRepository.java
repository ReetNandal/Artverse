package com.artgallery.repository;

import com.artgallery.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findTop5ByAuction_IdOrderByBidTimeDesc(Long auctionId);
    List<Bid> findTop20ByAuction_IdOrderByBidTimeDesc(Long auctionId);

    long countByAuction_Id(Long auctionId);
    long countByUser_Id(Long userId);
    List<Bid> findByAuction_IdAndUser_IdOrderByBidTimeDesc(Long auctionId, Long userId);
    // ✅ winner = highest/last bid (most recent high bid)
    Optional<Bid> findTop1ByAuction_IdOrderByAmountDescBidTimeDesc(Long auctionId);
}