package br.com.kopzinski.eventsourcing.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LineAddedEvent extends Event {

    private String fileName;
    private String lineString;

}
