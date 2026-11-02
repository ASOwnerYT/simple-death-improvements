package me.asowneryt.death_improvements_plus.compat;

//? fabric && 1.21.1 {

/*import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.asowneryt.death_improvements_plus.DIP;

public class TrinketsLegacyCompat implements AccessoryUtil {

    @Override
    public void registerDropEvent() {
        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) ->
                DIP.CONFIG.keepAccessoriesOnDeath.get() && !DIP.CONFIG.accessoryDropList.contains(DIP.getItemId(stack)) ?
                        TrinketEnums.DropRule.KEEP : TrinketEnums.DropRule.DEFAULT);
    }
}
*///?}
