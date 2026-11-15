package project.comp3607;

import org.junit.jupiter.api.Test;

import project.comp3607.events.EventFactory;
import project.comp3607.events.ErrorCategoryEnum;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventFactoryTest {

//all methods in EventFactory Class tested here

    @Test
    void checkIfSequenceIncrementsForEachFactory() {
         EventFactory f = new EventFactory();
        assertEquals(1, f.createSessionStartEvent().getSequence());
        assertEquals(2, f.createSessionEndEvent().getSequence());
        assertEquals(3, f.createClassCreatedEvent("TestName").getSequence());
}



    @Test
    void checkSeparateFactorySequence(){

        assertEquals(1, new EventFactory().createSessionStartEvent().getSequence());
        assertEquals(1, new EventFactory().createSessionStartEvent().getSequence());
    }


     @Test
    void packageEventsCarryPackageName() {
        EventFactory f = new EventFactory();
        assertEquals("EventFactory", f.createPackageOpenEvent("EventFactory").getEventDetails().get("package_name"));
        assertEquals("comp3607", f.createPackageCloseEvent("comp3607").getEventDetails().get("package_name"));
    }
     @Test
    void classEventsCarryClassName() {
        EventFactory f = new EventFactory();
        Event created = f.createClassCreatedEvent("Testing");
        Event deleted = f.createClassDeletedEvent("TestTestTest");
        assertEquals(EventTypeEnum.CLASS_CREATED, created.getEventType());
        assertEquals("Testing", created.getEventDetails().get("class_name"));
        assertEquals(EventTypeEnum.CLASS_DELETED, deleted.getEventType());
        assertEquals("TestTestTest", deleted.getEventDetails().get("class_name"));
    }



@Test
    void createCompileSucceededEventTest() {
        Event e = new EventFactory().createCompileSucceededEvent(List.of("Success.java", "Successor.java"));
        assertEquals(EventTypeEnum.COMPILE_SUCCEEDED, e.getEventType());
        assertEquals(List.of("Success.java", "Successor.java"), e.getEventDetails().get("files"));
    }

    @Test
    void createCompileErrorEventTest(){

        Event e = new EventFactory()
        .createCompileErrorEvent("file.java", 0, "Hello", ErrorCategoryEnum.TYPE);

         assertEquals(EventTypeEnum.COMPILE_ERROR, e.getEventType());
        assertEquals("file.java",       e.getEventDetails().get("file"));
        assertEquals(0,                e.getEventDetails().get("line"));
        assertEquals("Hello", e.getEventDetails().get("message"));
        // Important: the enum itself is stored, not its code or name.
        assertEquals(ErrorCategoryEnum.TYPE, e.getEventDetails().get("category"));
    }












}
