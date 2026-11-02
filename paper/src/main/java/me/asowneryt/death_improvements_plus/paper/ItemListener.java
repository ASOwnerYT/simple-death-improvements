package me.asowneryt.death_improvements_plus.paper;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Explosion resistance for dropped items and the custom despawn timer for items dropped on death.
 */
public class ItemListener implements Listener {

    private static final int TIMER_PERIOD = 20;

    private final DIPPlugin plugin;
    private final NamespacedKey lifetimeKey;
    private final Set<UUID> timedItems = new HashSet<>();

    public ItemListener(DIPPlugin plugin) {
        this.plugin = plugin;
        this.lifetimeKey = new NamespacedKey(plugin, "remaining_lifetime");
        for (World world : Bukkit.getWorlds()) {
            world.getEntitiesByClass(Item.class).forEach(this::startTimer);
        }
    }

    /**
     * Makes the item despawn after the given number of ticks spent loaded, instead of vanilla's 5 minutes.
     * The remaining time is stored on the item, so it survives chunk unloads and restarts like vanilla item age.
     */
    public void setLifetime(Item item, int ticks) {
        item.setUnlimitedLifetime(true);
        item.getPersistentDataContainer().set(lifetimeKey, PersistentDataType.INTEGER, ticks);
        startTimer(item);
    }

    private void startTimer(Item item) {
        if (!item.getPersistentDataContainer().has(lifetimeKey, PersistentDataType.INTEGER)) return;
        if (!timedItems.add(item.getUniqueId())) return;
        item.getScheduler().runAtFixedRate(plugin, task -> {
            PersistentDataContainer pdc = item.getPersistentDataContainer();
            Integer remaining = pdc.get(lifetimeKey, PersistentDataType.INTEGER);
            if (remaining == null) {
                task.cancel();
                timedItems.remove(item.getUniqueId());
            } else if (remaining <= TIMER_PERIOD) {
                task.cancel();
                timedItems.remove(item.getUniqueId());
                item.remove();
            } else {
                pdc.set(lifetimeKey, PersistentDataType.INTEGER, remaining - TIMER_PERIOD);
            }
        }, () -> timedItems.remove(item.getUniqueId()), TIMER_PERIOD, TIMER_PERIOD);
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Item item) startTimer(item);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDamage(EntityDamageEvent event) {
        if (!plugin.config().explosionResistantItems() || !(event.getEntity() instanceof Item)) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            event.setCancelled(true);
        }
    }
}
