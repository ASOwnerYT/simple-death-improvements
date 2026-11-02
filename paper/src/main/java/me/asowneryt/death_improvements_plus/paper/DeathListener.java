package me.asowneryt.death_improvements_plus.paper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles item keeping, item drops and experience drops when a player dies.
 * Runs late so that plugins adding or removing drops (graves, soulbound items, ...) are respected.
 */
public class DeathListener implements Listener {

    private static final int[] HOTBAR_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] ARMOR_SLOTS = {36, 37, 38, 39};
    private static final int[] OFFHAND_SLOTS = {40};

    private final DIPPlugin plugin;
    private final ItemListener itemListener;

    public DeathListener(DIPPlugin plugin, ItemListener itemListener) {
        this.plugin = plugin;
        this.itemListener = itemListener;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        DIPConfig config = plugin.config();
        Player player = event.getEntity();

        if (!event.getKeepInventory()) keepItems(event, config);

        if (config.playerDropMoreXpOnDeath() && !event.getKeepLevel()) {
            event.setDroppedExp(totalExperience(player) * config.droppedExperiencePercent() / 100);
        }

        Location safePos = plugin.safeSpotTracker().getLastSafePos(player);
        dropItems(event, config, safePos);
        dropExperience(event, config, safePos);
    }

    private void keepItems(PlayerDeathEvent event, DIPConfig config) {
        PlayerInventory inventory = event.getEntity().getInventory();
        if (config.keepHotbarOnDeath()) keepSlots(event, inventory, HOTBAR_SLOTS, config.hotbarDropList());
        if (config.keepArmorOnDeath()) keepSlots(event, inventory, ARMOR_SLOTS, config.armorDropList());
        if (config.keepOffhandOnDeath()) keepSlots(event, inventory, OFFHAND_SLOTS, config.offhandDropList());
    }

    private void keepSlots(PlayerDeathEvent event, PlayerInventory inventory, int[] slots, Set<NamespacedKey> dropList) {
        for (int slot : slots) {
            ItemStack stack = inventory.getItem(slot);
            if (stack == null || stack.getType().isAir() || dropList.contains(stack.getType().getKey())) continue;
            // Only keep what is actually being dropped, so items removed by curses or other plugins stay removed
            for (Iterator<ItemStack> it = event.getDrops().iterator(); it.hasNext(); ) {
                ItemStack drop = it.next();
                if (stack.equals(drop)) {
                    it.remove();
                    event.getItemsToKeep().add(drop);
                    break;
                }
            }
        }
    }

    private void dropItems(PlayerDeathEvent event, DIPConfig config, @Nullable Location safePos) {
        if (event.getDrops().isEmpty()) return;
        Player player = event.getEntity();
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();

        Location savePos = safePos != null ? findItemSavePos(player, config, safePos) : null;
        Location dropPos = savePos != null
                ? savePos.add(0.5, 1, 0.5)
                : player.getLocation().add(0, player.getEyeHeight() - 0.3, 0);

        for (ItemStack stack : drops) {
            if (stack == null || stack.getType().isAir()) continue;
            player.getWorld().dropItem(dropPos, stack, item -> {
                item.setPickupDelay(40);
                if (config.noItemSplatterOnDeath()) {
                    item.setVelocity(new Vector());
                } else {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    double speed = random.nextDouble() * 0.5;
                    double angle = random.nextDouble() * Math.PI * 2;
                    item.setVelocity(new Vector(-Math.sin(angle) * speed, 0.2, Math.cos(angle) * speed));
                }
                if (config.noDeathItemDespawn()) item.setUnlimitedLifetime(true);
                else itemListener.setLifetime(item, config.itemDespawnTimer() * 20);
            });
        }
        if (config.noItemSplatterOnDeath()) plugin.debugLog("Prevented item splatter for player %s", player.getName());
        if (config.noDeathItemDespawn()) plugin.debugLog("Set infinite lifetime to items dropped by player %s", player.getName());
    }

    /**
     * @return the block to drop items on instead of the death location, or null if the death location is safe
     */
    private @Nullable Location findItemSavePos(Player player, DIPConfig config, Location safePos) {
        World world = player.getWorld();
        Block on = SafeSpotTracker.blockOn(player);
        if (on.getType().isAir()) {
            plugin.debugLog("Player %s died while airborne", player.getName());
            boolean isSafeBelow = false;
            for (int y = on.getY() - 1; y > world.getMinHeight(); y--) {
                Block block = world.getBlockAt(on.getX(), y, on.getZ());
                if (config.tryItemLavaSaveOnDeath() && block.getType() == Material.LAVA && !player.isInLava()) {
                    plugin.debugLog("Block below player at %d %d %d is lava", block.getX(), block.getY(), block.getZ());
                    return logSave(player, safePos, ItemSaveReason.LAVA_AIR);
                }
                if (SafeSpotTracker.canStandOn(block)) {
                    plugin.debugLog("Block %s below player at %d %d %d is safe", block.getType().getKey(), block.getX(), block.getY(), block.getZ());
                    isSafeBelow = true;
                    break;
                }
            }
            if (config.tryItemVoidSaveOnDeath() && !isSafeBelow) {
                plugin.debugLog("Didn't find safe block below player");
                return logSave(player, safePos, ItemSaveReason.VOID_AIR);
            }
        }
        if (config.tryItemLavaSaveOnDeath() && player.isInLava()) {
            return logSave(player, safePos, ItemSaveReason.LAVA);
        }
        if (config.tryItemVoidSaveOnDeath() && player.getLocation().getY() < world.getMinHeight() - 64) {
            return logSave(player, safePos, ItemSaveReason.VOID);
        }
        return null;
    }

    private Location logSave(Player player, Location safePos, ItemSaveReason reason) {
        plugin.debugLog(
                "Dropped items for player %s at %d %d %d. Reason: %s",
                player.getName(), safePos.getBlockX(), safePos.getBlockY(), safePos.getBlockZ(), reason
        );
        return safePos;
    }

    private void dropExperience(PlayerDeathEvent event, DIPConfig config, @Nullable Location safePos) {
        int amount = event.getDroppedExp();
        if (!config.noXpSplatterOnDeath() || amount <= 0) return;
        Player player = event.getEntity();
        event.setDroppedExp(0);
        Location pos = safePos != null ? safePos.add(0.5, 1, 0.5) : player.getLocation();
        player.getWorld().spawn(pos, ExperienceOrb.class, orb -> {
            orb.setExperience(amount);
            orb.setVelocity(new Vector());
        });
        plugin.debugLog("Prevented XP splatter for player %s", player.getName());
    }

    /**
     * Total experience points the player has collected towards their current level and progress.
     */
    private static int totalExperience(Player player) {
        int level = player.getLevel();
        int xp = 0;
        for (int i = 0; i < level; i++) xp += xpNeededForNextLevel(i);
        return xp + (int) (player.getExp() * xpNeededForNextLevel(level));
    }

    private static int xpNeededForNextLevel(int level) {
        if (level >= 30) return 112 + (level - 30) * 9;
        if (level >= 15) return 37 + (level - 15) * 5;
        return 7 + level * 2;
    }
}
