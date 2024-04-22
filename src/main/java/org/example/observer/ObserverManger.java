package org.example.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObserverManger {
    Map<String, List<MaterialObserver>> listeners = new HashMap<>();

    public ObserverManger(String... operations) {
        for (String operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    public void subscribe(String eventType, MaterialObserver eventListener) {
        List<MaterialObserver> players = listeners.get(eventType);
        players.add(eventListener);
    }

    public void notify(String eventType, String playerUuid, String warehouseUuid, String materialUuid, String materialName, int currentValue, int toAddValue) {
        List<MaterialObserver> player = listeners.get(eventType);
        for (MaterialObserver listener: player) {
            listener.sendNotification(eventType, playerUuid, warehouseUuid, materialUuid, materialName, currentValue,  toAddValue);
        }
    }
}
