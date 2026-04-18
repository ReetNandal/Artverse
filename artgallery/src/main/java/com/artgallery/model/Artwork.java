package com.artgallery.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "artworks")
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "artist_name")
    private String artistName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "estimated_price")
    private BigDecimal estimatedPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "technique")
    private String technique;

    @Column(name = "year_made")
    private Integer yearMade;

    @Column(name = "size_dimension")
    private String sizeDimension;

    @Column(name = "orientation")
    private String orientation;

    public String getTechnique() {
        return technique;
    }

    public Integer getYearMade() {
        return yearMade;
    }

    public String getSizeDimension() {
        return sizeDimension;
    }

    public String getOrientation() {
        return orientation;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public void setYearMade(Integer yearMade) {
        this.yearMade = yearMade;
    }

    public void setSizeDimension(String sizeDimension) {
        this.sizeDimension = sizeDimension;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}