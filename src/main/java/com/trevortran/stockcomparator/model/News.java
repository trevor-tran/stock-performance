package com.trevortran.stockcomparator.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(indexes = @Index(columnList = "ticker, publishedDate, relevanceScore"))
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String ticker;
    @Column(columnDefinition="text")
    String title;
    @Column(unique=true, columnDefinition="text")
    String url;
    LocalDate publishedDate;
    @Column(columnDefinition="text")
    String summary;
    String imageUrl;
    double relevanceScore;

    public static class Builder {
        private String ticker;
        private String title;
        private String url;
        private LocalDate publishedDate;
        private String summary;
        private String imageUrl;
        private double relevanceScore;

        public Builder ticker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder publishedDate(LocalDate publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder relevanceScore(double relevanceScore) {
            this.relevanceScore = relevanceScore;
            return this;
        }

        public News build() {
            return new News(this);
        }
    }
    private News(Builder builder) {
        title = builder.title;
        ticker = builder.ticker;
        url = builder.url;
        publishedDate = builder.publishedDate;
        summary = builder.summary;
        imageUrl = builder.imageUrl;
        relevanceScore = builder.relevanceScore;
    }
}
