package net.x7bbbbbbb.twilightteleport_ex.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import java.util.List;

public class TwilightTeleportEXMixinCanceller implements MixinCanceller {

    private static final List<String> BLOCKED_MIXINS = List.of(
            "net.ochibo.twilightteleport.mixin.ServerPlayNetworkHandlerTeleportMixin",
            "net.ochibo.twilightteleport.mixin.ServerPlayerEntityTeleportMixin"
    );

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return BLOCKED_MIXINS.contains(mixinClassName);
    }
}