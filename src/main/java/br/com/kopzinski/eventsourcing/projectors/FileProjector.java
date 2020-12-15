package br.com.kopzinski.eventsourcing.projectors;

import br.com.kopzinski.eventsourcing.domain.*;
import br.com.kopzinski.eventsourcing.events.*;
import br.com.kopzinski.eventsourcing.repository.EventStore;
import br.com.kopzinski.eventsourcing.repository.FileReadRepository;

import java.util.*;

public class FileProjector {

    FileReadRepository readRepository;
    EventStore writeRepository;

    private static final String[] split(String text, String separator) {
        return text.split(separator);
    }
    private static final String clean(String text) {
        return text.replaceAll("(\\[|\\])", "");
    }

    public FileProjector(FileReadRepository readRepository, EventStore writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    public void project(String fileName) throws Exception {
        List<Event> events = writeRepository.getEvents(fileName);
        Comparator<Event> eventSorter = Comparator.comparing(Event::getCreated);
        Collections.sort(events, eventSorter);

        for (Event event : events) {
            if (event instanceof FileCreatedEvent)
                apply(fileName, (FileCreatedEvent) event);
            if (event instanceof LineAddedEvent)
                apply(fileName, (LineAddedEvent) event);
        }
    }

    public void apply(String fileName, FileCreatedEvent event) {
        FileResult fileResult = Optional.ofNullable(readRepository.getFileResult(fileName))
                .orElse(new FileResult());
        fileResult.setFileName(fileName);
        readRepository.addFileResult(fileName, fileResult);
    }

    public void apply(String fileName, LineAddedEvent event) throws Exception {
        FileResult fileResult = readRepository.getFileResult(fileName);
        if(fileResult == null)
            throw new Exception("File has not been written yet");

        String lineString = event.getLineString();
        String[] lineItems = split(lineString,"ç");

        switch (lineItems[0]) {
            case "001":
                Seller seller = handleSellerLine(lineItems);
                fileResult.getSellers().add(seller);
                break;
            case "002":
                Client client = handleClientLine(lineItems);
                fileResult.getClients().add(client);
                break;
            case "003":
                Sale sale = handleSaleLine(lineItems);
                fileResult.getSales().add(sale);
                Optional<Sale> optionalTopSale = Optional.ofNullable(fileResult.getTopSale());
                if(optionalTopSale.isEmpty() || sale.getTotalPrice() > optionalTopSale.get().getTotalPrice()) {
                    fileResult.setTopSale(sale);
                }

                fileResult.getSellers()
                        .stream()
                        .filter(s -> s.getName().equals(sale.getSellerName()))
                        .findFirst()
                        .ifPresent(s -> s.setTotalSold(s.getTotalSold() + sale.getTotalPrice()));

                fileResult.getSellers()
                        .stream()
                        .sorted(Comparator.comparingDouble(Seller::getTotalSold))
                        .findFirst()
                        .ifPresent(s -> fileResult.setWorseSeller(s));

                break;
            default: return;
        }

    }

    private final Seller handleSellerLine(String[] lineItems) {
        return Seller.builder()
                .typeIdentifier(lineItems[0])
                .cpf(lineItems[1])
                .name(lineItems[2])
                .salary(Double.valueOf(lineItems[3]))
                .build();
    }

    private final Client handleClientLine(String[] lineItems) {
        return Client.builder()
                .typeIdentifier(lineItems[0])
                .cnpj(lineItems[1])
                .name(lineItems[2])
                .businessArea(lineItems[3])
                .build();
    }

    private final Sale handleSaleLine(String[] lineItems) {
        Set<Item> items = extractItems(lineItems);
        Double totalPrice = items.stream().mapToDouble(Item::getPrice).sum();

        return Sale.builder()
                .typeIdentifier(lineItems[0])
                .id(lineItems[1])
                .sellerName(lineItems[3].trim())
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    private Set<Item> extractItems(String[] lineItems) {
        Set<Item> items = new HashSet<>();
        String cleared = clean(lineItems[2]);
        String[] rawItems = split(cleared, ",");
        for (String rawItem: rawItems ) {
            String[] saleItemParts = split(rawItem, "-");
            String id = saleItemParts[0];
            Long qty = Long.parseLong(saleItemParts[1]);
            Double price = Double.valueOf(saleItemParts[2]);
            Item item = new Item(id, qty, price);
            items.add(item);
        }
        return items;
    }

}
