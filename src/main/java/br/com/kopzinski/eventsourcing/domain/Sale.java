package br.com.kopzinski.eventsourcing.domain;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sale extends Record {

    @EqualsAndHashCode.Include
    private String id;

    private String sellerName;
    private Double totalPrice;
    private Set<Item> items = new HashSet<>();

    @Builder
    public Sale(String typeIdentifier, String id, String sellerName, Set<Item> items, Double totalPrice) {
        super(typeIdentifier);
        this.id = id;
        this.sellerName = sellerName;
        this.items = items;
        this.totalPrice = totalPrice;
    }
}
