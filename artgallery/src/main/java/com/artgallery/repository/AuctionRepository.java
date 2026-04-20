package com.artgallery.repository;

import com.artgallery.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByStatus(String status);

    @Query(value = """
            SELECT a.*
            FROM auctions a
            WHERE NOW() BETWEEN a.start_time AND a.end_time
            """, nativeQuery = true)
    List<Auction> findLiveAuctions();

    @Query(value = """
            SELECT a.*
            FROM auctions a
            WHERE a.start_time > NOW()
            """, nativeQuery = true)
    List<Auction> findUpcomingAuctions();

    @Query(value = """
            SELECT a.*
            FROM auctions a
            WHERE a.end_time < NOW()
            """, nativeQuery = true)
    List<Auction> findEndedAuctions();

    @Query(value = """
            SELECT a.*
            FROM auctions a
            JOIN artworks aw ON a.artwork_id = aw.id
            WHERE NOW() BETWEEN a.start_time AND a.end_time
              AND aw.category = :category
            """, nativeQuery = true)
    List<Auction> findLiveAuctionsByCategory(@Param("category") String category);

    @Query(value = """
            SELECT a.*
            FROM auctions a
            JOIN artworks aw ON a.artwork_id = aw.id
            WHERE a.start_time > NOW()
              AND aw.category = :category
            """, nativeQuery = true)
    List<Auction> findUpcomingAuctionsByCategory(@Param("category") String category);

    @Query(value = """
            SELECT a.*
            FROM auctions a
            JOIN artworks aw ON a.artwork_id = aw.id
            WHERE a.end_time < NOW()
              AND aw.category = :category
            """, nativeQuery = true)
    List<Auction> findEndedAuctionsByCategory(@Param("category") String category);

    @Query(value = """
            SELECT a.*
            FROM auctions a
            JOIN artworks aw ON a.artwork_id = aw.id
            WHERE aw.category = :category
            """, nativeQuery = true)
    List<Auction> findByCategory(@Param("category") String category);

    @Query(value = """
        SELECT aw.category, COALESCE(SUM(a.current_bid), 0) AS total_volume
        FROM auctions a
        JOIN artworks aw ON a.artwork_id = aw.id
        WHERE NOW() BETWEEN a.start_time AND a.end_time
        GROUP BY aw.category
        ORDER BY total_volume DESC
        """, nativeQuery = true)
    List<Object[]> getLiveAuctionVolumeByCategory();

    long countByWinnerId(@Param("winnerId") Long winnerId);

    @Query(value = """
        SELECT a.*
        FROM auctions a
        WHERE a.winner_id = :winnerId
          AND a.end_time < NOW()
        ORDER BY a.end_time DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Auction> findRecentWins(@Param("winnerId") Long winnerId,
                                 @Param("limit") int limit);

    @Query(value = """
        SELECT a.*
        FROM auctions a
        WHERE a.end_time >= NOW()
        ORDER BY a.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Auction> findActiveAuctionsLimited(@Param("limit") int limit);

    @Query(value = """
        SELECT COUNT(*)
        FROM auctions a
        WHERE a.end_time >= NOW()
        """, nativeQuery = true)
    long countActiveAuctions();


    @Query(value = """
        SELECT a.*
        FROM auctions a
        WHERE a.seller_id = :sellerId
        ORDER BY a.created_at DESC
        """, nativeQuery = true)
    List<Auction> findBySellerIdOrderByCreatedAtDesc(@Param("sellerId") Long sellerId);
    @Query(value = """
    SELECT a.*
    FROM auctions a
    WHERE a.seller_id = :sellerId
    ORDER BY a.id DESC
    """, nativeQuery = true)
    List<Auction> findBySellerIdOrderByIdDesc(@Param("sellerId") Long sellerId);
    @Query(value = """
        SELECT COUNT(*)
        FROM auctions a
        WHERE a.seller_id = :sellerId
        """, nativeQuery = true)
    long countBySellerId(@Param("sellerId") Long sellerId);

    @Query(value = """
        SELECT a.*
        FROM auctions a
        WHERE a.seller_id = :sellerId
          AND a.end_time >= NOW()
        ORDER BY a.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Auction> findActiveAuctionsBySellerLimited(@Param("sellerId") Long sellerId,
                                                    @Param("limit") int limit);
    @Query(value = """
    SELECT a.*
    FROM auctions a
    WHERE a.winner_id = :winnerId
      AND a.end_time < NOW()
    ORDER BY a.end_time DESC
    """, nativeQuery = true)
    List<Auction> findAllWonAuctions(@Param("winnerId") Long winnerId);

    @Query(value = """
    SELECT a.*
    FROM auctions a
    WHERE a.id = :auctionId
      AND a.winner_id = :winnerId
    LIMIT 1
    """, nativeQuery = true)
    Optional<Auction> findWonAuctionByIdAndWinnerId(@Param("auctionId") Long auctionId,
                                                    @Param("winnerId") Long winnerId);

}