package org.example.observer;

public interface MaterialObserver {
    void sendNotification(String eventType, String playerUuid, String warehouseUuid, String materialUuid, String materialType, int currentValue, int toAddValue);
}
