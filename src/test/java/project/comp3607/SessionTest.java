package project.comp3607;

import org.junit.jupiter.api.Test;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;


import project.comp3607.model.Session;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {


    private static Event event(EventTypeEnum type, long offset) {
        return new Event(1, type, Map.of());
    }

    @Test
    void emptySessionHasNullTimestamps() {
        Session s = new Session();
        assertNull(s.getStartTime());
        assertNull(s.getEndTime());
        assertTrue(s.getEvents().isEmpty());
    }

    @Test
    void startTimeIsSetOnlyBySessionStart() {
        Session s = new Session();
        s.addEvent(event(EventTypeEnum.CLASS_CREATED, 5)); // NOT a start
        assertNull(s.getStartTime());
        s.addEvent(event(EventTypeEnum.SESSION_START, 10));
        assertEquals(10, s.getStartTime());
    }

    @Test
    void endTimeIsSetOnlyBySessionEnd() {
        Session s = new Session();
        s.addEvent(event(EventTypeEnum.SESSION_START, 0));
        s.addEvent(event(EventTypeEnum.CLASS_CREATED, 5));
        assertNull(s.getEndTime());
        s.addEvent(event(EventTypeEnum.SESSION_END, 60));
        assertEquals(60, s.getEndTime());
    }

    @Test
    void eventsListIsReadOnly() {
        Session s = new Session();
        assertThrows(UnsupportedOperationException.class,
                () -> s.getEvents().add(event(EventTypeEnum.SESSION_START, 0)));
    }

    @Test
    void eventsPreserveOrder() {
        Session s = new Session();
        Event a = event(EventTypeEnum.SESSION_START, 0);
        Event b = event(EventTypeEnum.SESSION_END,   1);
        s.addEvent(a);
        s.addEvent(b);
        assertEquals(a, s.getEvents().get(0));
        assertEquals(b, s.getEvents().get(1));
    }
}
