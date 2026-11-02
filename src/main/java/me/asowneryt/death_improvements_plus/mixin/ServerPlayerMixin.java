package me.asowneryt.death_improvements_plus.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import me.asowneryt.death_improvements_plus.DIP;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

//~ if <26.1 'gamerules.GameRules' -> 'GameRules'
import net.minecraft.world.level.gamerules.GameRules;

//? >=26.1 {
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Shadow;
//?}

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    //? >=26.1
    @Shadow public abstract @NotNull ServerLevel level();

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        //~ if <26.1 'level' -> 'level, blockPos, f'
        super(level, gameProfile);
    }

    @SuppressWarnings("resource")
	@WrapMethod(method = "restoreFrom")
    private void restoreItems(ServerPlayer oldPlayer, boolean restoreAll, Operation<Void> original) {
        if (
                !restoreAll && !oldPlayer.isSpectator() &&
                //~ if <26.1 'get(GameRules.KEEP_INVENTORY)' -> 'getBoolean(GameRules.RULE_KEEPINVENTORY)'
                !level().getGameRules().get(GameRules.KEEP_INVENTORY)
        ) {
            if (DIP.CONFIG.keepArmorOnDeath.get() || DIP.CONFIG.keepHotbarOnDeath.get() || DIP.CONFIG.keepOffhandOnDeath.get()) {
                getInventory().replaceWith(oldPlayer.getInventory());
            }
        }
        original.call(oldPlayer, restoreAll);
    }
}
