package me.asowneryt.death_improvements_plus.paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DIPPlugin extends JavaPlugin {

    private DIPConfig config;
    private SafeSpotTracker safeSpotTracker;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = DIPConfig.load(getConfig());

        safeSpotTracker = new SafeSpotTracker(this);
        getServer().getPluginManager().registerEvents(safeSpotTracker, this);
        ItemListener itemListener = new ItemListener(this);
        getServer().getPluginManager().registerEvents(itemListener, this);
        getServer().getPluginManager().registerEvents(new DeathListener(this, itemListener), this);
        safeSpotTracker.start();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            config = DIPConfig.load(getConfig());
            sender.sendMessage("Death Improvements Plus config reloaded.");
            return true;
        }
        return false;
    }

    public DIPConfig config() {
        return config;
    }

    public SafeSpotTracker safeSpotTracker() {
        return safeSpotTracker;
    }

    public void debugLog(String message, Object... args) {
        if (config.debug()) getLogger().info(String.format(message, args));
    }
}
