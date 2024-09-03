package co.killionrevival.killioncommons.database.models;

/**
 * Return code for sql query methods
 */
public enum ReturnCode {
    SUCCESS(true),
    ALREADY_EXISTS(false),
    NOT_EXIST(false),
    FAILURE(false);

    private boolean truthy;

    ReturnCode(boolean truthy) {
        this.truthy = truthy;
    }

    public boolean isTruthy() {
        return this.truthy;
    }
}