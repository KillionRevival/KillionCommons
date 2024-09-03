package co.killionrevival.killioncommons.util.console.models;

import lombok.Getter;

public enum LogLevel {
    OFF(0),
    ERROR(1),
    WARNING(2),
    INFO(3),
    DEBUG(4);

    @Getter
    private final Integer level;

    LogLevel(Integer level) {
        this.level = level;
    }
}