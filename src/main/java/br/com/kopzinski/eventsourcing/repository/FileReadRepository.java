package br.com.kopzinski.eventsourcing.repository;

import br.com.kopzinski.eventsourcing.domain.FileResult;

import java.util.HashMap;
import java.util.Map;

public class FileReadRepository {

    private static FileReadRepository instance;
    private Map<String, FileResult> fileResults;

    private FileReadRepository() {
        this.fileResults = new HashMap<>();
    }

    public static FileReadRepository getInstance() {
        if(instance == null) {
            instance = new FileReadRepository();
        }
        return instance;
    }

    public void addFileResult(String id, FileResult fileResult) {
        fileResults.put(id, fileResult);
    }

    public FileResult getFileResult(String id) {
        return fileResults.get(id);
    }

}
