package br.com.kopzinski.eventsourcing;

import br.com.kopzinski.eventsourcing.services.FileReader;
import br.com.kopzinski.eventsourcing.services.FileWriter;
import br.com.kopzinski.eventsourcing.services.PropertiesReader;

public class App {
    public static void main(String[] args ) throws Exception {
        System.out.println("Kop! EventSourcing App has been started");
        PropertiesReader reader = new PropertiesReader("kop-app.properties");
        FileWriter fileWriter = new FileWriter(reader.getProperty("output"));
        FileReader fileReader = new FileReader(fileWriter);
        fileReader.watchFiles(reader.getProperty("input"));

    }
}
