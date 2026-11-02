package me.asowneryt.death_improvements_plus.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.asowneryt.death_improvements_plus.DIP;
import me.asowneryt.death_improvements_plus.access.PlayerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if <26.1 'gamerules.GameRules' -> 'GameRules'
import net.minecraft.world.level.gamerules.GameRules;

//? >=26.1 {
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import me.asowneryt.death_improvements_plus.access.ItemEntityAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.asowneryt.death_improvements_plus.util.ModUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
*///?}

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerAccess {

    @Shadow public abstract Component getDisplayName();

    @Unique Player dip$self = (Player) (Object) this;
    @Unique BlockPos dip$lastSafePos = BlockPos.ZERO;
    @Unique boolean dip$startedTrackingSafePos = false;
    @Unique int dip$delayBeforeTracking = 100;

    //? <26.1 {
    /*@Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;setPickUpDelay(I)V"
            )
    )
    private void preventItemDespawnOnDeath(
            ItemStack droppedItem,
            boolean dropAround,
            boolean includeThrowerName,
            CallbackInfoReturnable<ItemEntity> cir,
            @Local ItemEntity itemEntity
    ) {
        if (dip$self.isDeadOrDying()) {
            if (DIP.CONFIG.noDeathItemDespawn.get()) {
                itemEntity.setUnlimitedLifetime();
                DIP.debugLog("Set infinite lifetime to items dropped by player {}", dip$self.getDisplayName().getString());
            } else ((ItemEntityAccess) itemEntity).dip$setLifetime(DIP.CONFIG.itemDespawnTimer.get() * 20);
        }
    }

    @ModifyArgs(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
            )
    )
    private void preventItemThrowOnDeath(Args args) {
        if (DIP.CONFIG.noItemSplatterOnDeath.get() && dip$self.isDeadOrDying()) {
            args.setAll(0.0d, 0.0d, 0.0d);
            DIP.debugLog("Prevented item splatter for player {}", dip$self.getDisplayName().getString());
        }
    }

    @WrapOperation(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity trySaveItemsOnDeath(Level level, double posX, double posY, double posZ, ItemStack itemStack, Operation<ItemEntity> original) {
		return ModUtil.trySaveItemsOnDeath(dip$self, dip$lastSafePos, level, posX, posY, posZ, itemStack, original);
    }
    *///?}

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void trackLastSafeSpot(CallbackInfo ci) {
        BlockPos pos = dip$self.getOnPos();
        if (dip$delayBeforeTracking > 0) dip$delayBeforeTracking--;
        else if (pos != BlockPos.ZERO && dip$self.getBlockStateOn().entityCanStandOn(dip$self.level(), pos, dip$self) && !dip$self.isInLava()) {
            if (!dip$startedTrackingSafePos) {
                dip$startedTrackingSafePos = true;
				DIP.debugLog("Started tracking last safe position for player {}", dip$self.getDisplayName().getString());
            }
            dip$lastSafePos = pos;
        }
    }

    @WrapMethod(method = "getBaseExperienceReward")
    private int modifyDroppedXpOnDeath(/*? >=26.1 {*/ServerLevel level, /*?}*/Operation<Integer> original) {
        //~ if <26.1 'get(GameRules.KEEP_INVENTORY)' -> 'getBoolean(GameRules.RULE_KEEPINVENTORY)'
        //~ if <26.1 'level' -> 'dip$self.level()'
        if (DIP.CONFIG.playerDropMoreXpOnDeath.get() && !level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            int xp = 0;
            int xpLevel = dip$self.experienceLevel;
            for (int i = 0; i < xpLevel; i++) {
                dip$self.experienceLevel = i;
                xp += dip$self.getXpNeededForNextLevel();
            }
            dip$self.experienceLevel = xpLevel;
            xp += (int) (dip$self.experienceProgress * dip$self.getXpNeededForNextLevel());
            return (int) (xp * (float) DIP.CONFIG.droppedExperiencePercent.get() / 100);
        }
        return original.call(/*? >=26.1 {*/level/*?}*/);
    }

    //? if 1.21.1 {
    /*@Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveLastSafeSpot(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("LastSafePosX", dip$lastSafePos.getX());
        compound.putInt("LastSafePosY", dip$lastSafePos.getY());
        compound.putInt("LastSafePosZ", dip$lastSafePos.getZ());
        DIP.debugLog("Saved safe position {} {} {} for player {}", dip$lastSafePos.getX(), dip$lastSafePos.getY(), dip$lastSafePos.getZ(), getDisplayName().getString());
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void loadLastSafeSpot(CompoundTag compound, CallbackInfo ci) {
        int x = compound.getInt("LastSafePosX");
        int y = compound.getInt("LastSafePosY");
        int z = compound.getInt("LastSafePosZ");
        dip$lastSafePos = new BlockPos(x, y, z);
        DIP.debugLog("Loaded safe position {} {} {} for player {}", x, y, z, getDisplayName().getString());
    }
    *///?} else {
    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void saveLastSafeSpot(ValueOutput output, CallbackInfo ci) {
        output.putInt("LastSafePosX", dip$lastSafePos.getX());
        output.putInt("LastSafePosY", dip$lastSafePos.getY());
        output.putInt("LastSafePosZ", dip$lastSafePos.getZ());
        DIP.debugLog("Saved safe position {} {} {} for player {}", dip$lastSafePos.getX(), dip$lastSafePos.getY(), dip$lastSafePos.getZ(), getDisplayName().getString());
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void loadLastSafeSpot(ValueInput input, CallbackInfo ci) {
        int x = input.getIntOr("LastSafePosX", 0);
        int y = input.getIntOr("LastSafePosY", 0);
        int z = input.getIntOr("LastSafePosZ", 0);
        dip$lastSafePos = new BlockPos(x, y, z);
        DIP.debugLog("Loaded safe position {} {} {} for player {}", x, y, z, getDisplayName().getString());
    }
    //?}

    @Override
    public BlockPos dip$getLastSafeBlockPosition() {
        return dip$lastSafePos;
    }
}
