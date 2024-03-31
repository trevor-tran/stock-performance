package com.trevortran.stockcomparator.model;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class News {
    @Id
    UUID id;
    String ticker;
    String title;
    String url;
    LocalDate publishedDate;
    String summary;
    String imageUrl;
    double relevanceScore;
}
