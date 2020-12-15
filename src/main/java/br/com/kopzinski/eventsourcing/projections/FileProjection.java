package br.com.kopzinski.eventsourcing.projections;

import br.com.kopzinski.eventsourcing.domain.*;
import br.com.kopzinski.eventsourcing.queries.*;
import br.com.kopzinski.eventsourcing.repository.FileReadRepository;

public class FileProjection {

    private FileReadRepository repository;

    public FileProjection(FileReadRepository repository) {
        this.repository = repository;
    }

    private FileResult getFileResult(String fileName) throws Exception {
        FileResult fileResult = repository.getFileResult(fileName);
        if(fileResult == null)
            throw new Exception("FileResult does not exist.");
        return fileResult;
    }

    public String handle(FileNameQuery query) throws Exception {
        FileResult fileResult = getFileResult(query.getFileName());
        return fileResult.getFileName();
    }

    public Integer handle(SellerQuantityQuery query) throws Exception {
        FileResult fileResult = getFileResult(query.getFileName());
        return fileResult.getSellers().size();
    }

    public Integer handle(ClientQuantityQuery query) throws Exception {
        FileResult fileResult = getFileResult(query.getFileName());
        return fileResult.getClients().size();
    }

    public Sale handle(TopSaleQuery query) throws Exception {
        FileResult fileResult = getFileResult(query.getFileName());
        return fileResult.getTopSale();
    }

    public Seller handle(WorseSellerQuery query) throws Exception {
        FileResult fileResult = getFileResult(query.getFileName());
        return fileResult.getWorseSeller();
    }

}
