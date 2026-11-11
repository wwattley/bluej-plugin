package project.comp3607.events;

public enum EventTypeEnum {
    SESSION_START("SS"),
    SESSION_END("SE"),

    // More event types to be added here as needed

    // Project lifecycle
    PACKAGE_OPEN("PO"),
    PACKAGE_CLOSE("PC"),

    // Class Lifecycle
    CLASS_CREATED("CC"),
    CLASS_DELETED("CD"),

    // Compilation
    COMPILE_SUCCEEDED("CS"),
    COMPILE_ERROR("CE");

    private final String code;

    private EventTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() { return code; }

}
