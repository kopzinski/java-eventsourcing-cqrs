package br.com.kopzinski.eventsourcing.services;

import br.com.kopzinski.eventsourcing.aggregates.FileAggregate;
import br.com.kopzinski.eventsourcing.commands.AddFileLineCommand;
import br.com.kopzinski.eventsourcing.commands.CreateFileCommand;
import br.com.kopzinski.eventsourcing.projections.FileProjection;
import br.com.kopzinski.eventsourcing.projectors.FileProjector;
import br.com.kopzinski.eventsourcing.repository.EventStore;
import br.com.kopzinski.eventsourcing.repository.FileReadRepository;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.Collection;

public class FileReader {

    private static final long POLL_INTERVAL = 1000;

    private FileAggregate aggregate;
    private EventStore writeRepository;
    private FileReadRepository readRepository;
    private FileProjector projector;
    private FileProjection projection;
    private FileWriter fileWriter;

    public FileReader(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
        writeRepository = EventStore.getInstance();
        readRepository = FileReadRepository.getInstance();
        aggregate = new FileAggregate(writeRepository);
        projector = new FileProjector(readRepository, writeRepository);
        projection = new FileProjection(readRepository);
    }

    private final void handleLine(String fileName, String lineString) {
        AddFileLineCommand addFileLineCommand = new AddFileLineCommand(fileName, lineString);
        aggregate.handleAddFileLineCommand(addFileLineCommand);
    }

    private final void handleFile(File file) throws Exception {
        final LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        String fileName = file.getName();
        try {
            CreateFileCommand createFileCommand = new CreateFileCommand(fileName);
            aggregate.handleCreateFileCommand(createFileCommand);

            while (it.hasNext()) {
                final String line = it.nextLine();
                handleLine(fileName, line);
            }
        } catch (Exception e) {
            System.out.println("Kop! e: " + e.getMessage());
        } finally {
            projector.project(fileName);
            fileWriter.writeFileResult(fileName);
        }

    }

    public void watchFiles(String directoryName) throws Exception {
        File rootDir = new File(directoryName);

        FileAlterationObserver observer = new FileAlterationObserver(rootDir);
        FileAlterationMonitor monitor = new FileAlterationMonitor(POLL_INTERVAL);

        Collection<File> files = FileUtils.listFiles(rootDir, null, false);
        for (File file: files) {
            System.out.println("Kop! File found on startup:" + file.getName());
            handleFile(file);
        }

        FileAlterationListener listener = new FileAlterationListenerAdaptor() {

            @SneakyThrows
            @Override
            public void onFileCreate(File file) {
                System.out.println("Kop! A new file has been added" + file.getName());
                handleFile(file);
            }
        };

        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
    }

}
