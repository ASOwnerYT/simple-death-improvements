package me.asowneryt.death_improvements_plus.mixin;

import me.asowneryt.death_improvements_plus.access.ExperienceOrbAccess;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin implements ExperienceOrbAccess {

    //? <26.1 {
    /*@Shadow private int value;
    *///?} else {
	@Shadow /*? fabric {*/protected/*?} else {*//*public*//*?}*/ abstract void setValue(int value);
    //?}

	@Override
	public void dip$setValue(int value) {
        //? <26.1 {
        /*this.value = value;
        *///?} else {
		setValue(value);
        //?}
	}
}
