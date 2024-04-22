package org.example.storage;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.MaterialDto;
import org.example.domain.MaterialEntity;
import org.example.domain.MaterialType;
import org.example.exception.MaterialMaxCapacityExceedingException;
import org.example.exception.MaterialNegativeValueException;
import org.example.observer.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class MaterialStorage {

    private final Logger logger = LoggerFactory.getLogger(MaterialStorage.class);

    CopyOnWriteArrayList<MaterialEntity> material = new CopyOnWriteArrayList<>();

    public synchronized int getMaterialCount() {
        return this.material.size();
    }

    //
    public synchronized boolean addMaterial(MaterialEntity material) {
        try {
            Optional<MaterialEntity> any = this.material.stream()
                    .filter(f -> f.getWarehouseUuid().equals(material.getMaterialUuid()))
                    .findAny();
            if (any.isPresent()) {
                MaterialEntity materialEntity = any.get();
                int i = materialEntity.getCurrentValue() + material.getCurrentValue();
                materialEntity.setCurrentValue(Math.min(i, materialEntity.getMaxCapacity()));
                return true;
            } else {
                this.material.add(material);
                return true;
            }
        } catch (MaterialNegativeValueException | MaterialMaxCapacityExceedingException e) {
            logger.error("Material value exception, {}",  e.getMessage());
            return false;
        }

    }

    public synchronized boolean isMaterialPresent(String materialUuid) {
        Optional<MaterialEntity> any = this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(materialUuid))
                .findAny();
        return any.isPresent();
    }

    public synchronized boolean removeMaterial(MaterialEntity material) {
        return this.material.removeIf(v -> v.getMaterialUuid().equals(material.getMaterialUuid()));
    }

    public Optional<MaterialEntity> findMaterialByMaterialUuid(String materialUuid) {
        return this.material.stream()
                .filter(f -> f.getMaterialUuid().equals(materialUuid))
                .findFirst();
    }

    public List<MaterialEntity> findMaterialEntitiesByWarehouseUuid(String warehouseUuid) {
        return this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(warehouseUuid))
                .collect(Collectors.toList());
    }

    public List<MaterialEntity> findMaterialByPlayerUuid(String playerUuid) {
        return this.material.stream()
                .filter(f -> f.getPlayerUuid().equals(playerUuid))
                .collect(Collectors.toList());
    }

    public synchronized List<MaterialEntity> findMaterialByMaterialType(MaterialType materialType) {
        return this.material.stream()
                .filter(f -> f.getMaterialType().equals(materialType))
                .collect(Collectors.toList());
    }

    public synchronized Optional<MaterialEntity> findMaterialByMaterialTypeAndWarehouseUuid(String warehouseUuid, MaterialType materialType) {
        return this.material.stream()
                .filter(f -> f.getMaterialType().equals(materialType))
                .filter(f -> f.getWarehouseUuid().equals(warehouseUuid))
                .findFirst();
    }

    public synchronized List<MaterialEntity> getMaterialsOfPlayerByPlayerUuid(String playerUuid) {
        return this.material.stream()
                .filter(f -> f.getPlayerUuid().equals(playerUuid))
                .collect(Collectors.toList());
    }

    public synchronized Optional<MaterialEntity> findMaterialByWarehouseUuidAndMaterialUuid(String warehouseUuid, String materialUuid) {
        return this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(warehouseUuid))
                .filter(f -> f.getMaterialUuid().equals(materialUuid))
                .findAny();
    }

    public synchronized boolean moveIfMaterialPresent(String playerUuid, String hostWarehouse, String fromWarehouse, MaterialDto transferMaterial) {

        Optional<MaterialEntity> hostMaterialOptional = this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(hostWarehouse))
                .findAny();

        if (hostMaterialOptional.isEmpty()) {
            // add material in order to host the material;
            MaterialEntity materialEntity = composeMaterial(transferMaterial.getMaterialType(), hostWarehouse, playerUuid);
            this.addMaterial(materialEntity);
        }
        Optional<MaterialEntity> fromMaterialOptional = findMaterialByWarehouseUuidAndMaterialUuid(fromWarehouse, transferMaterial.getMaterialUuid());
        // getting created material in order to hose the transfer material
        hostMaterialOptional = this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(hostWarehouse))
                .findAny();


        if (fromMaterialOptional.isPresent()) {
            MaterialEntity hostMaterial = hostMaterialOptional.get();
            MaterialEntity fromMaterial = fromMaterialOptional.get();

            try {
                int min = Math.min(fromMaterial.getCurrentValue(), transferMaterial.getMaterialValue());
                fromMaterial.setCurrentValue(fromMaterial.getCurrentValue() - min);

                int max = Math.min(hostMaterial.getMaxCapacity() - hostMaterial.getCurrentValue(), min);
                int valueRollBack = max - hostMaterial.getMaxCapacity();
                // should be checked this rollback case
                if (valueRollBack > 0) {
                    fromMaterial.setCurrentValue(fromMaterial.getCurrentValue() + valueRollBack);
                }
                hostMaterial.setCurrentValue(hostMaterial.getCurrentValue() + max);
                return true;
            } catch (MaterialNegativeValueException | MaterialMaxCapacityExceedingException e) {

                logger.error("Material value exception, {}",  e.getMessage());
                return false;
            }


        }
        return false;
    }

    public synchronized boolean deleteMaterialWhenPlayerIsDeleted(String playerUuid) {
        List<MaterialEntity> collect = this.material.stream().filter(f -> f.getPlayerUuid().equals(playerUuid))
                .toList();
        return this.material.removeAll(collect);
    }

    // could be added a method to edit material metadata such as name, icon, description and so on

    private MaterialEntity composeMaterial(MaterialType materialType, String warehouseUuid, String playerUuid) {
        MaterialEntity material = new MaterialEntity();
        try {
            material.setMaterialType(materialType);
            material.setWarehouseUuid(warehouseUuid);
            material.setMaterialUuid(UUID.randomUUID().toString());
            material.setCurrentValue(0);
            material.setIcon(materialType.name() + " Icon");
            material.setName(materialType.name() + "_Name");
            material.setDescription(materialType.name() + " description");
            material.setPlayerUuid(playerUuid);
            material.setMaxCapacity(materialType.getMaxCapacity());
            return material;
        } catch (MaterialNegativeValueException | MaterialMaxCapacityExceedingException e) {
            logger.error("Material value exception, {}",  e.getMessage());
            return material;
        }
    }
}
