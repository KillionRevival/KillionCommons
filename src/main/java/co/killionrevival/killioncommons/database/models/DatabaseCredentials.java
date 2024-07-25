package co.killionrevival.killioncommons.database.models;

import lombok.Getter;

@Getter
public class DatabaseCredentials {
    private String ip;
    private int port;
    private String username;
    private String password;
    private String database;
}
