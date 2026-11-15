package project.comp3607;

import org.junit.jupiter.api.Test;
import project.comp3607.events.ErrorCategoryEnum;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;

import project.comp3607.model.Session;
import project.comp3607.model.ActivitySession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static project.comp3607.events.EventTypeEnum.*;

class ActivitySessionTest {



    private static Event event(EventTypeEnum type, long offsetSec) {
        return new Event(1, type, Map.of("category", ErrorCategoryEnum.OTHER));
    }
    //private methods to build out a full acitivity session
    private static ActivitySession activity(Event... events) {
        Session s = new Session();
        for (Event e : events) s.addEvent(e);
        return ActivitySession.compute(s);
    }

    @Test
    void emptySessionIsZeroZero() {
        ActivitySession a = ActivitySession.compute(new Session());
        assertEquals(0, a.getActiveSeconds());
        assertEquals(0, a.getIdleSeconds());
    }

    @Test
    void onlySessionEventsMeansAllIdle() {
        // start at 0, end at 100. No activity events in between.
        ActivitySession a = activity(
                event(SESSION_START, 0),
                event(SESSION_END,   100));
        assertEquals(0,   a.getActiveSeconds());
        assertEquals(100, a.getIdleSeconds());
    }

    @Test
    void singleActivityEventAtStartWithSessionEndCapsAt60() {
        // activity at 0, session end at 100. gap 100 -> capped at 60.
        ActivitySession a = activity(
                event(SESSION_START,   0),
                event(COMPILE_ERROR,   0),
                event(SESSION_END,   100));
        assertEquals(60,  a.getActiveSeconds());
        assertEquals(40,  a.getIdleSeconds());
    }

    @Test
    void twoActivityEventsWithin60sContributeFullGap() {
        ActivitySession a = activity(
                event(SESSION_START,    0),
                event(COMPILE_ERROR,   10),
                event(COMPILE_ERROR,   40),
                event(SESSION_END,     50));
        // gap 10->40 = 30 (uncapped); gap 40->50 = 10 (uncapped). total active = 40.
        assertEquals(40, a.getActiveSeconds());
        assertEquals(10, a.getIdleSeconds());
    }

    @Test
    void twoActivityEventsOver60sCapped() {
        ActivitySession a = activity(
                event(SESSION_START,     0),
                event(COMPILE_ERROR,    10),
                event(COMPILE_ERROR,   500),
                event(SESSION_END,     600));
        // gap 10->500 = 490 -> capped 60. gap 500->600 = 100 -> capped 60. total = 120.
        assertEquals(120, a.getActiveSeconds());
        assertEquals(480, a.getIdleSeconds());
    }

    @Test
    void trailingActivityFollowedBySessionEndCountsTimeToEnd() {
        ActivitySession a = activity(
                event(SESSION_START,     0),
                event(CLASS_CREATED,    10),
                event(SESSION_END,      80));
        // one activity at 10. next activity none -> upper = session_end at 80. gap 70 -> 60.
        assertEquals(60, a.getActiveSeconds());
        assertEquals(20, a.getIdleSeconds());
    }

    @Test
    void lastEventBeingAnActivityEventContributesZero() {
        // Activity event IS the last event. No SESSION_END.
        ActivitySession a = activity(
                event(SESSION_START,  0),
                event(COMPILE_ERROR, 30));
        // upper = last event = itself. gap 0. active 0. total 30. idle 30.
        assertEquals(0,  a.getActiveSeconds());
        assertEquals(30, a.getIdleSeconds());
    }

    @Test
    void packageOpenAndCloseCountAsActivity() {
        ActivitySession a = activity(
                event(SESSION_START,    0),
                event(PACKAGE_OPEN,    10),
                event(PACKAGE_CLOSE,   20),
                event(SESSION_END,     30));
        // 10->20 = 10, 20->30 = 10. active = 20.
        assertEquals(20, a.getActiveSeconds());
        assertEquals(10, a.getIdleSeconds());
    }

    @Test
    void sessionStartAndEndAreNotActivity() {
        ActivitySession a = activity(
                event(SESSION_START,  0),
                event(SESSION_END,   50));
        // Neither is activity, so nothing contributes to active.
        assertEquals(0,  a.getActiveSeconds());
        assertEquals(50, a.getIdleSeconds());
    }
}
