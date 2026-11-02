package me.asowneryt.death_improvements_plus.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.asowneryt.death_improvements_plus.DIP;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//? <26.1 {
/*import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;
*///?}

@Mixin(Inventory.class)
public class InventoryMixin {

    //? <26.1 {
    /*@Shadow @Final public NonNullList<ItemStack> items;
    @Shadow @Final public NonNullList<ItemStack> armor;
    @Shadow @Final public NonNullList<ItemStack> offhand;
    *///?}

    @ModifyArg(
            method = "dropAll",
            at = @At(
                    value = "INVOKE",
                    //~ if >26.2 'drop' -> 'createItemStackToDrop'
                    target = "Lnet/minecraft/world/entity/player/Player;createItemStackToDrop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"
            ),
            index = 1
    )
    private boolean preventItemSplatterOnDeath(boolean dropAround) {
		return !DIP.CONFIG.noItemSplatterOnDeath.get() && dropAround;
    }

    @ModifyExpressionValue(
            method = "dropAll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            )
    )
    //? <26.1 {
    /*private boolean keepItems(boolean original, @Local List<ItemStack> list, @Local ItemStack item, @Local int i) {
        if (DIP.CONFIG.keepArmorOnDeath.get() && list.equals(armor) && !DIP.CONFIG.armorDropList.contains(DIP.getItemId(item))) return true;
        if (DIP.CONFIG.keepHotbarOnDeath.get() && list.equals(items) && i < 9 && !DIP.CONFIG.hotbarDropList.contains(DIP.getItemId(item))) return true;
		if (DIP.CONFIG.keepOffhandOnDeath.get() && list.equals(offhand) && !DIP.CONFIG.offhandDropList.contains(DIP.getItemId(item))) return true;
        return original;
    }
    *///?} else {
    private boolean keepHotbarItems(boolean original, @Local ItemStack itemStack, @Local int i) {
        return DIP.CONFIG.keepHotbarOnDeath.get() && i < 9 && !DIP.CONFIG.hotbarDropList.contains(DIP.getItemId(itemStack)) || original;
    }
    //?}
}
