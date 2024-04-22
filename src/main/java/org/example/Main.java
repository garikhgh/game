package org.example;


import org.example.domain.MaterialEntity;
import org.example.domain.MaterialType;
import org.example.exception.MaterialMaxCapacityExceedingException;
import org.example.exception.MaterialNegativeValueException;
import org.example.storage.MaterialStorage;
import org.example.storage.PlayerStorage;
import org.example.storage.WarehouseStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws InterruptedException {


        PlayerStorage playerStorage = new PlayerStorage();
        WarehouseStorage warehouseStorage = new WarehouseStorage();
        MaterialStorage materialStorage = new MaterialStorage();

        Thread thread1 = new Thread(() -> testGame(playerStorage, warehouseStorage, materialStorage, "Thread 1"));
        Thread thread2 = new Thread(() -> testGame(playerStorage, warehouseStorage, materialStorage, "Thread 2"));
        Thread thread3 = new Thread(() -> testGame(playerStorage, warehouseStorage, materialStorage, "Thread 3"));

        // Start the threads
        thread1.start();
        thread2.start();
        thread3.start();
        // during 1 second the app adds 300 players 3000 warehouses and 9000 materials
        Thread.sleep(1000);
        CopyOnWriteArrayList<String> players = playerStorage.getPlayers();
        List<String> allWarehouses = warehouseStorage.findAllWarehouses();
        int materialCount = materialStorage.getMaterialCount();

        logger.info("##### Players should be    100  Qty= {}", players.size());
        logger.info("##### Warehouses should be 1000 Qty= {}", allWarehouses.size());
        logger.info("##### Materials should be  9000 Qty= {}", materialCount);

    }


    private static void testGame(PlayerStorage playerStorage, WarehouseStorage warehouseStorage, MaterialStorage materialStorage, String threadName) {

        IntStream.range(0, 100)
                .forEach(i -> {
                    String createdPlayerUuid = playerStorage.createPlayer();
                    // create 10 warehouse for each player
                    for (int j = 0; j < 10; j++) {
                        String warehouse = warehouseStorage.addWarehouse(createdPlayerUuid);
//                         adding 3 type of materials to each warehouse
                        MaterialEntity materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.IRON, 30);
                        boolean b = materialStorage.addMaterial(materialEntity);
                        materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.COOPER, 15);
                        b = materialStorage.addMaterial(materialEntity);
                        materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.BOLT, 60);
                        b = materialStorage.addMaterial(materialEntity);
                    }
                });
    }
    public static MaterialEntity mockMaterial(String playerUuid, String warehouseUuid, MaterialType materialType, int materialCurrentValue) {
        MaterialEntity material = new MaterialEntity();
        try {

            material.setPlayerUuid(playerUuid);
            material.setMaterialType(materialType);
            material.setWarehouseUuid(warehouseUuid);
            material.setMaterialUuid(UUID.randomUUID().toString());
            material.setMaxCapacity(materialType.getMaxCapacity());
            material.setCurrentValue(materialCurrentValue);
            material.setIcon(materialType.name() + " Icon");
            material.setName(materialType.name() + "_Name");
            material.setDescription(materialType.name() + " description");


            return material;
        } catch (MaterialNegativeValueException | MaterialMaxCapacityExceedingException e) {

            logger.error("Material value exception, {}", e.getMessage());
            return material;
        }
    }
}