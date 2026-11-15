package project.comp3607;

import org.junit.jupiter.api.Test;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;
import project.comp3607.model.ClassLifeSummary;
import project.comp3607.model.Session;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClassLifeSummaryTest {

   

    private static Event event(EventTypeEnum type) {
        return new Event(1, type, Map.of("ClassNameTesting", T0));
    }

    @Test
    void checkEmptyClassLife() {
        ClassLifeSummary c = ClassLifeSummary.compute(new Session());
        assertEquals(0, c.getClassesCreated());
        assertEquals(0, c.getClassesDeleted());
    }

    @Test
    void checkIfCreationAndDeletionWorks() {
        Session s = new Session();
        s.addEvent(event(EventTypeEnum.CLASS_CREATED));
        s.addEvent(event(EventTypeEnum.CLASS_CREATED));
        s.addEvent(event(EventTypeEnum.CLASS_DELETED));
        ClassLifeSummary c = ClassLifeSummary.compute(s);
        assertEquals(2, c.getClassesCreated());
        assertEquals(1, c.getClassesDeleted());
    }
}
