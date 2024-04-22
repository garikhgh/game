package org.example.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MaterialDto {

    private MaterialType materialType;
    private int materialValue;
}
