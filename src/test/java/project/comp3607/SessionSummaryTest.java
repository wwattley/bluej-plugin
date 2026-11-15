package project.comp3607;

import org.junit.jupiter.api.Test;
import project.comp3607.events.ErrorCategoryEnum;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;

import project.comp3607.model.Session;
import project.comp3607.model.SessionSummary;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionSummaryTest {

//SessionSummary takes most of our classes
//Session,Activity, ClassLife, Compilation Summary

    @Test
    void summaryExposesTheSessionItWasBuiltFrom() {
        Session s = new Session();
        assertSame(s, new SessionSummary(s).getSession());
    }

    @Test
    void allSummariesFilled() {
        Session s = new Session();
        s.addEvent(new Event(1, EventTypeEnum.SESSION_START, Map.of("className", "ClassDetailsHere")));
        s.addEvent(new Event(2, EventTypeEnum.CLASS_CREATED,
                Map.of("class_name2","ClassdetailsHere2")));
        s.addEvent(new Event(3, EventTypeEnum.COMPILE_SUCCEEDED,
                Map.of("files", java.util.List.of("maven.java","tame.java"))));
        s.addEvent(new Event(4, EventTypeEnum.COMPILE_ERROR,
                Map.of("file", "robot.java", "line", 3, "message", "x",
                       "category", ErrorCategoryEnum.SYMBOL)));
        s.addEvent(new Event(5, EventTypeEnum.SESSION_END, Map.of()));

        SessionSummary summary = new SessionSummary(s);

        assertEquals(1, summary.getClassLifeSummary().getClassesCreated());
        assertEquals(2, summary.getCompilationSummary().getTotalCompilations());
        assertEquals(50.0, summary.getCompilationSummary().getCompilationSuccessRate(), 1e-9);
        assertEquals(1, summary.getCompilationSummary().getErrorFrequency().get("SYM"));
        assertTrue(summary.getActivitySummary().getActiveSeconds() > 0);
    }
}
