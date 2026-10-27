package net.x7bbbbbbb.twilightteleport_ex;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwilightTeleportEX implements ModInitializer {
	public static final String MOD_ID = "twilightteleport_ex";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[TwilightTeleport EX] TwilightTeleport is being modified...");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
