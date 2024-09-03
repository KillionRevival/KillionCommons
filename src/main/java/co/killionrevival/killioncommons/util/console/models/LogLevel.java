package co.killionrevival.killioncommons.util.console.models;

import java.util.logging.Level;

public enum LogLevel {
    OFF(Level.OFF),
    ERROR(Level.SEVERE),
    WARNING(Level.WARNING),
    INFO(Level.INFO),
    DEBUG(Level.FINER),
    ALL(Level.ALL);

    private Level level;

    LogLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }
}