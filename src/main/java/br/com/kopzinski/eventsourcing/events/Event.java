package br.com.kopzinski.eventsourcing.events;

import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class Event {

    private final UUID id = UUID.randomUUID();

    private final Date created = new Date();

}
