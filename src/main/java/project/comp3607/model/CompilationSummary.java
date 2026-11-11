package project.comp3607.model;

import project.comp3607.events.Event;
import project.comp3607.events.EventTypeEnum;
import project.comp3607.model.Session;

import project.comp3607.events.ErrorCategorizer;
import project.comp3607.events.ErrorCategoryEnum;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CompilationSummary {
    private int totalCompilations;
    private int successfulCompilations;
    private int failedCompilations;
    private double compilationSuccessRate;
    private Map<String, Integer> errorFrequency;

    public CompilationSummary(
        int totalCompilations,
        int successfulCompilations,
        int failedCompilations,
        double compilationSuccessRate,
        Map<String, Integer> errorFrequency
    ) {
        this.totalCompilations = totalCompilations;
        this.successfulCompilations = successfulCompilations;
        this.failedCompilations = failedCompilations;
        this.compilationSuccessRate = compilationSuccessRate;
        this.errorFrequency = errorFrequency;
    }

    public int getTotalCompilations() { return totalCompilations; }
    public int getSuccessfulCompilations() { return successfulCompilations; }
    public int getFailedCompilations() { return failedCompilations; }
    public double getCompilationSuccessRate() { return compilationSuccessRate; }
    public Map<String, Integer> getErrorFrequency() { return errorFrequency; }

    public static CompilationSummary compute(Session session) {
        List<Event> events = session.getEvents();

        int successfulCompiles = 0;
        int failedCompiles = 0;
        Map<String, Integer> errorFrequency = new HashMap<>();

        for (Event event : events) {
            if (event.getEventType() == EventTypeEnum.COMPILE_SUCCEEDED) {
                successfulCompiles++;

            } else if (event.getEventType() == EventTypeEnum.COMPILE_ERROR) {
                failedCompiles++;

                ErrorCategoryEnum category = (ErrorCategoryEnum) event.getEventDetails().get("category");
                errorFrequency.put(
                    category.getCode(),
                    errorFrequency.getOrDefault(category.getCode(), 0) + 1
                );
            }
        }

        int totalCompiles = successfulCompiles + failedCompiles;
        double compilationSuccessRate = totalCompiles == 0
            ? 0.0
            : ((double) successfulCompiles / totalCompiles) * 100.0;

        return new CompilationSummary(
            totalCompiles,
            successfulCompiles,
            failedCompiles,
            compilationSuccessRate,
            errorFrequency
        );

    }



}
