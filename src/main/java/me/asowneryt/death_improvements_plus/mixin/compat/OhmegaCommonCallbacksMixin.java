package me.asowneryt.death_improvements_plus.mixin.compat;

//? >=26.1 {

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.swacky.ohmega.event.CommonCallbacks;
import me.asowneryt.death_improvements_plus.DIP;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("ohmega")
@Mixin(CommonCallbacks.class)
public class OhmegaCommonCallbacksMixin {

    @ModifyExpressionValue(
            method = "onPlayerDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            )
    )
    private static boolean keepAtlas(boolean original, @Local ItemStack stack) {
        return DIP.CONFIG.keepAccessoriesOnDeath.get() ? !DIP.CONFIG.accessoryDropList.contains(DIP.getItemId(stack)) : original;
    }
}
//?}
