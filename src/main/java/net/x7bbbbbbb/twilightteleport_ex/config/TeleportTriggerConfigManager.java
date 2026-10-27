package net.x7bbbbbbb.twilightteleport_ex.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.x7bbbbbbb.twilightteleport_ex.TwilightTeleportEX;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TeleportTriggerConfigManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final long RELOAD_CHECK_INTERVAL_MS = 5_000L;

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("twilightteleport_ex-server.json");

    private static volatile TeleportTriggerConfig config =
            TeleportTriggerConfig.defaults();

    private static boolean initialised;
    private static long nextCheckAtMillis;
    private static long loadedModifiedTime = Long.MIN_VALUE;

    private TeleportTriggerConfigManager() {
    }

    public static TeleportTriggerConfig get() {
        long now = System.currentTimeMillis();

        if (!initialised || now >= nextCheckAtMillis) {
            refresh(now);
        }

        return config;
    }

    private static synchronized void refresh(long now) {
        nextCheckAtMillis = now + RELOAD_CHECK_INTERVAL_MS;

        if (!Files.exists(CONFIG_PATH)) {
            if (!initialised) {
                config = TeleportTriggerConfig.defaults();
                save();
                initialised = true;
            }

            return;
        }

        long modifiedTime;

        try {
            modifiedTime = Files.getLastModifiedTime(CONFIG_PATH)
                    .toMillis();
        } catch (IOException exception) {
            initialised = true;
            return;
        }

        if (initialised && modifiedTime == loadedModifiedTime) {
            return;
        }

        initialised = true;
        loadedModifiedTime = modifiedTime;

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            TeleportTriggerConfig loaded = GSON.fromJson(
                    reader,
                    TeleportTriggerConfig.class
            );

            if (loaded == null) {
                loaded = TeleportTriggerConfig.defaults();
            }

            loaded.sanitize();
            warnAboutUnknownMods(loaded);
            config = loaded;
        } catch (Exception exception) {
            TwilightTeleportEX.LOGGER.warn(
                    "Failed to load twilightteleport_ex-server.json. "
                            + "Keeping the previous values.",
                    exception
            );
        }
    }

    private static void warnAboutUnknownMods(TeleportTriggerConfig loaded) {
        for (String modId : loaded.getBlacklistedModSet()) {
            if (!FabricLoader.getInstance().isModLoaded(modId)) {
                TwilightTeleportEX.LOGGER.warn(
                        "Blacklisted mod id '{}' is not a loaded mod.",
                        modId
                );
            }
        }
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }

            loadedModifiedTime = Files.getLastModifiedTime(CONFIG_PATH)
                    .toMillis();
        } catch (IOException exception) {
            TwilightTeleportEX.LOGGER.warn(
                    "Failed to save twilightteleport_ex-server.json.",
                    exception
            );
        }
    }
}
