package co.killionrevival.killioncommons.config.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.killionrevival.killioncommons.util.logger.models.LogLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultConfig {
    @JsonProperty("plugin_prefix")
    private String pluginPrefix;

    @JsonProperty("log_level")
    private LogLevel logLevel;

    public void setLogLevel(String logLevel) {
        try {
            this.logLevel = LogLevel.valueOf(logLevel.toUpperCase());

        } catch (IllegalArgumentException e) {
            this.logLevel = LogLevel.INFO;
        }
    }
}
