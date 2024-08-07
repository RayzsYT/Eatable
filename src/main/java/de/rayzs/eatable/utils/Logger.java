package de.rayzs.eatable.utils;

import de.rayzs.eatable.plugin.EatablePlugin;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import java.util.*;

public class Logger {

    private final static java.util.logging.Logger LOGGER = EatablePlugin.getPluginLogger();

    public static void info(List<String> texts) { texts.forEach(text -> send(Priority.INFO, text)); }
    public static void warning(List<String> texts) { texts.forEach(text -> send(Priority.WARNING, text)); }
    public static void info(String text) { send(Priority.INFO, text); }
    public static void warning(String text) { send(Priority.WARNING, text); }
    public static void debug(String text) { send(Priority.DEBUG, text); }

    protected static void send(Priority priority, String text) {
        if(priority == Priority.DEBUG) return;

        if(LOGGER == null) {
            System.out.println(text);
            return;
        }

        if(text.contains("§")) {
            Bukkit.getConsoleSender().sendMessage(text);
            return;
        }

        Level level = Level.parse(priority.name());
        LOGGER.log(level, text);
    }

    protected enum Priority { INFO, WARNING, DEBUG }
}