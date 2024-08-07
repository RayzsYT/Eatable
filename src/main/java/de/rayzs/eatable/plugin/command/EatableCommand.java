package de.rayzs.eatable.plugin.command;

import de.rayzs.eatable.api.EatableItems;
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

        if(!sender.hasPermission("eatable.use")) {
            sender.sendMessage("§cYou are not permitted to execute this command!");
            return true;
        }

        Player player = null;
        if(sender instanceof Player playerSender) player = playerSender;

        try {

            if (length > 0) {
                String task = args[0].toLowerCase();

                if (length == 1) {
                    if (task.equals("info")) {
                        if (player == null) {
                            sender.sendMessage("§cThis can only be executed as an online player!");
                            return true;
                        }

                        ItemStack stack = player.getItemInHand();
                        if (stack.getType() == Material.AIR) {
                            player.sendMessage("§cYou need to hold an item!");
                            return true;
                        }

                        if(!stack.hasItemMeta() || stack.getItemMeta().hasFood()) {
                            sender.sendMessage("§cThe item you're holding does not have any information!");
                            return true;
                        }

                        ItemFood itemFood = EatableItems.getItemFromStack(stack);

                        sender.sendMessage("§7Information of item in hand:");
                        sender.sendMessage(" §7Nutrition: §e" + itemFood.getNutrition());
                        sender.sendMessage(" §7Saturation: §e" + itemFood.getSaturation());
                        sender.sendMessage(" §7Seconds: §e" + itemFood.getSeconds());
                        sender.sendMessage(" §7Conditions: §cNot available for in-hand items");
                        return true;

                    } else if (task.equals("list")) {

                        sender.sendMessage("§7List of all groups:");
                        for (String itemGroupsName : EatableItems.getItemGroupsNames())
                            sender.sendMessage("  §8- §e" + itemGroupsName);

                        return true;

                    } else if (task.equals("uneatable")) {

                        if (player == null) {
                            sender.sendMessage("§cThis can only be executed as an online player!");
                            return true;
                        }

                        ItemStack stack = player.getItemInHand();

                        if (stack.getType() == Material.AIR) {
                            player.sendMessage("§cYou need to hold an item!");
                            return true;
                        }

                        if(!stack.hasItemMeta() || !stack.getItemMeta().hasFood()) {
                            sender.sendMessage("This item cannot be consumed!");
                            return true;
                        }

                        ItemMeta meta = stack.getItemMeta();
                        meta.setFood(null);
                        stack.setItemMeta(meta);

                        sender.sendMessage("&aThe item in your hand cannot be consumed any longer!");
                        return true;
                    }
                }

                if (length == 2) {
                    String sub = args[1];

                    if (task.equals("create")) {

                        if (sub.equalsIgnoreCase("hand")) {
                            sender.sendMessage("§cGroup cannot be named that way!");
                            return true;
                        }

                        if (EatableItems.getItemFromName(sub) != null) {
                            sender.sendMessage("§cThis name is already given!");
                            return true;
                        }

                        EatableItems.create(sub, EatableItems.createEmptyItemFood());
                        sender.sendMessage("§aCreated group §2" + sub + "§a.");
                        return true;

                    } else  if (task.equals("delete")) {

                        if (sub.equalsIgnoreCase("hand")) {
                            sender.sendMessage("§cGroup cannot be named that way!");
                            return true;
                        }

                        if (EatableItems.getItemFromName(sub) == null) {
                            sender.sendMessage("§cThis group does not exist!");
                            return true;
                        }

                        EatableItems.delete(sub);
                        sender.sendMessage("§cDeleted group §4" + sub + "§c.");
                        return true;

                    } else if (task.equals("info")) {
                        ItemFood itemFood = EatableItems.getItemFromName(sub);

                        if (itemFood == null) {
                            sender.sendMessage("§cThis group does not exist!");
                            return true;
                        }

                        ItemConditions conditions = itemFood.getConditions();

                        sender.sendMessage("§7Information of group: §f" + sub);
                        sender.sendMessage(" §7Nutrition: §e" + itemFood.getNutrition());
                        sender.sendMessage(" §7Saturation: §e" + itemFood.getSaturation());
                        sender.sendMessage(" §7Seconds: §e" + itemFood.getSeconds());

                        if (conditions == null) {
                            sender.sendMessage("§7Conditions: §cNone!");
                            return true;
                        }

                        sender.sendMessage(" §7Conditions:");
                        sender.sendMessage("  §7Permission: §e" + conditions.getPermission());
                        sender.sendMessage("  §7World: §e" + conditions.getWorldName());
                        sender.sendMessage("  §7Material: §e" + conditions.getMaterial());
                        sender.sendMessage("  §7Displayname: §e" + conditions.getName());
                        if(conditions.getLore() == null) {
                            sender.sendMessage("  §7Lore: §cNo lore!");
                            return true;
                        }
                        sender.sendMessage("  §7Lore: §e");
                        sender.sendMessage("    §8- §a" + String.join("    §8- §a", conditions.getLore()));
                        return true;
                    }
                }

                if (length >= 3) {
                    String target = args[1],
                            option = args[2].toLowerCase(),
                            value = length == 4 ? args[3].toLowerCase() : null;

                    if (task.equals("property")) {
                        ItemStack stack = null;
                        ItemFood itemFood;
                        if (target.equalsIgnoreCase("hand")) {

                            if (player == null) {
                                sender.sendMessage("§cThis can only be executed as an online player!");
                                return true;
                            }

                            stack = player.getItemInHand();

                            if (stack.getType() == Material.AIR) {
                                player.sendMessage("§cYou need to hold an item!");
                                return true;
                            }

                            itemFood = EatableItems.getItemFromStack(stack);

                        } else {
                            itemFood = EatableItems.getItemFromName(target);

                            if (itemFood == null) {
                                sender.sendMessage("§cThis group does not exist!");
                                return true;
                            }
                        }

                        if (value == null) {
                            sender.sendMessage("§cValue is missing!");
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
                        sender.sendMessage(empty ? "§cInvalid property!" : "§aSet property §2" + option + " §ato §2" + value + "§a.");
                        return true;

                    } else if (task .equals("condition")) {

                        ItemFood itemFood = EatableItems.getItemFromName(target);
                        ItemConditions conditions = itemFood.getConditions();
                        ItemStack stack = player != null ? player.getItemInHand() : null;
                        boolean empty = false;

                        if (value == null) {
                            if (!option.equals("hand")) {
                                sender.sendMessage("§cValue is missing!");
                                return true;
                            }

                            if (player == null) {
                                sender.sendMessage("§cThis can only be executed as an online player!");
                                return true;
                            }

                            if (stack.getType() != Material.AIR) {
                                sender.sendMessage("§cYou need to hold an item!");
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
                                            sender.sendMessage("§cThis can only be executed as an online player!");
                                            return true;
                                        }

                                        if (stack.getType() == Material.AIR) {
                                            sender.sendMessage("§cYou need to hold an item!");
                                            return true;
                                        }

                                        conditions.requiresMaterial(stack.getType());
                                    } else try {
                                        value = value.toUpperCase();
                                        conditions.requiresMaterial(Material.valueOf(value));
                                    } catch (Throwable ignored) {
                                        sender.sendMessage("§cInvalid material type!");
                                        return true;
                                    }
                                }

                                case "displayname" -> {
                                    if (value.equalsIgnoreCase("hand")) {

                                        if (player == null) {
                                            sender.sendMessage("§cThis can only be executed as an online player!");
                                            return true;
                                        }

                                        if (stack.getType() != Material.AIR) {
                                            sender.sendMessage("§cYou need to hold an item!");
                                            return true;
                                        }

                                        if (!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) {
                                            sender.sendMessage("§cThis item does not have a displayname!");
                                            return true;
                                        }

                                        conditions.requiresName(stack.getItemMeta().getDisplayName());
                                    } else conditions.requiresName(value);
                                }

                                case "lore" -> {
                                    if (value.equalsIgnoreCase("hand")) {
                                        if (player == null) {
                                            sender.sendMessage("§cThis can only be executed as an online player!");
                                            return true;
                                        }

                                        if (stack.getType() == Material.AIR) {
                                            sender.sendMessage("§cYou need to hold an item!");
                                            return true;
                                        }

                                        if (!stack.hasItemMeta() || !stack.getItemMeta().hasLore()) {
                                            sender.sendMessage("§cDoes item has no lore!");
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

                        sender.sendMessage(empty ? "§cInvalid condition!" : "§aSet condition §2" + option + " §ato §2" + value + "§a.");
                        return true;
                    }
                }
            }

        } catch (Throwable ignored) {
            sender.sendMessage("§cInvalid syntax!");
            ignored.printStackTrace();
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
}
