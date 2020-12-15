package br.com.kopzinski.eventsourcing.aggregates;

import br.com.kopzinski.eventsourcing.commands.AddFileLineCommand;
import br.com.kopzinski.eventsourcing.commands.CreateFileCommand;
import br.com.kopzinski.eventsourcing.events.*;
import br.com.kopzinski.eventsourcing.repository.EventStore;

import java.util.Arrays;
import java.util.List;

public class FileAggregate {

    private EventStore writeRepository;

    public FileAggregate(EventStore repository) {
        this.writeRepository = repository;
    }

    public List<Event> handleCreateFileCommand(CreateFileCommand command) {
        FileCreatedEvent fileCreatedEvent = new FileCreatedEvent(command.getFileName());
        writeRepository.addEvent(command.getFileName(), fileCreatedEvent);
        return Arrays.asList(fileCreatedEvent);
    }

    public List<Event> handleAddFileLineCommand(AddFileLineCommand command) {
        LineAddedEvent lineAddedEvent = new LineAddedEvent(command.getFileName(), command.getLineString());
        writeRepository.addEvent(command.getFileName(), lineAddedEvent);
        return Arrays.asList(lineAddedEvent);
    }

}
