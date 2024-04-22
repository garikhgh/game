package org.example.observer;

public class NotificationSender implements MaterialObserver {

    @Override
    public void sendNotification(String eventType, String playerUuid, String warehouseUuid, String materialUuid, String materialType) {
        String format = String.format("Event, Player %s warehouse [%s], material  %s: MaterialType: %s,  event= %s ", playerUuid, warehouseUuid, materialUuid, eventType);
        System.out.println("######################################################################################");
        System.out.println(format);
        System.out.println( "---------------------PLAYER NOTIFICATION: SENDING....--------------------------------");
        System.out.println("######################################################################################");
    }
}
