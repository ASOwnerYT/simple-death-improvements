package me.asowneryt.death_improvements_plus.compat;

//? neoforge {

/*import me.asowneryt.death_improvements_plus.DIP;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.event.DropRulesEvent;

//~ if <26.1 'common.DropRule' -> 'type.capability.ICurio'
import top.theillusivec4.curios.api.common.DropRule;

public class CuriosCompat implements AccessoryUtil {

    @Override
    public void registerDropEvent() {
        NeoForge.EVENT_BUS.addListener(DropRulesEvent.class, event ->
                event.addOverride(
                        stack ->
                                DIP.CONFIG.keepAccessoriesOnDeath.get() &&
                                !DIP.CONFIG.accessoryDropList.contains(DIP.getItemId(stack)),
                        /^? <26.1 {^//^ICurio.^//^?}^/DropRule.ALWAYS_KEEP
                )
        );
    }
}
*///?}
