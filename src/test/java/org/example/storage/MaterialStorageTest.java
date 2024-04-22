package org.example.storage;

import org.example.domain.MaterialDto;
import org.example.domain.MaterialEntity;
import org.example.domain.MaterialType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MaterialStorageTest {

    @Test
    void addMaterialWithThreads() throws InterruptedException {

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
        assertEquals(300, players.size());
        assertEquals(3000, allWarehouses.size());
        assertEquals(9000, materialCount);

    }


    @Test
    void addMaterial() {
        PlayerStorage playerStorage = new PlayerStorage();
        WarehouseStorage warehouseStorage = new WarehouseStorage();
        MaterialStorage materialStorage = new MaterialStorage();
        testGame(playerStorage, warehouseStorage, materialStorage, "");
        // should be 100 players, 100 warehouses and 3000 material
        CopyOnWriteArrayList<String> players = playerStorage.getPlayers();
        List<String> allWarehouses = warehouseStorage.findAllWarehouses();
        int materialCount = materialStorage.getMaterialCount();
        assertEquals(100, players.size());
        assertEquals(1000, allWarehouses.size());
        assertEquals(3000, materialCount);

        String s = players.get(0);
        List<MaterialEntity> materialsOfPlayerByPlayerUuid = materialStorage.getMaterialsOfPlayerByPlayerUuid(s);
        assertEquals(30, materialsOfPlayerByPlayerUuid.size());
    }
    private void testGame(PlayerStorage playerStorage, WarehouseStorage warehouseStorage, MaterialStorage materialStorage, String threadName) {

        IntStream.range(0, 100)
                .forEach(i -> {
                    String createdPlayerUuid = playerStorage.createPlayer();
                    // create 10 warehouse for each player
                    for (int j = 0; j < 10; j++) {
                        String warehouse = warehouseStorage.addWarehouse(createdPlayerUuid);
//                         adding 3 type of materials to each warehouse
                        MaterialEntity materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.IRON);
                        boolean b = materialStorage.addMaterial(materialEntity);
                        materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.COOPER);
                        b = materialStorage.addMaterial(materialEntity);
                        materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.BOLT);
                        b = materialStorage.addMaterial(materialEntity);
                    }
                });
    }

    @Test
    void removeMaterial() {

        PlayerStorage playerStorage = new PlayerStorage();
        WarehouseStorage warehouseStorage = new WarehouseStorage();
        MaterialStorage materialStorage = new MaterialStorage();

        String createdPlayerUuid = playerStorage.createPlayer();
        String warehouse = warehouseStorage.addWarehouse(createdPlayerUuid);

        MaterialEntity materialEntity = mockMaterial(createdPlayerUuid, warehouse, MaterialType.IRON);
        boolean b = materialStorage.addMaterial(materialEntity);

        List<MaterialEntity> materialByPlayerUuid = materialStorage.findMaterialByPlayerUuid(createdPlayerUuid);
        assertEquals(1, materialByPlayerUuid.size());

        boolean b1 = materialStorage.removeMaterial(materialEntity);
        assertTrue(b1);
    }
    @Test
    void moveMaterial() {
        PlayerStorage playerStorage = new PlayerStorage();
        WarehouseStorage warehouseStorage = new WarehouseStorage();
        MaterialStorage materialStorage = new MaterialStorage();

        String createdPlayerUuid = playerStorage.createPlayer();

        String fromWarehouse = warehouseStorage.addWarehouse(createdPlayerUuid);
        String hostWarehouse = warehouseStorage.addWarehouse(createdPlayerUuid);

        List<String> playersWarehouses = warehouseStorage.findPlayersWarehouses(createdPlayerUuid);
        assertEquals(2, playersWarehouses.size());

        // adding material Iron in fromWarehouse
        MaterialEntity materialEntity = mockMaterial(createdPlayerUuid, fromWarehouse, MaterialType.IRON);

        // adding material before moving
        boolean b = materialStorage.addMaterial(materialEntity);
        assertTrue(b);
        MaterialDto materialDto = new MaterialDto();
        materialDto.setMaterialValue(20);
        materialDto.setMaterialType(MaterialType.IRON);
        materialDto.setMaterialUuid(materialEntity.getMaterialUuid());

        boolean b1 = materialStorage.moveIfMaterialPresent(createdPlayerUuid, hostWarehouse, fromWarehouse, materialDto);
        Optional<MaterialEntity> materialByMaterialUuid = materialStorage.findMaterialByMaterialUuid(materialEntity.getMaterialUuid());
        assertTrue(materialByMaterialUuid.isPresent());
        MaterialEntity materialEntity1 = materialByMaterialUuid.get();
        assertEquals(20, materialEntity1.getCurrentValue());
        assertTrue(b1);


        // roll back
        materialDto = new MaterialDto();
        materialDto.setMaterialValue(100);
        materialDto.setMaterialType(MaterialType.IRON);
        materialDto.setMaterialUuid(materialEntity.getMaterialUuid());

        b1 = materialStorage.moveIfMaterialPresent(createdPlayerUuid, hostWarehouse, fromWarehouse, materialDto);
        assertTrue(b1);

        materialByMaterialUuid = materialStorage.findMaterialByMaterialUuid(materialEntity.getMaterialUuid());
        assertTrue(materialByMaterialUuid.isPresent());
        materialEntity1 = materialByMaterialUuid.get();
        assertEquals(0, materialEntity1.getCurrentValue());
        assertTrue(b1);
    }

    public MaterialEntity mockMaterial(String playerUuid, String warehouseUuid, MaterialType materialType) {
        MaterialEntity material = new MaterialEntity();
        material.setMaterialType(materialType);
        material.setWarehouseUuid(warehouseUuid);
        material.setMaterialUuid(UUID.randomUUID().toString());
        material.setCurrentValue(40);
        material.setIcon(materialType.name() + " Icon");
        material.setName(materialType.name() +  "_Name");
        material.setDescription(materialType.name() + " description");
        material.setPlayerUuid(playerUuid);
        material.setMaxCapacity(materialType.getMaxCapacity());
        return material;
    }


}