package br.com.kopzinski.eventsourcing.services;

import br.com.kopzinski.eventsourcing.domain.Sale;
import br.com.kopzinski.eventsourcing.domain.Seller;
import br.com.kopzinski.eventsourcing.projections.FileProjection;
import br.com.kopzinski.eventsourcing.queries.*;
import br.com.kopzinski.eventsourcing.repository.FileReadRepository;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileWriter {

    String outputDirectory;
    private FileReadRepository readRepository;
    private FileProjection projection;

    public FileWriter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        readRepository = FileReadRepository.getInstance();
        projection = new FileProjection(readRepository);
    }

    public void writeFileResult(String fileName) throws Exception {

        List<String> resultLines = new ArrayList<>();

        FileNameQuery query = new FileNameQuery(fileName);
        String name = projection.handle(query);
        resultLines.add("Results for the file: " + name);

        Integer clientsQty = projection.handle(new ClientQuantityQuery(fileName));
        resultLines.add("Number of clients: " + clientsQty);

        Integer sellersQty = projection.handle(new SellerQuantityQuery(fileName));
        resultLines.add("Number of sellers: " + sellersQty);

        Sale topSale = projection.handle(new TopSaleQuery(fileName));
        resultLines.add("Top Sale ID: " + topSale.getId());

        Seller worseSeller = projection.handle(new WorseSellerQuery(fileName));
        resultLines.add("Worse Seller Name: " + worseSeller.getName());

        File file = new File(outputDirectory + "/" + fileName);
        try {
            FileUtils.writeLines(file,"UTF-8", resultLines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
