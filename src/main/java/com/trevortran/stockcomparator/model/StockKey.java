package com.trevortran.stockcomparator.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockKey implements Serializable {
    @NonNull
    @ManyToOne
    @JoinColumn(name="symbol_id", referencedColumnName = "id")
    private Symbol symbol;
    @NonNull
    private LocalDate date;
}
