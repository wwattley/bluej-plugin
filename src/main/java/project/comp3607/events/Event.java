package project.comp3607.events;

import java.time.Instant;
import java.util.Map;

public class Event implements EventInterface {
    private final int sequence;
    private final Instant timestamp;
    private final EventTypeEnum type;
    private final Map<String, Object> eventDetails;

    public Event(int sequence, EventTypeEnum type, Map<String, Object> eventDetails) {
        this.sequence = sequence;
        this.timestamp = Instant.now();
        this.type = type;

        // validate details sometime

        this.eventDetails = Map.copyOf(eventDetails);
    }

    @Override
    public int getSequence() { return sequence; }

    @Override
    public Instant getTimestamp() { return timestamp; }

    @Override
    public EventTypeEnum getEventType() { return type; }

    @Override
    public Map<String, Object> getEventDetails() { return eventDetails; }

}
