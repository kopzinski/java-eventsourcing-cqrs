package br.com.kopzinski.eventsourcing.domain;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Item {

    private String id;
    private Long qty;
    private Double price;

}
