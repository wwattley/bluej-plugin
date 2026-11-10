package project.comp3607.events;

public enum ErrorCategoryEnum {
    SYMBOL("SYM"),
    TYPE("TYP"),
    SYNTAX("SYN"),
    BRACES("BRC"),
    MISSING_RETURN("RET"),
    OTHER("OTH");

    private final String code;

    private ErrorCategoryEnum(String code) {
        this.code = code;
    }

    public String getCode() { return code; }

}
