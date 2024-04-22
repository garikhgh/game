package org.example.storage;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.MaterialDto;
import org.example.domain.MaterialEntity;
import org.example.domain.MaterialType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class MaterialStorage {

    CopyOnWriteArrayList<MaterialEntity> material = new CopyOnWriteArrayList<>();

    public synchronized int getMaterialCount() {
        return this.material.size();
    }

    //
    public synchronized boolean addMaterial(MaterialEntity material) {
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
    }
    public synchronized boolean isMaterialPresent(String materialUuid) {
        Optional<MaterialEntity> any = this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(materialUuid))
                .findAny();
        return any.isPresent();
    }

    public synchronized boolean removeMaterial(MaterialEntity material) {
        return this.material.removeIf(v->v.getMaterialUuid().equals(material.getMaterialUuid()));
    }

    public Optional<MaterialEntity> findMaterialByMaterialUuid(String materialUuid) {
        return this.material.stream()
                .filter(f->f.getMaterialUuid().equals(materialUuid))
                .findFirst();
    }
    public List<MaterialEntity> findMaterialEntitiesByWarehouseUuid(String warehouseUuid) {
        return this.material.stream()
                .filter(f->f.getWarehouseUuid().equals(warehouseUuid))
                .collect(Collectors.toList());
    }
    public List<MaterialEntity> findMaterialByPlayerUuid(String playerUuid) {
        return this.material.stream()
                .filter(f->f.getPlayerUuid().equals(playerUuid))
                .collect(Collectors.toList());
    }
    public synchronized List<MaterialEntity> findMaterialByMaterialType(MaterialType materialType) {
        return this.material.stream()
                .filter(f->f.getMaterialType().equals(materialType))
                .collect(Collectors.toList());
    }
    public synchronized Optional<MaterialEntity> findMaterialByMaterialTypeAndWarehouseUuid(String warehouseUuid, MaterialType materialType) {
        return this.material.stream()
                .filter(f->f.getMaterialType().equals(materialType))
                .filter(f->f.getWarehouseUuid().equals(warehouseUuid))
                .findFirst();
    }

    public synchronized boolean moveIfMaterialPresent(String playerUuid, String hostWarehouse, String fromWarehouse, MaterialDto transferMaterial) {
        Optional<MaterialEntity> hostMaterialOptional = this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(hostWarehouse))
                .findAny();

        Optional<MaterialEntity> fromMaterialOptional = this.material.stream()
                .filter(f -> f.getWarehouseUuid().equals(fromWarehouse))
                .findAny();

        if (hostMaterialOptional.isPresent() && fromMaterialOptional.isPresent()) {
            MaterialEntity hostMaterial = hostMaterialOptional.get();
            MaterialEntity fromMaterial = fromMaterialOptional.get();

            int min = Math.min(fromMaterial.getCurrentValue(), transferMaterial.getMaterialValue());
            fromMaterial.setCurrentValue(fromMaterial.getCurrentValue() - min);

            int max = Math.max(hostMaterial.getCurrentValue() + min, hostMaterial.getMaxCapacity());
            int valueRollBack = max - hostMaterial.getMaxCapacity() ;
            if (valueRollBack>0){
                fromMaterial.setCurrentValue(fromMaterial.getCurrentValue() + valueRollBack);
            }
            hostMaterial.setCurrentValue(max);
        }
        return false;
    }
}
