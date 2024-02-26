package com.trevortran.stockcomparator.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Symbol {
    @Id
    private String id;
    private String name;
    private String type;
    private String region;
    private String timeZone;
    private String currency;
    private LocalDate lastUpdated;
    @OneToMany(mappedBy = "id.symbol", cascade = CascadeType.ALL)
    private Set<Stock> stocks;
}
