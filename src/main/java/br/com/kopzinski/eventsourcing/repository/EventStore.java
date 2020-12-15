package br.com.kopzinski.eventsourcing.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.kopzinski.eventsourcing.events.Event;

public class EventStore {

    private static EventStore instance;

    private Map<String, List<Event>> store;
    private EventStore() {
        this.store = new HashMap<>();
    }

    public static EventStore getInstance() {
        if(instance == null) {
            instance = new EventStore();
        }
        return instance;
    }

    public void addEvent(String id, Event event) {
        List<Event> events = store.get(id);
        if (events == null) {
            events = new ArrayList<Event>();
            events.add(event);
            store.put(id, events);
        } else {
            events.add(event);
        }
    }

    public List<Event> getEvents(String id) {
        return store.get(id);
    }

}
