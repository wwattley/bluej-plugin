package project.comp3607.model.refact;

import project.comp3607.model.Session;

public class SessionSummary {
    private Session session;
    private CompilationSummary compilationSummary;
    private ClassLifeSummary classLifeSummary;

    public SessionSummary(Session currentSession) {
        this.session = currentSession;
        this.compilationSummary = CompilationSummary.compute(currentSession);
        this.classLifeSummary = ClassLifeSummary.compute(currentSession);
    }

    public Session getSession() { return session; }
    public CompilationSummary getCompilationSummary() { return compilationSummary; }
    public ClassLifeSummary getClassLifeSummary() { return classLifeSummary; }

}
