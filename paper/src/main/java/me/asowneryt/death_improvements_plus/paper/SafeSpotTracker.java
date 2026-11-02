package me.asowneryt.death_improvements_plus.paper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the last block each player stood on that is safe to drop items on, persisted in the player's data.
 */
public class SafeSpotTracker implements Listener {

    private static final int DELAY_BEFORE_TRACKING = 100;

    private final DIPPlugin plugin;
    private final NamespacedKey worldKey;
    private final NamespacedKey posKey;
    private final Map<UUID, Location> lastSafePos = new HashMap<>();
    private final Map<UUID, Integer> delayBeforeTracking = new HashMap<>();

    public SafeSpotTracker(DIPPlugin plugin) {
        this.plugin = plugin;
        this.worldKey = new NamespacedKey(plugin, "last_safe_world");
        this.posKey = new NamespacedKey(plugin, "last_safe_pos");
    }

    public void start() {
        Bukkit.getOnlinePlayers().forEach(this::load);
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1, 1);
    }

    /**
     * Block the player is standing on, approximating vanilla's {@code Entity#getOnPos}.
     */
    public static Block blockOn(Player player) {
        return player.getLocation().subtract(0, 0.2, 0).getBlock();
    }

    public static boolean canStandOn(Block block) {
        return block.getBlockData().isFaceSturdy(BlockFace.UP, BlockSupport.FULL);
    }

    private static boolean isDangerous(Block block) {
        return switch (block.getType()) {
            case LAVA, FIRE, SOUL_FIRE -> true;
            default -> false;
        };
    }

    /**
     * @return the last safe block position in the player's current world, or null if none is known
     */
    public @Nullable Location getLastSafePos(Player player) {
        Location pos = lastSafePos.get(player.getUniqueId());
        return pos != null && player.getWorld().equals(pos.getWorld()) ? pos.clone() : null;
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead()) continue;
            UUID uuid = player.getUniqueId();
            int delay = delayBeforeTracking.getOrDefault(uuid, 0);
            if (delay > 0) {
                delayBeforeTracking.put(uuid, delay - 1);
                continue;
            }
            Block block = blockOn(player);
            // Also check where items would land: isInLava() can lag a tick behind teleports into lava
            if (player.isInLava() || !canStandOn(block) || isDangerous(block.getRelative(BlockFace.UP))) continue;
            Location pos = block.getLocation();
            Location previous = lastSafePos.put(uuid, pos);
            if (pos.equals(previous)) continue;
            if (previous == null) plugin.debugLog("Started tracking last safe position for player %s", player.getName());
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            pdc.set(worldKey, PersistentDataType.STRING, pos.getWorld().getUID().toString());
            pdc.set(posKey, PersistentDataType.INTEGER_ARRAY, new int[]{pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()});
        }
    }

    private void load(Player player) {
        UUID uuid = player.getUniqueId();
        delayBeforeTracking.put(uuid, DELAY_BEFORE_TRACKING);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        String worldId = pdc.get(worldKey, PersistentDataType.STRING);
        int[] pos = pdc.get(posKey, PersistentDataType.INTEGER_ARRAY);
        if (worldId == null || pos == null || pos.length != 3) return;
        World world;
        try {
            world = Bukkit.getWorld(UUID.fromString(worldId));
        } catch (IllegalArgumentException e) {
            return;
        }
        if (world == null) return;
        lastSafePos.put(uuid, new Location(world, pos[0], pos[1], pos[2]));
        plugin.debugLog("Loaded safe position %d %d %d for player %s", pos[0], pos[1], pos[2], player.getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        lastSafePos.remove(uuid);
        delayBeforeTracking.remove(uuid);
    }
}
