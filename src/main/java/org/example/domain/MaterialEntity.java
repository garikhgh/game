package org.example.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MaterialEntity {

    private String materialUuid;
    private String warehouseUuid;
    private String playerUuid;

    private MaterialType materialType;

    private String name;
    private String description;
    private String icon;

    private int maxCapacity;
    private int currentValue;
}
