package br.com.kopzinski.eventsourcing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public abstract class Record {

    private String typeIdentifier;

}
