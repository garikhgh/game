package org.example.storage;

import org.example.domain.MaterialEntity;
import org.example.domain.MaterialType;
import org.junit.jupiter.api.Test;

import java.util.IntSummaryStatistics;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MaterialStorageTest {

    @Test
    void addMaterial() {

        MaterialStorage materialStorage = new MaterialStorage();
        IntStream.rangeClosed(0, 100).parallel()
                .forEach(i -> {
                    MaterialEntity materialEntity = mockMaterial();
                    boolean b = materialStorage.addMaterial(materialEntity);
                    System.out.println("Material is added " + i +"   " +  b);
                });


//        executor.submit(()->{
//            MaterialEntity materialEntity = mockMaterial();
//            boolean b = materialStorage.addMaterial(materialEntity);
//            System.out.println("Material is added " +"   " +  b);
//        });


        System.out.println("Material in Storage = " + materialStorage.getMaterialCount());
    }

    public MaterialEntity mockMaterial() {
        MaterialEntity material = new MaterialEntity();
        material.setMaterialType(MaterialType.IRON);
        material.setWarehouseUuid(UUID.randomUUID().toString());
        material.setMaterialUuid(UUID.randomUUID().toString());
        material.setCurrentValue(40);
        material.setIcon("Iron Icon");
        material.setName("IronName");
        material.setDescription("description");
        material.setPlayerUuid(UUID.randomUUID().toString());
        material.setMaxCapacity(500);
        return material;
    }
}