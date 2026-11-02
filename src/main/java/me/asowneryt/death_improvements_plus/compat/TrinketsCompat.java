package me.asowneryt.death_improvements_plus.compat;

//? >=26.1 {

import eu.pb4.trinkets.api.TrinketDropRule;
import eu.pb4.trinkets.api.event.TrinketDropCallback;
import me.asowneryt.death_improvements_plus.DIP;

public class TrinketsCompat implements AccessoryUtil {

    @Override
    public void registerDropEvent() {
        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) ->
                DIP.CONFIG.keepAccessoriesOnDeath.get() && !DIP.CONFIG.accessoryDropList.contains(DIP.getItemId(stack)) ?
                        TrinketDropRule.KEEP : TrinketDropRule.DEFAULT);
    }
}
//?}
