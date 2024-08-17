package de.rayzs.eatable.plugin.command;

import de.rayzs.eatable.api.EatableItems;
import de.rayzs.eatable.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import de.rayzs.eatable.api.item.*;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EatableCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int length = args.length;

        if(!checkPermsAndResponse(sender, "use")) return true;

        Player player = null;
        if(sender instanceof Player playerSender) player = playerSender;

        try {

            if (length > 0) {
                String task = args[0].toLowerCase();

                if (length == 1) {

                    if (task.equals("reload")) {
                        if(!checkPermsAndResponse(sender, "reload")) return true;

                        MessageUtil.clear();
                        EatableItems.getConfig().reload();
                        EatableItems.load();

                        MessageUtil.send(sender, "Reload");
                        return true;
                    } else if (task.equals("info")) {

                        if(!checkPermsAndResponse(sender, "info")) return true;

                        if (player == null) {
                            MessageUtil.send(sender, "OnlyPlayers");
                            return true;
                        }

                        ItemStack stack = player.getItemInHand();
                        if (stack.getType() == Material.AIR) {
                            MessageUtil.send(sender, "NoItem");
                            return true;
                        }

                        if(!stack.hasItemMeta() || stack.getItemMeta().hasFood()) {
                            MessageUtil.send(sender, "NoInformation");
                            return true;
                        }

                        ItemFood itemFood = EatableItems.getItemFromStack(stack);

                        MessageUtil.send(sender, "Info.Hand.Message",
                                "%nutrition%", String.valueOf(itemFood.getNutrition()),
                                "%saturation%", String.valueOf(itemFood.getSaturation()),
                                "%seconds%", String.valueOf(itemFood.getSeconds()));

                        return true;

                    } else if (task.equals("list")) {

                        if(!checkPermsAndResponse(sender, "list")) return true;

                        MessageUtil.Message message = MessageUtil.getMessage(sender, "Group.List");
                        List<String> groupListLines = message.getLines();
                        String targetLine = null;

                        for (String groupListLine : groupListLines) {
                            if(targetLine == null && groupListLine.contains("%group%")) {
                                targetLine = groupListLine;
                                for (String groupName : EatableItems.getItemGroupsNames())
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', targetLine.replace("%group%", groupName)));

                                continue;
                            }

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', groupListLine));
                        }

                        return true;

                    } else if (task.equals("uneatable")) {

                        if(!checkPermsAndResponse(sender, "uneatable")) return true;

                        if (player == null) {
                            MessageUtil.send(sender, "OnlyPlayers");
                            return true;
                        }

                        ItemStack stack = player.getItemInHand();

                        if (stack.getType() == Material.AIR) {
                            MessageUtil.send(sender, "NoItem");
                            return true;
                        }

                        if(!stack.hasItemMeta() || !stack.getItemMeta().hasFood()) {
                            MessageUtil.send(sender, "Uneatable.NotConsumable");
                            return true;
                        }

                        ItemMeta meta = stack.getItemMeta();
                        meta.setFood(null);
                        stack.setItemMeta(meta);

                        MessageUtil.send(sender, "Uneatable.Success");
                        return true;
                    }
                }

                if (length == 2) {
                    String sub = args[1];

                    if (task.equals("create")) {

                        if(!checkPermsAndResponse(sender, "create")) return true;

                        if (sub.equalsIgnoreCase("hand")) {
                            MessageUtil.send(sender, "Group.Create.NotPossible");
                            return true;
                        }

                        if (EatableItems.getItemFromName(sub) != null) {
                            MessageUtil.send(sender, "Group.Create.AlreadyGiven");
                            return true;
                        }

                        EatableItems.create(sub, EatableItems.createEmptyItemFood());
                        MessageUtil.send(sender, "Group.Create.Success", "%group%", sub);
                        return true;

                    } else  if (task.equals("delete")) {

                        if(!checkPermsAndResponse(sender, "delete")) return true;

                        if (sub.equalsIgnoreCase("hand")) {
                            MessageUtil.send(sender, "Group.Delete.NotPossible");
                            return true;
                        }

                        if (EatableItems.getItemFromName(sub) == null) {
                            MessageUtil.send(sender, "GroupNotExist");
                            return true;
                        }

                        EatableItems.delete(sub);
                        MessageUtil.send(sender, "Group.Delete.Success", "%group%", sub);
                        return true;

                    } else if (task.equals("info")) {

                        if(!checkPermsAndResponse(sender, "info")) return true;

                        ItemFood itemFood = EatableItems.getItemFromName(sub);

                        if (itemFood == null) {
                            MessageUtil.send(sender, "GroupNotExist");
                            return true;
                        }

                        ItemConditions conditions = itemFood.getConditions();

                        MessageUtil.send(sender, "Info.Group.Message",
                                "%group%", sub,
                                "%nutrition%", String.valueOf(itemFood.getNutrition()),
                                "%saturation%", String.valueOf(itemFood.getSaturation()),
                                "%seconds%", String.valueOf(itemFood.getSeconds()));
                        if (conditions == null) {
                            MessageUtil.send(sender, "Info.Group.NoConditions", "%group%", sub);
                            return true;
                        }

                        MessageUtil.send(sender, "Info.Group.Message",
                                "%group%", sub,
                                "%permission%", conditions.getPermission(),
                                "%world%", conditions.getWorldName(),
                                "%material%", conditions.getMaterial().name(),
                                "%displayname%", conditions.getName());

                        if(conditions.getLore() == null) {
                            MessageUtil.send(sender, "Info.Group.Lore.NoLore", "%group%", sub);
                            return true;
                        }

                        MessageUtil.Message message = MessageUtil.getMessage(sender, "Info.Conditions.Lore");
                        List<String> loreLines = message.getLines();
                        String targetLine = null;

                        for (String loreLine : loreLines) {
                            if(targetLine == null && loreLine.contains("%lores%")) {
                                targetLine = loreLine;
                                for (String conditionLore : conditions.getLore())
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', targetLine.replace("%lore%", conditionLore)));

                                continue;
                            }

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', loreLine));
                        }

                        return true;
                    }
                }

                if (length >= 3) {
                    String target = args[1],
                            option = args[2].toLowerCase(),
                            value = length == 4 ? args[3].toLowerCase() : null;

                    if (task.equals("property")) {

                        if(!checkPermsAndResponse(sender, "property")) return true;

                        ItemStack stack = null;
                        ItemFood itemFood;
                        if (target.equalsIgnoreCase("hand")) {

                            if (player == null) {
                                MessageUtil.send(sender, "OnlyPlayers");
                                return true;
                            }

                            stack = player.getItemInHand();

                            if (stack.getType() == Material.AIR) {
                                MessageUtil.send(sender, "NoItem");
                                return true;
                            }

                            itemFood = EatableItems.getItemFromStack(stack);

                        } else {
                            itemFood = EatableItems.getItemFromName(target);

                            if (itemFood == null) {
                                MessageUtil.send(sender, "GroupNotExist");
                                return true;
                            }
                        }

                        if (value == null) {
                            MessageUtil.send(sender, "Property.ValueMissing");
                            return true;
                        }

                        boolean empty = false;
                        switch (option) {
                            case "nutrition" -> itemFood.setNutrition(Integer.parseInt(value));
                            case "saturation" -> itemFood.setSaturation(Float.parseFloat(value));
                            case "fast" -> itemFood.setFast(Boolean.parseBoolean(value));
                            case "seconds" -> itemFood.setSeconds(Float.parseFloat(value));
                            case "alwayseatable" -> itemFood.setAlwaysEatable(Boolean.parseBoolean(value));
                            default -> empty = true;
                        }

                        if (!empty) {
                            if(!target.equalsIgnoreCase("hand"))
                                EatableItems.create(target, itemFood);
                            else EatableItems.transformItemEatable(stack, itemFood);
                        }

                        if(empty) {
                            MessageUtil.send(sender, "Property.InvalidProperty");
                            return true;
                        }

                        MessageUtil.send(sender, target.equalsIgnoreCase("hand") ? "Property.Success" : "Property.SuccessGroup", "%group%", target, "%option%", option, "%value%", value);
                        return true;

                    } else if (task.equals("condition")) {

                        if(!checkPermsAndResponse(sender, "condition")) return true;

                        ItemFood itemFood = EatableItems.getItemFromName(target);
                        ItemConditions conditions = itemFood.getConditions();
                        ItemStack stack = player != null ? player.getItemInHand() : null;
                        boolean empty = false;

                        if (value == null) {
                            if (!option.equals("hand")) {
                                MessageUtil.send(sender, "Condition.ValueMissing");
                                return true;
                            }

                            if (player == null) {
                                MessageUtil.send(sender, "OnlyPlayers");
                                return true;
                            }

                            if (stack.getType() != Material.AIR) {
                                MessageUtil.send(sender, "NoItem");
                                return true;
                            }

                            conditions = EatableItems.getConditionFromStack(stack);

                        } else {

                            switch (option) {
                                case "world" -> conditions.requiresWorld(value);
                                case "permission" -> conditions.requiresPermission(value);

                                case "material" -> {
                                    if (value.equalsIgnoreCase("hand")) {

                                        if (player == null) {
                                            MessageUtil.send(sender, "OnlyPlayers");
                                            return true;
                                        }

                                        if (stack.getType() == Material.AIR) {
                                            MessageUtil.send(sender, "NoItem");
                                            return true;
                                        }

                                        conditions.requiresMaterial(stack.getType());
                                    } else try {
                                        value = value.toUpperCase();
                                        conditions.requiresMaterial(Material.valueOf(value));
                                    } catch (Throwable ignored) {
                                        MessageUtil.send(sender, "Condition.Hand.InvalidMaterial");
                                        return true;
                                    }
                                }

                                case "displayname" -> {
                                    if (value.equalsIgnoreCase("hand")) {

                                        if (player == null) {
                                            MessageUtil.send(sender, "OnlyPlayers");
                                            return true;
                                        }

                                        if (stack.getType() != Material.AIR) {
                                            MessageUtil.send(sender, "NoItem");
                                            return true;
                                        }

                                        if (!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) {
                                            MessageUtil.send(sender, "Condition.Hand.NoDisplayname");
                                            return true;
                                        }

                                        conditions.requiresName(stack.getItemMeta().getDisplayName());
                                    } else conditions.requiresName(value);
                                }

                                case "lore" -> {
                                    if (value.equalsIgnoreCase("hand")) {
                                        if (player == null) {
                                            MessageUtil.send(sender, "OnlyPlayers");
                                            return true;
                                        }

                                        if (stack.getType() == Material.AIR) {
                                            MessageUtil.send(sender, "NoItem");
                                            return true;
                                        }

                                        if (!stack.hasItemMeta() || !stack.getItemMeta().hasLore()) {
                                            MessageUtil.send(sender, "Condition.Hand.NoLores");
                                            return true;
                                        }

                                        conditions.requiresLore(stack.getItemMeta().getLore());
                                    } else conditions.requiresLore(value.split("%%"));
                                }

                                default -> empty = true;
                            }
                        }

                        if (!target.equalsIgnoreCase("hand") && !empty)
                            EatableItems.create(target, itemFood);

                        if(empty) {
                            MessageUtil.send(sender, "Condition.InvalidCondition");
                            return true;
                        }

                        MessageUtil.send(sender, "Condition.Success", "%group%", target, "%option%", option, "%value%", value);
                        return true;
                    }
                }
            }

        } catch (Throwable ignored) {
            MessageUtil.send(sender, "SyntaxMessage");
            return true;
        }

        sender.sendMessage("§7All available command: §f/" + label + "§7...");
        sender.sendMessage("§7 list");
        sender.sendMessage("§7 uneatable");
        sender.sendMessage("§7 info <name>");
        sender.sendMessage("§7 create <name>");
        sender.sendMessage("§7 property <hand/name> <property> <hand/value>");
        sender.sendMessage("§7 condition <name> <condition> <hand/value>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("eatable.use")) return List.of();

        final List<String> suggestions = new ArrayList<>(), result = new ArrayList<>();
        final int length = args.length;

        switch (length) {

            case 1:
                suggestions.addAll(Arrays.asList("uneatable", "list", "reload", "info", "create", "delete", "property", "condition"));
                break;

            case 2:
                if(args[0].equalsIgnoreCase("property")) {
                    suggestions.addAll(Arrays.asList(EatableItems.getItemGroupsNames()));
                    suggestions.add("hand");
                } else if(args[0].equalsIgnoreCase("condition") || args[0].equals("delete") || args[0].equals("info"))
                    suggestions.addAll(Arrays.asList(EatableItems.getItemGroupsNames()));
                break;

            case 3:
                if(args[0].equalsIgnoreCase("property"))
                    suggestions.addAll(Arrays.asList("saturation", "nutrition", "fast", "seconds", "alwayseatable"));

                if(args[0].equalsIgnoreCase("condition"))
                    suggestions.addAll(Arrays.asList("world", "permission", "material", "lore", "displayname", "item"));
                break;

            case 4:
                if(args[0].equalsIgnoreCase("property") && (args[2].equalsIgnoreCase("alwayseatable") || args[2].equalsIgnoreCase("fast")))
                    suggestions.addAll(Arrays.asList("true", "false"));

                if(args[0].equalsIgnoreCase("condition") && args[2].equalsIgnoreCase("world"))
                    Bukkit.getWorlds().forEach(world -> suggestions.add(world.getName()));

                if(args[0].equalsIgnoreCase("condition") && args[2].equalsIgnoreCase("material"))
                    Arrays.asList(Material.values()).forEach(material -> suggestions.add(material.name().toLowerCase()));
        }

        suggestions.stream().filter(suggestion -> suggestion.startsWith(args[args.length-1].toLowerCase())).forEach(result::add);
        return result;
    }

    private boolean checkPermsAndResponse(CommandSender sender, String permission) {
        boolean permitted = sender.hasPermission("eatable." + permission);
        if(!permitted) MessageUtil.send(sender, "NoPerms", "%permission%", permission);
        return permitted;
    }
}
