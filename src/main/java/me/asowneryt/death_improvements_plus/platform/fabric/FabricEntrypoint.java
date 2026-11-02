package me.asowneryt.death_improvements_plus.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import me.asowneryt.death_improvements_plus.DIP;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		DIP.onInitialize();
	}
}
//?}
