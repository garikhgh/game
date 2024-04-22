package org.example.domain;


import lombok.Getter;
import lombok.Setter;
import org.example.exception.MaterialMaxCapacityExceedingException;
import org.example.exception.MaterialNegativeValueException;
import org.example.observer.NotificationSender;
import org.example.observer.ObserverManger;

import static org.example.constants.ConstantValues.*;

@Setter
@Getter
public class MaterialEntity implements ObserverInstanceInvoker{


    private transient ObserverManger observerManger;

    private String materialUuid;
    private String warehouseUuid;
    private String playerUuid;

    private MaterialType materialType;

    private String name;
    private String description;
    private String icon;

    private int maxCapacity;
    private int currentValue;


    public void setCurrentValue(int cV) throws MaterialNegativeValueException, MaterialMaxCapacityExceedingException {

        if (cV < 0)  {
            throw new MaterialNegativeValueException("Material value could not be negative.");
        }
        if (cV > this.maxCapacity) {
            String s = String.format("Material max capacity %s could not be exceeded", this.maxCapacity);
            throw  new MaterialMaxCapacityExceedingException(s);
        }
        if (this.currentValue > cV) {
            this.currentValue = cV;
            this.observerManger.notify(MATERIAL_IS_SUBTRACTED,this.playerUuid, this.warehouseUuid, this.materialUuid, this.materialType.name() );
        }
        if (this.currentValue < cV) {
            this.currentValue = cV;
            this.observerManger.notify(MATERIAL_IS_ADDED, this.playerUuid, this.warehouseUuid, this.materialUuid, this.materialType.name() );
        }
        if (this.currentValue  == 0) {
            this.currentValue = cV;
            this.observerManger.notify(MATERIAL_IS_ZERO,this.playerUuid, this.warehouseUuid, this.materialUuid, this.materialType.name() );
        }
        if (this.currentValue  == this.maxCapacity) {
            this.currentValue = cV;
            this.observerManger.notify(MATERIAL_IS_FULL,this.playerUuid, this.warehouseUuid, this.materialUuid, this.materialType.name() );
        }
    }

    public MaterialEntity() {
        invokeObserverInstance();
    }


    @Override
    public void invokeObserverInstance() {
        this.observerManger = new ObserverManger(MATERIAL_IS_ADDED, MATERIAL_IS_FULL, MATERIAL_IS_ZERO, MATERIAL_IS_SUBTRACTED);
        subscribe();
    }

    public void subscribe() {
        observerManger.subscribe(MATERIAL_IS_FULL, new NotificationSender());
        observerManger.subscribe(MATERIAL_IS_ZERO, new NotificationSender());
        observerManger.subscribe(MATERIAL_IS_SUBTRACTED, new NotificationSender());
        observerManger.subscribe(MATERIAL_IS_ADDED, new NotificationSender());
    }
}
