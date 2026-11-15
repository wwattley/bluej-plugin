package project.comp3607;

import org.junit.jupiter.api.Test;
import project.comp3607.events.ErrorCategoryEnum;
import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;

import project.comp3607.model.Session;

import project.comp3607.model.CompilationSummary;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static project.comp3607.events.EventTypeEnum.*;

class CompilationSummaryTest {


    private static int seq = 0;

    private static Event event(EventTypeEnum type, Map<String, Object> details) {
        seq = seq++;
        return new Event(seq, type, details);
    }

    private static Event success() {
        return event(COMPILE_SUCCEEDED, Map.of("files", java.util.List.of("A.java")));
    }

    private static Event error(ErrorCategoryEnum cat) {
        return event(COMPILE_ERROR, Map.of(
                "file", "A.java", "line", 1, "message", "x", "category", cat));
    }

    private static CompilationSummary compileSummary(Event... events) {
        Session s = new Session();
        for (Event e : events) s.addEvent(e);
        return CompilationSummary.compute(s);
    }

    @Test
    void emptySessionProducesZeroes() {
        CompilationSummary c = compileSummary();
        assertEquals(0, c.getTotalCompilations());
        assertEquals(0, c.getSuccessfulCompilations());
        assertEquals(0, c.getFailedCompilations());
        assertEquals(0.0, c.getCompilationSuccessRate(), 1e-9);
        assertTrue(c.getErrorFrequency().isEmpty());
    }

    @Test
    void oneSuccessIs100Percent() {
        CompilationSummary c = compileSummary(success());
        assertEquals(1, c.getTotalCompilations());
        assertEquals(1, c.getSuccessfulCompilations());
        assertEquals(0, c.getFailedCompilations());
        assertEquals(100.0, c.getCompilationSuccessRate(), 1e-9);
    }

    @Test
    void oneFailureIs0Percent() {
        CompilationSummary c = compileSummary(error(ErrorCategoryEnum.SYMBOL));
        assertEquals(1, c.getTotalCompilations());
        assertEquals(0, c.getSuccessfulCompilations());
        assertEquals(1, c.getFailedCompilations());
        assertEquals(0.0, c.getCompilationSuccessRate(), 1e-9);
        assertEquals(1, c.getErrorFrequency().get("SYM"));
    }

    @Test
    void twoSuccessOneFailIsTwoThirds() {
        CompilationSummary c = compileSummary(success(), success(), error(ErrorCategoryEnum.SYMBOL));
        assertEquals(3, c.getTotalCompilations());
        assertEquals(2, c.getSuccessfulCompilations());
        assertEquals(1, c.getFailedCompilations());
        assertEquals(66.666, c.getCompilationSuccessRate(), 0.001);
    }

    @Test
    void errorFrequencyAccumulatesPerCode() {
        CompilationSummary c = compileSummary(
                error(ErrorCategoryEnum.SYMBOL),
                error(ErrorCategoryEnum.SYMBOL),
                error(ErrorCategoryEnum.TYPE),
                error(ErrorCategoryEnum.SYNTAX));
        assertEquals(2, c.getErrorFrequency().get("SYM"));
        assertEquals(1, c.getErrorFrequency().get("TYP"));
        assertEquals(1, c.getErrorFrequency().get("SYN"));
    }

    @Test
    void schemaSampleNumbersReproduced() {
        // 2 compiles: 1 ok, 1 error. 0.5 in the schema means ratio, here it's 50.0 percent.
        CompilationSummary c = compileSummary(success(), error(ErrorCategoryEnum.SYMBOL));
        assertEquals(2, c.getTotalCompilations());
        assertEquals(1, c.getSuccessfulCompilations());
        assertEquals(1, c.getFailedCompilations());
        // The code computes *percent*, not ratio. Schema sample showed 0.5. Documented gap.
        assertEquals(50.0, c.getCompilationSuccessRate(), 1e-9);
    }
}
