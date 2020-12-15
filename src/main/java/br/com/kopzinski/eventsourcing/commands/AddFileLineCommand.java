package br.com.kopzinski.eventsourcing.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddFileLineCommand {
    private String fileName;
    private String lineString;

}
