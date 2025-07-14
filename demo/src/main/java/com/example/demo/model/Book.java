package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String language;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    private String genre;

    @Column(length = 100)
    private String description;

    private String isbn;

    @Column(name = "page_count")
    private Integer pageCount;

    private Double rating;

    @Column(name = "is_new")
    private Boolean isNew;

    @Column(name = "image_path")
    private String imagePath;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "availability_status", nullable = false)
    private String availabilityStatus = "Товар в наличии";

    public void setAvailabilityStatus() {
        if (this.quantity < 1) {
            this.availabilityStatus = "товар закончился";
        } else if (this.quantity < 5) {
            this.availabilityStatus = "осталось мало";
        } else {
            this.availabilityStatus = "в наличии";
        }
    }

    public boolean isLowStock() {
        return quantity >= 1 && quantity < 5;
    }

    public boolean isOutOfStock() {
        return quantity < 1;
    }

}
