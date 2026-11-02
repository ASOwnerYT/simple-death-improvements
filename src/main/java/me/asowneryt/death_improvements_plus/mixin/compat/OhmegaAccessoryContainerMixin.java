package me.asowneryt.death_improvements_plus.mixin.compat;

//? <26.1 {

/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import me.asowneryt.death_improvements_plus.DIP;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("ohmega")
@Mixin(AccessoryContainer.class)
public class OhmegaAccessoryContainerMixin {

    @ModifyExpressionValue(
            method = "removeStackFromSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            )
    )
    private static boolean keepAtlas(boolean original, @Local ItemStack stack, @Local boolean fromDeath) {
        return (fromDeath && DIP.CONFIG.keepAccessoriesOnDeath.get() && !DIP.CONFIG.accessoryDropList.contains(DIP.getItemId(stack))) || original;
    }
}
*///?}
