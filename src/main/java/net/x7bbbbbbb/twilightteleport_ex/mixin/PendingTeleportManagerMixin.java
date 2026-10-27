package net.x7bbbbbbb.twilightteleport_ex.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.ochibo.twilightteleport.server.PendingTeleportManager;
import net.x7bbbbbbb.twilightteleport_ex.server.TeleportAnimationFilter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = PendingTeleportManager.class, remap = false)
public abstract class PendingTeleportManagerMixin {

    @Shadow
    @Final
    private static Map<UUID, ?> SESSIONS;

    @Shadow
    private static boolean shouldBypassInterception(
            ServerPlayerEntity player
    ) {
        throw new AssertionError();
    }

    @Inject(
            method = "interceptExternalTeleport",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void twilightTeleport$filterAnimationTrigger(
            ServerPlayerEntity player,
            ServerWorld destinationWorld,
            double destinationX,
            double destinationY,
            double destinationZ,
            PendingTeleportManager.DeferredTeleportAction action,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (shouldBypassInterception(player)
                || SESSIONS.containsKey(player.getUuid())) {
            return;
        }

        if (TeleportAnimationFilter.shouldSkipAnimation(
                player,
                destinationWorld,
                destinationX,
                destinationZ
        )) {
            cir.setReturnValue(false);
        }
    }
}
