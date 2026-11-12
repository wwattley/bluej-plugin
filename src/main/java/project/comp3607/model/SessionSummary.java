package project.comp3607.model;

public class SessionSummary {
    private Session session;
    private CompilationSummary compilationSummary;
    private ActivitySession activitySession;
    private ClassLifeSummary classLifeSummary;


    public SessionSummary(Session currentSession) {
        this.session = currentSession;
        this.compilationSummary = CompilationSummary.compute(currentSession);
        this.activitySession = ActivitySession.compute();
        this.classLifeSummary = ClassLifeSummary.compute(currentSession);
    }

    public Session getSession() { return session; }
    public CompilationSummary getCompilationSummary() { return compilationSummary; }
    public ClassLifeSummary getClassLifeSummary() { return classLifeSummary; }

}
