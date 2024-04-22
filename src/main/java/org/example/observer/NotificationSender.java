package org.example.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationSender implements MaterialObserver {

    private final Logger logger = LoggerFactory.getLogger(NotificationSender.class);

    @Override
    public void sendNotification(String eventType, String playerUuid, String warehouseUuid, String materialUuid, String materialType) {


        String format = String.format("Event, Player %s warehouse [%s], material  %s: MaterialType: %s,  event= %s ", playerUuid, warehouseUuid, materialUuid, materialType, eventType);
        logger.info("######################################################################################");
        logger.info(format);
        logger.info( "---------------------PLAYER NOTIFICATION: SENDING....--------------------------------");
        logger.info("######################################################################################");
    }
}
