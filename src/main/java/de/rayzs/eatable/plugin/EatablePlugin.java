package de.rayzs.eatable.plugin;

import de.rayzs.eatable.api.EatableItems;
import de.rayzs.eatable.plugin.command.EatableCommand;
import de.rayzs.eatable.plugin.events.PlayerInteract;
import de.rayzs.eatable.utils.configuration.Configurator;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class EatablePlugin extends JavaPlugin {

    private static Plugin PLUGIN;
    private static Logger LOGGER;

    @Override
    public void onEnable() {
        PLUGIN = this;
        LOGGER = getLogger();

        Configurator.createResourcedFile(getDataFolder(), "messages.yml", "messages.yml", false);

        EatableItems.load();

        EatableCommand eatableCommand = new EatableCommand();
        PluginCommand command = getCommand("eatable");
        command.setExecutor(eatableCommand);
        command.setTabCompleter(eatableCommand);

        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
    }

    @Override
    public void onDisable() {

    }

    public static Plugin getPlugin() {
        return PLUGIN;
    }

    public static Logger getPluginLogger() {
        return LOGGER;
    }
}
