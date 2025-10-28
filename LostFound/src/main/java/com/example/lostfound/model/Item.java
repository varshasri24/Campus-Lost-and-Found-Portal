package com.example.lostfound.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LOST or FOUND
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private String location;
    private String posterName;
    private String posterContact;

    // saved image path like "uploads/123_filename.jpg"
    private String imagePath;

    // small image hash for similarity
    private String imageHash;

    private LocalDateTime createdAt;

    public Item() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    // (IDE can generate but include manually if needed)
    // ... (include all standard getters/setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPosterName() { return posterName; }
    public void setPosterName(String posterName) { this.posterName = posterName; }

    public String getPosterContact() { return posterContact; }
    public void setPosterContact(String posterContact) { this.posterContact = posterContact; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getImageHash() { return imageHash; }
    public void setImageHash(String imageHash) { this.imageHash = imageHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
