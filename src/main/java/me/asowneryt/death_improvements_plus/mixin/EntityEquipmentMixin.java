package me.asowneryt.death_improvements_plus.mixin;

//? >=26.1 {

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.asowneryt.death_improvements_plus.DIP;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.EnumMap;

//? >26.2
import net.minecraft.util.Prediction;

@Mixin(EntityEquipment.class)
public abstract class EntityEquipmentMixin {

    @Shadow @Final private EnumMap<EquipmentSlot, ItemStack> items;

    @WrapMethod(method = "dropAll")
    private void keepOffhandAndEquipment(LivingEntity dropper, Operation<Void> original) {
        if (dropper instanceof Player && (DIP.CONFIG.keepHotbarOnDeath.get() || DIP.CONFIG.keepArmorOnDeath.get() || DIP.CONFIG.keepOffhandOnDeath.get())) {
            items.replaceAll((slot, stack) -> {
                if (DIP.CONFIG.keepOffhandOnDeath.get() && slot.equals(EquipmentSlot.OFFHAND) &&
                        !DIP.CONFIG.offhandDropList.contains(DIP.getItemId(stack))) return stack;
                if (DIP.CONFIG.keepArmorOnDeath.get() && !DIP.CONFIG.armorDropList.contains(DIP.getItemId(stack)) && (
                        slot.equals(EquipmentSlot.HEAD) ||
                        slot.equals(EquipmentSlot.CHEST) ||
                        slot.equals(EquipmentSlot.LEGS) ||
                        slot.equals(EquipmentSlot.FEET)
                )) return stack;
                //~ if >26.2 'false' -> 'Prediction.SERVER_ONLY'
                dropper.drop(stack, !DIP.CONFIG.noItemSplatterOnDeath.get(), Prediction.SERVER_ONLY);
                return ItemStack.EMPTY;
            });
        }
        else original.call(dropper);
    }

    @ModifyArg(
            method = "dropAll",
            at = @At(
                    value = "INVOKE",
                    //~ if >26.2 'drop' -> 'createItemStackToDrop'
                    target = "Lnet/minecraft/world/entity/LivingEntity;createItemStackToDrop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"
            ),
            index = 1
    )
    private boolean preventItemSplatterOnDeath(boolean dropAround) {
		return !DIP.CONFIG.noItemSplatterOnDeath.get() && dropAround;
    }
}
//?}
