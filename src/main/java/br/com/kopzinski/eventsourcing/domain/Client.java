package br.com.kopzinski.eventsourcing.domain;
import lombok.*;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Client extends Record {

    @EqualsAndHashCode.Include
    private String cnpj;
    private String name;
    private String businessArea;

    @Builder
    public Client(String typeIdentifier, String cnpj, String name, String businessArea) {
        super(typeIdentifier);
        this.cnpj = cnpj;
        this.name = name;
        this.businessArea = businessArea;
    }
}
