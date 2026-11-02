package me.asowneryt.death_improvements_plus.paper;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record DIPConfig(
        boolean noItemSplatterOnDeath,
        boolean noDeathItemDespawn,
        int itemDespawnTimer,
        boolean tryItemLavaSaveOnDeath,
        boolean tryItemVoidSaveOnDeath,
        boolean explosionResistantItems,
        boolean noXpSplatterOnDeath,
        boolean playerDropMoreXpOnDeath,
        int droppedExperiencePercent,
        boolean keepArmorOnDeath,
        Set<NamespacedKey> armorDropList,
        boolean keepHotbarOnDeath,
        Set<NamespacedKey> hotbarDropList,
        boolean keepOffhandOnDeath,
        Set<NamespacedKey> offhandDropList,
        boolean debug
) {

    public static DIPConfig load(FileConfiguration config) {
        return new DIPConfig(
                config.getBoolean("no-item-splatter-on-death", true),
                config.getBoolean("no-death-item-despawn", true),
                Math.max(1, config.getInt("item-despawn-timer", 300)),
                config.getBoolean("try-item-lava-save-on-death", true),
                config.getBoolean("try-item-void-save-on-death", true),
                config.getBoolean("explosion-resistant-items", true),
                config.getBoolean("no-xp-splatter-on-death", true),
                config.getBoolean("player-drop-more-xp-on-death", true),
                Math.clamp(config.getInt("dropped-experience-percent", 80), 1, 100),
                config.getBoolean("keep-armor-on-death", false),
                keys(config, "armor-drop-list"),
                config.getBoolean("keep-hotbar-on-death", false),
                keys(config, "hotbar-drop-list"),
                config.getBoolean("keep-offhand-on-death", false),
                keys(config, "offhand-drop-list"),
                config.getBoolean("debug", false)
        );
    }

    private static Set<NamespacedKey> keys(FileConfiguration config, String path) {
        return config.getStringList(path).stream()
                .map(NamespacedKey::fromString)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }
}
