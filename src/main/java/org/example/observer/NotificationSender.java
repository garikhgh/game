package org.example.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationSender implements MaterialObserver {

    private final Logger logger = LoggerFactory.getLogger(NotificationSender.class);

    @Override
    public void sendNotification(String eventType, String playerUuid, String warehouseUuid, String materialUuid, String materialType, int currentValue, int toAddValue) {


        logger.info("######################################################################################");
        logger.info( "------------------------    NOTIFICATION: SENDING    --------------------------------");
        logger.info("Event, Player {} warehouse [{}], material  {}: MaterialType: {},  event= {}: CurrentValue {} -> {}.", playerUuid, warehouseUuid, materialUuid, materialType, eventType, currentValue, toAddValue);
        logger.info("######################################################################################");
    }
}
