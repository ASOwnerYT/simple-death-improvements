package me.asowneryt.death_improvements_plus.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.asowneryt.death_improvements_plus.access.ItemEntityAccess;
import net.minecraft.world.entity.item.ItemEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemEntityAccess {

    @Unique private int dip$lifetime = 6000;

    @Override
    public void dip$setLifetime(int lifetime) {
        dip$lifetime = lifetime;
    }

    @ModifyExpressionValue(
            method = "tick",
            //? fabric {
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=6000"
            )
            //?} else {
            /*at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;lifespan:I",
                    opcode = Opcodes.GETFIELD
            )
            *///?}
    )
    private int modifyLifetime(int original) {
        return dip$lifetime != 6000 ? dip$lifetime : original;
    }
}
