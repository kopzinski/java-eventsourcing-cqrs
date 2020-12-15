package br.com.kopzinski.eventsourcing.domain;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Seller extends Record {

    @EqualsAndHashCode.Include
    private String cpf;

    private String name;
    private Double salary;
    private Double totalSold = 0.0;

    @Builder
    public Seller(String typeIdentifier, String cpf, String name, Double salary) {
        super(typeIdentifier);
        this.cpf = cpf;
        this.name = name;
        this.salary = salary;
    }

    public void setTotalSold(Double totalSold) {
        this.totalSold = totalSold;
    }
}
