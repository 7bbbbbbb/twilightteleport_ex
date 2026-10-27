package net.x7bbbbbbb.twilightteleport_ex.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class TeleportTriggerConfig {

    private int minHorizontalDistanceChunks = 7;

    private List<String> blacklistedMods = new ArrayList<>();

    private transient Set<String> blacklistedModSet = new HashSet<>();

    public static TeleportTriggerConfig defaults() {
        TeleportTriggerConfig config = new TeleportTriggerConfig();
        config.sanitize();
        return config;
    }

    public void sanitize() {
        if (minHorizontalDistanceChunks < 0) {
            minHorizontalDistanceChunks = 0;
        }

        if (blacklistedMods == null) {
            blacklistedMods = new ArrayList<>();
        }

        Set<String> normalized = new HashSet<>();

        for (String modId : blacklistedMods) {
            if (modId != null && !modId.isBlank()) {
                normalized.add(modId.strip().toLowerCase(Locale.ROOT));
            }
        }

        blacklistedModSet = normalized;
    }

    public int getMinHorizontalDistanceChunks() {
        return minHorizontalDistanceChunks;
    }

    public double getMinHorizontalDistanceBlocks() {
        return minHorizontalDistanceChunks * 16.0D;
    }

    public Set<String> getBlacklistedModSet() {
        return blacklistedModSet;
    }
}
