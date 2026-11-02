package me.asowneryt.death_improvements_plus.platform;

//$ loader_util_import
import me.asowneryt.death_improvements_plus.platform.fabric.FabricLoaderUtil;

public interface MultiLoaderUtil {
    MultiLoaderUtil INSTANCE = /*$ loader_util_inst*/ new FabricLoaderUtil();

    boolean isModLoaded(String modId);
    boolean isDevEnv();
}
