package com.monkey.mobattack.utils;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private final HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(UUID playerId, String action, int seconds, boolean bypass) {
        if (bypass) return false;

        long now = System.currentTimeMillis();
        cooldowns.putIfAbsent(playerId, new HashMap<>());
        HashMap<String, Long> playerCooldowns = cooldowns.get(playerId);

        if (playerCooldowns.containsKey(action)) {
            long expire = playerCooldowns.get(action);
            if (now < expire) {
                return true;
            }
        }

        playerCooldowns.put(action, now + seconds * 1000L);
        return false;
    }

    public long getRemainingTime(UUID playerId, String action) {
        HashMap<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null || !playerCooldowns.containsKey(action)) return 0;
        long expire = playerCooldowns.get(action);
        long now = System.currentTimeMillis();
        return Math.max(0, (expire - now) / 1000);
    }
}
