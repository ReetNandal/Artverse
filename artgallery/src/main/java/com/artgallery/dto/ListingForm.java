package com.artgallery.dto;

import java.time.LocalDate;

public class ListingForm {

    private String title;
    private String imageUrl;
    private String category;
    private String description;
    private Double startingBid;
    private Double bidIncrement;
    private Double reservePrice;
    private LocalDate startDate;
    private Integer durationDays;
    private String artistName;
    private String technique;
    private Integer yearMade;
    private String sizeDimension;
    private String orientation;

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getTechnique() { return technique; }
    public void setTechnique(String technique) { this.technique = technique; }

    public Integer getYearMade() { return yearMade; }
    public void setYearMade(Integer yearMade) { this.yearMade = yearMade; }

    public String getSizeDimension() { return sizeDimension; }
    public void setSizeDimension(String sizeDimension) { this.sizeDimension = sizeDimension; }

    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getStartingBid() { return startingBid; }
    public void setStartingBid(Double startingBid) { this.startingBid = startingBid; }

    public Double getBidIncrement() { return bidIncrement; }
    public void setBidIncrement(Double bidIncrement) { this.bidIncrement = bidIncrement; }

    public Double getReservePrice() { return reservePrice; }
    public void setReservePrice(Double reservePrice) { this.reservePrice = reservePrice; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
}