package net.x7bbbbbbb.twilightteleport_ex.server;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportCallerResolver {

    public static final String VANILLA = "minecraft";

    private static final String[] OWN_PACKAGES = {
            "net.ochibo.twilightteleport.",
            "net.x7bbbbbbb.twilightteleport_ex."
    };

    private static final String[] IGNORED_PREFIXES = {
            "java.",
            "javax.",
            "jdk.",
            "sun.",
            "com.sun.",
            "net.minecraft.",
            "com.mojang.",
            "org.spongepowered.",
            "com.llamalad7.mixinextras.",
            "net.fabricmc.loader.",
            "io.netty.",
            "it.unimi.",
            "org.slf4j.",
            "org.apache."
    };

    private static final StackWalker WALKER = StackWalker.getInstance();

    private static final Map<String, String> CLASS_TO_MOD =
            new ConcurrentHashMap<>();

    private TeleportCallerResolver() {
    }

    public static Set<String> resolveCallerMods() {
        List<String> classNames = WALKER.walk(frames ->
                frames.map(StackWalker.StackFrame::getClassName)
                        .distinct()
                        .toList()
        );

        Set<String> modIds = new LinkedHashSet<>();

        for (String rawName : classNames) {
            String className = normalize(rawName);

            if (isIgnored(className)) {
                continue;
            }

            String modId = CLASS_TO_MOD.computeIfAbsent(
                    className,
                    TeleportCallerResolver::findOwningMod
            );

            if (!modId.isEmpty()) {
                modIds.add(modId);
            }
        }

        if (modIds.isEmpty()) {
            modIds.add(VANILLA);
        }

        return modIds;
    }

    private static String normalize(String className) {
        int slash = className.indexOf('/');

        if (slash > 0) {
            className = className.substring(0, slash);
        }

        int synthetic = className.indexOf("$$");

        if (synthetic > 0) {
            className = className.substring(0, synthetic);
        }

        return className;
    }

    private static boolean isIgnored(String className) {
        for (String ownPackage : OWN_PACKAGES) {
            if (className.startsWith(ownPackage)) {
                return true;
            }
        }

        for (String prefix : IGNORED_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    private static String findOwningMod(String className) {
        String classFile = className.replace('.', '/') + ".class";

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (mod.findPath(classFile).isPresent()) {
                return mod.getMetadata().getId();
            }
        }

        return "";
    }
}
