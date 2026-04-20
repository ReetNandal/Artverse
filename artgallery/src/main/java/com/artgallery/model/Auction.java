package com.artgallery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "artwork_id")
    private Long artworkId;

    @Column(name = "seller_id")
    private Long sellerId;

    private String title;
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "current_bid")
    private Double currentBid;

    @Column(name = "bid_increment")
    private Double bidIncrement;

    private String status;

    @Column(name = "reserve_met")
    private Boolean reserveMet;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private String artworkImageUrl;

    @Transient
    private String artworkCategory;

    @Transient
    private String artworkTitle;

    @Transient
    private String artistName;

    @Transient
    private String artworkDescription;

    @Transient
    private Double basePrice;

    @Transient
    private Double estimatedPrice;

    @Transient
    private Long totalBids;

    @Transient
    private Long endTimeMillis;
    @Transient
    private String technique;

    @Transient
    private Integer yearMade;

    @Transient
    private String sizeDimension;
    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @Column(name = "payment_paid_at")
    private LocalDateTime paymentPaidAt;

    @Transient
    private String resultStatus;

    @Transient
    private String resultButtonText;

    @Transient
    private Double shippingCharge;

    @Transient
    private Double taxAmount;

    @Transient
    private Double totalPayable;

    @Transient
    private String orientation;
    public String getTechnique() { return technique; }
    public void setTechnique(String technique) { this.technique = technique; }

    public Integer getYearMade() { return yearMade; }
    public void setYearMade(Integer yearMade) { this.yearMade = yearMade; }

    public String getSizeDimension() { return sizeDimension; }
    public void setSizeDimension(String sizeDimension) { this.sizeDimension = sizeDimension; }

    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Double getCurrentBid() { return currentBid; }
    public void setCurrentBid(Double currentBid) { this.currentBid = currentBid; }

    public Double getBidIncrement() { return bidIncrement; }
    public void setBidIncrement(Double bidIncrement) { this.bidIncrement = bidIncrement; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getReserveMet() { return reserveMet; }
    public void setReserveMet(Boolean reserveMet) { this.reserveMet = reserveMet; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getArtworkImageUrl() { return artworkImageUrl; }
    public void setArtworkImageUrl(String artworkImageUrl) { this.artworkImageUrl = artworkImageUrl; }

    public String getArtworkCategory() { return artworkCategory; }
    public void setArtworkCategory(String artworkCategory) { this.artworkCategory = artworkCategory; }

    public String getArtworkTitle() { return artworkTitle; }
    public void setArtworkTitle(String artworkTitle) { this.artworkTitle = artworkTitle; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getArtworkDescription() { return artworkDescription; }
    public void setArtworkDescription(String artworkDescription) { this.artworkDescription = artworkDescription; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(Double estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public Long getTotalBids() { return totalBids; }
    public void setTotalBids(Long totalBids) { this.totalBids = totalBids; }

    public Long getEndTimeMillis() { return endTimeMillis; }
    public void setEndTimeMillis(Long endTimeMillis) { this.endTimeMillis = endTimeMillis; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaymentDeadline() { return paymentDeadline; }
    public void setPaymentDeadline(LocalDateTime paymentDeadline) { this.paymentDeadline = paymentDeadline; }

    public LocalDateTime getPaymentPaidAt() { return paymentPaidAt; }
    public void setPaymentPaidAt(LocalDateTime paymentPaidAt) { this.paymentPaidAt = paymentPaidAt; }

    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }

    public String getResultButtonText() { return resultButtonText; }
    public void setResultButtonText(String resultButtonText) { this.resultButtonText = resultButtonText; }

    public Double getShippingCharge() { return shippingCharge; }
    public void setShippingCharge(Double shippingCharge) { this.shippingCharge = shippingCharge; }

    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }

    public Double getTotalPayable() { return totalPayable; }
    public void setTotalPayable(Double totalPayable) { this.totalPayable = totalPayable; }

}