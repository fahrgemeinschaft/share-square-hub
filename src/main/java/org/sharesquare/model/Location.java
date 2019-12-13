package org.sharesquare.model;

import lombok.Data;

@Data
public class Location {

    public enum type {
        Address,
        City
    }

    private long latitude;
    private long longitude;
    private String name;
    private type type;

}
