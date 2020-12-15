package br.com.kopzinski.eventsourcing.domain;

import lombok.Data;
import lombok.ToString;

import java.util.*;

@Data
@ToString
public class FileResult {
    public String id = UUID.randomUUID().toString();
    private String fileName;
    private Set<Seller> sellers = new HashSet<>();
    private Set<Client> clients = new HashSet<>();
    private Set<Sale> sales = new HashSet<>();
    private Sale topSale;
    private Seller worseSeller;

}
