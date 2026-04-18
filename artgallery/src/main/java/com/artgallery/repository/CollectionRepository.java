package com.artgallery.repository;

import com.artgallery.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<CollectionItem, Long> {

    boolean existsByAuctionId(Long auctionId);

    List<CollectionItem> findByUserIdOrderByIdDesc(Long userId);
}