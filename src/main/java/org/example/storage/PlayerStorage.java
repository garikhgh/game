package org.example.storage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Setter
@Getter
@NoArgsConstructor
public class PlayerStorage {

    private CopyOnWriteArrayList<String> players = new CopyOnWriteArrayList<>();

    public synchronized String createPlayer() {
        String uuid = UUID.randomUUID().toString();
        players.add(uuid);
        return uuid;
    }
    public synchronized boolean ifPlayerPresent(String playerUuid) {
        return players.contains(playerUuid);
    }
    public synchronized boolean removePlayerIfPresent(String playerUuid) {
        return players.removeIf(p -> p.equals(playerUuid));
    }
}
