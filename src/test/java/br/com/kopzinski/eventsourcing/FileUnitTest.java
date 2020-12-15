package br.com.kopzinski.eventsourcing;

import br.com.kopzinski.eventsourcing.aggregates.FileAggregate;
import br.com.kopzinski.eventsourcing.commands.*;
import br.com.kopzinski.eventsourcing.domain.Sale;
import br.com.kopzinski.eventsourcing.domain.Seller;
import br.com.kopzinski.eventsourcing.projections.FileProjection;
import br.com.kopzinski.eventsourcing.projectors.FileProjector;
import br.com.kopzinski.eventsourcing.queries.*;
import br.com.kopzinski.eventsourcing.repository.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FileUnitTest {

    private EventStore writeRepository;
    private FileAggregate aggregate;
    private FileProjector projector;
    private FileReadRepository readRepository;
    private FileProjection projection;

    @Before
    public void setUp() {
        writeRepository = EventStore.getInstance();
        readRepository = FileReadRepository.getInstance();
        aggregate = new FileAggregate(writeRepository);
        projector = new FileProjector(readRepository, writeRepository);
        projection = new FileProjection(readRepository);
    }

    @Test
    public void givenNewFileShouldProjectFileName() throws Exception {

        String fileName = UUID.randomUUID().toString();
        CreateFileCommand createFileCommand = new CreateFileCommand(fileName);
        aggregate.handleCreateFileCommand(createFileCommand);

        projector.project(fileName);
        FileNameQuery query = new FileNameQuery(fileName);
        assertEquals(fileName, projection.handle(query));
    }

    @Test
    public void givenNewSellerLineAddedShouldProjectSellersQty() throws Exception {

        String fileName = UUID.randomUUID().toString();
        CreateFileCommand createFileCommand = new CreateFileCommand(fileName);
        aggregate.handleCreateFileCommand(createFileCommand);

        String lineString = "001ç1234567891234çPedroç50000";
        AddFileLineCommand addFileLineCommand = new AddFileLineCommand(fileName, lineString);
        aggregate.handleAddFileLineCommand(addFileLineCommand);

        String lineString2 = "001ç3245678865434çPauloç40000.99";
        AddFileLineCommand addFileLineCommand2 = new AddFileLineCommand(fileName, lineString2);
        aggregate.handleAddFileLineCommand(addFileLineCommand2);

        projector.project(fileName);
        SellerQuantityQuery query = new SellerQuantityQuery(fileName);
        assertEquals(Integer.valueOf(2), projection.handle(query));
    }

    @Test
    public void givenNewClientLineAddedShouldProjectClientsQty() throws Exception {

        String fileName = UUID.randomUUID().toString();
        CreateFileCommand createFileCommand = new CreateFileCommand(fileName);
        aggregate.handleCreateFileCommand(createFileCommand);

        String lineString = "002ç2345675434544345çJose da SilvaçRural";
        AddFileLineCommand addFileLineCommand = new AddFileLineCommand(fileName, lineString);
        aggregate.handleAddFileLineCommand(addFileLineCommand);

        String lineString2 = "002ç2345675433444345çEduardo PereiraçRural";
        AddFileLineCommand addFileLineCommand2 = new AddFileLineCommand(fileName, lineString2);
        aggregate.handleAddFileLineCommand(addFileLineCommand2);

        projector.project(fileName);
        ClientQuantityQuery query = new ClientQuantityQuery(fileName);
        assertEquals(Integer.valueOf(2), projection.handle(query));
    }

    @Test
    public void givenTowNewSalesLineAddedShouldProjectTheTopSale() throws Exception {

        String fileName = UUID.randomUUID().toString();
        CreateFileCommand createFileCommand = new CreateFileCommand(fileName);
        aggregate.handleCreateFileCommand(createFileCommand);

        String lineString = "003ç08ç[1-34-10,2-33-1.50,3-40-0.10]çPaulo";
        AddFileLineCommand addFileLineCommand = new AddFileLineCommand(fileName, lineString);
        aggregate.handleAddFileLineCommand(addFileLineCommand);

        String lineString2 = "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]ç Pedro";
        AddFileLineCommand addFileLineCommand2 = new AddFileLineCommand(fileName, lineString2);
        aggregate.handleAddFileLineCommand(addFileLineCommand2);

        projector.project(fileName);
        TopSaleQuery query = new TopSaleQuery(fileName);

        Sale expectedTopSale = Sale.builder().id("10").build();
        Sale topSale = projection.handle(query);
        assertEquals(expectedTopSale.getId(), topSale.getId());
    }

    @Test
    public void givenNewFileTwoNewSellersAndTwoNewClientsShouldProjectTwoClientsAndTwoSellers() throws Exception {

        String fileName = UUID.randomUUID().toString();
        CreateFileCommand createFileCommand = new CreateFileCommand(fileName);
        aggregate.handleCreateFileCommand(createFileCommand);

        String[] lines = {
            "001ç1234567891234çPedroç50000",
            "001ç3245678865434çPauloç40000.99",
            "002ç2345675434544345çJose da SilvaçRural",
            "002ç2345675433444345çEduardo PereiraçRural",
            "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]ç Pedro",
            "003ç08ç[1-34-10,2-33-1.50,3-40-0.10]çPaulo"
        };

        for (String line: lines ) {
            AddFileLineCommand addFileLineCommand = new AddFileLineCommand(fileName, line);
            aggregate.handleAddFileLineCommand(addFileLineCommand);
        }

        projector.project(fileName);

        ClientQuantityQuery clientQuery = new ClientQuantityQuery(fileName);
        assertEquals(Integer.valueOf(2), projection.handle(clientQuery));

        SellerQuantityQuery sellerQuery = new SellerQuantityQuery(fileName);
        assertEquals(Integer.valueOf(2), projection.handle(sellerQuery));

        TopSaleQuery topSaleQuery = new TopSaleQuery(fileName);
        Sale expectedTopSale = Sale.builder().id("10").build();
        Sale topSale = projection.handle(topSaleQuery);
        assertEquals(expectedTopSale.getId(), topSale.getId());

        WorseSellerQuery worseSellerQuery = new WorseSellerQuery(fileName);
        Seller expectedWorseSeller = Seller.builder().cpf("3245678865434").build();
        Seller worseSeller = projection.handle(worseSellerQuery);
        assertEquals(expectedWorseSeller.getCpf(), worseSeller.getCpf());

    }

}
