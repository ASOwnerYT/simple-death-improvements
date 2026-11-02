package me.asowneryt.death_improvements_plus.platform.fabric;

//? fabric {

import me.asowneryt.death_improvements_plus.platform.MultiLoaderUtil;
import net.fabricmc.loader.api.FabricLoader;

public class FabricLoaderUtil implements MultiLoaderUtil {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
//?}
