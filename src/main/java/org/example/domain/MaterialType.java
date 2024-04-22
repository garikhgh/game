package org.example.domain;


import lombok.Getter;

@Getter
public enum MaterialType {

    IRON(100),
    COOPER(200),
    BOLT(250);

    private final int maxCapacity;
    MaterialType(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
