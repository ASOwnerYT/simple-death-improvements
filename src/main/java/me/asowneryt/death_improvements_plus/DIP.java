package me.asowneryt.death_improvements_plus;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.asowneryt.death_improvements_plus.compat.AccessoryUtil;
import me.asowneryt.death_improvements_plus.config.ModConfig;
import me.asowneryt.death_improvements_plus.platform.MultiLoaderUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DIP {

    public static final String MOD_ID = /*$ mod_id*/ "death_improvements_plus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);

    public static void onInitialize() {
        if (AccessoryUtil.INSTANCE != null) AccessoryUtil.INSTANCE.registerDropEvent();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Identifier getItemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    public static void debugLog(String message, Object ... args) {
        if (MultiLoaderUtil.INSTANCE.isDevEnv()) LOGGER.info(message, args);
    }
}
