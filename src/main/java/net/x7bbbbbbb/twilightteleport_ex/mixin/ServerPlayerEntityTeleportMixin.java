package net.x7bbbbbbb.twilightteleport_ex.mixin;

import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.ochibo.twilightteleport.server.PendingTeleportManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityTeleportMixin {

    @Inject(
            method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void twilightTeleportEx$interceptDimensionTeleport(
            ServerWorld destinationWorld,
            double destinationX,
            double destinationY,
            double destinationZ,
            float yaw,
            float pitch,
            CallbackInfo ci
    ) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        boolean intercepted =
                PendingTeleportManager.interceptExternalTeleport(
                        player,
                        destinationWorld,
                        destinationX,
                        destinationY,
                        destinationZ,
                        () -> {
                            player.teleport(
                                    destinationWorld,
                                    destinationX,
                                    destinationY,
                                    destinationZ,
                                    yaw,
                                    pitch
                            );
                            return true;
                        }
                );

        if (intercepted) {
            ci.cancel();
        }
    }

    @Inject(
            method = "teleport(Lnet/minecraft/server/world/ServerWorld;"
                    + "DDDLjava/util/Set;FF)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void twilightTeleportEx$interceptExternalTeleport(
            ServerWorld destinationWorld,
            double destinationX,
            double destinationY,
            double destinationZ,
            Set<PositionFlag> flags,
            float yaw,
            float pitch,
            CallbackInfoReturnable<Boolean> cir
    ) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        Set<PositionFlag> copiedFlags = Set.copyOf(flags);

        boolean intercepted =
                PendingTeleportManager.interceptExternalTeleport(
                        player,
                        destinationWorld,
                        destinationX,
                        destinationY,
                        destinationZ,
                        () -> player.teleport(
                                destinationWorld,
                                destinationX,
                                destinationY,
                                destinationZ,
                                copiedFlags,
                                yaw,
                                pitch
                        )
                );

        if (intercepted) {
            cir.setReturnValue(true);
        }
    }
}
