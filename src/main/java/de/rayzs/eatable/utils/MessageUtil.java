package de.rayzs.eatable.utils;

import de.rayzs.eatable.utils.configuration.*;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import java.util.*;

public class MessageUtil {

    private static final HashMap<String, Message> MESSAGES = new HashMap<>();
    private static final Message ERROR_MESSAGE = new Message("&c");
    private static final ConfigurationBuilder FILE = Configurator.get("messages");
    
    public static void clear() {
        MESSAGES.clear();
    }
    
    public static void send(CommandSender sender, String key, String... replacements) {
        Message message = null;
        if(MESSAGES.containsKey(key)) {
            try {

                Object obj = FILE.get(key);

                if(obj instanceof String line)
                    message = new Message(line);
                else if(obj instanceof List<?> lines)
                    message = new Message((List<String>) lines);

                if(message != null) MESSAGES.put(key, message);

            } catch (Exception exception) {
                Logger.warning("Failed to load message from message.yml: " + key);
            }
        }

        (message == null ? ERROR_MESSAGE : message).sendMessage(sender, replacements);
    }
    
    private static class Message {
        
        private final List<String> LINES = new ArrayList<>();

        public Message(List<String> lines) {
            LINES.addAll(lines);
        }

        public Message(String... lines) {
            LINES.addAll(List.of(lines));
        }

        public Message add(List<String> lines) {
            LINES.addAll(lines);
            return this;
        }

        public Message add(String... lines) {
            LINES.addAll(List.of(lines));
            return this;
        }

        public void sendMessage(CommandSender sender, String... replacements) {
            for (String line : StringUtils.replaceList(LINES, replacements))
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
