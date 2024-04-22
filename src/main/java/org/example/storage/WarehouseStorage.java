package org.example.storage;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class WarehouseStorage {

    private ConcurrentMap<String, List<String>> warehouseStorage = new ConcurrentHashMap<>();

    public synchronized String addWarehouse(String playerUuid) {
        String uuid = UUID.randomUUID().toString();
        if (!warehouseStorage.containsKey(playerUuid)) {
            List<String> warehouseList = warehouseStorage.get(playerUuid);
            if (warehouseList != null) {
                warehouseList.add(uuid);
                return uuid;
            } else {
                List<String> newWarehouseList = new ArrayList<>();
                newWarehouseList.add(uuid);
                warehouseStorage.put(playerUuid, newWarehouseList);
                return uuid;
            }
        } else {
            List<String> warehouseList = warehouseStorage.get(playerUuid);
            if (warehouseList != null) {
                warehouseList.add(uuid);
                return uuid;
            } else {
                List<String> newWarehouseList = new ArrayList<>();
                newWarehouseList.add(uuid);
                warehouseStorage.put(playerUuid, newWarehouseList);
                return uuid;
            }
        }
    }

    public synchronized boolean ifWarehousePresent(String playerUuid, String warehouseUuid) {
        if (warehouseStorage.containsKey(playerUuid)) {
            List<String> warehouseList = warehouseStorage.get(playerUuid);
            return warehouseList.contains(warehouseUuid);
        }
        return false;
    }
    public synchronized List<String> findPlayersWarehouses(String playerUuid) {
        if (warehouseStorage.containsKey(playerUuid)) {
            return warehouseStorage.get(playerUuid);
        }
        return Collections.emptyList();
    }

    public synchronized List<String> findAllWarehouses() {
        return this.warehouseStorage.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
