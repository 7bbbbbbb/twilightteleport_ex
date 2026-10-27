package net.x7bbbbbbb.twilightteleport_ex.server;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.x7bbbbbbb.twilightteleport_ex.config.TeleportTriggerConfig;
import net.x7bbbbbbb.twilightteleport_ex.config.TeleportTriggerConfigManager;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportAnimationFilter {

    private static final Set<String> API_BLACKLIST =
            ConcurrentHashMap.newKeySet();

    private TeleportAnimationFilter() {
    }

    public static void blacklistMod(String modId) {
        if (modId != null && !modId.isBlank()) {
            API_BLACKLIST.add(modId.strip().toLowerCase(Locale.ROOT));
        }
    }

    public static boolean shouldSkipAnimation(
            ServerPlayerEntity player,
            ServerWorld destinationWorld,
            double destinationX,
            double destinationZ
    ) {
        TeleportTriggerConfig config = TeleportTriggerConfigManager.get();

        if (isTooCloseHorizontally(
                config,
                player,
                destinationWorld,
                destinationX,
                destinationZ
        )) {
            return true;
        }

        Set<String> configBlacklist = config.getBlacklistedModSet();

        if (configBlacklist.isEmpty() && API_BLACKLIST.isEmpty()) {
            return false;
        }

        for (String modId : TeleportCallerResolver.resolveCallerMods()) {
            String key = modId.toLowerCase(Locale.ROOT);

            if (configBlacklist.contains(key)
                    || API_BLACKLIST.contains(key)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isTooCloseHorizontally(
            TeleportTriggerConfig config,
            ServerPlayerEntity player,
            ServerWorld destinationWorld,
            double destinationX,
            double destinationZ
    ) {
        double minBlocks = config.getMinHorizontalDistanceBlocks();

        if (minBlocks <= 0.0D
                || player.getServerWorld() != destinationWorld) {
            return false;
        }

        double dx = destinationX - player.getX();
        double dz = destinationZ - player.getZ();

        return dx * dx + dz * dz < minBlocks * minBlocks;
    }
}
