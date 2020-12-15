package br.com.kopzinski.eventsourcing.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FileCreatedEvent extends Event {

    private String fileName;

}
