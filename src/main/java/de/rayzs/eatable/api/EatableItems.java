package de.rayzs.eatable.api;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.UseRemainder;
import de.rayzs.eatable.utils.configuration.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import de.rayzs.eatable.api.item.*;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import java.util.*;

public class EatableItems {

    private static HashMap<String, ItemFood> ITEMS = new HashMap<>();
    private static final ConfigurationBuilder CONFIG = Configurator.get("items");

    public static void load() {
        ITEMS = new HashMap<>();

        for (String key : CONFIG.getKeys(false)) {
            ITEMS.put(key, getItemFoodFromConfig(key));
        }
    }

    private static void saveItemFood(String name, ItemFood itemFood) {
        CONFIG.set(name + ".saturation", itemFood.getSaturation())
                .set(name + ".nutrition", itemFood.getNutrition())
                .set(name + ".fast", itemFood.isFast())
                .set(name + ".alwaysEatable", itemFood.isAlwaysEatable())
                .set(name + ".time", itemFood.getSeconds())
                .set(name + ".convertsTo", itemFood.getConvertsToStack());

        if(itemFood.getConditions() != null) {
            CONFIG.set(name + ".conditions.worldName", itemFood.getConditions().getWorldName())
                    .set(name + ".conditions.material", itemFood.getConditions().getMaterial() == null ? "AIR" : itemFood.getConditions().getMaterial().name())
                    .set(name + ".conditions.displayName", itemFood.getConditions().getName())
                    .set(name + ".conditions.permission", itemFood.getConditions().getPermission())
                    .set(name + ".conditions.lore", itemFood.getConditions().getLore());
        }

        CONFIG.save();
    }

    public static ItemFood getItemFromName(String name) {
        return ITEMS.get(name);
    }

    public static ItemFood getItemFromStack(ItemStack stack) {
        ItemFood itemFood = new ItemFood();

        FoodProperties food = stack.getData(DataComponentTypes.FOOD);
        if(food != null) {
            itemFood.setNutrition(food.nutrition());
            itemFood.setSaturation(food.saturation());
            itemFood.setAlwaysEatable(food.canAlwaysEat());
        }

        Consumable consumable = stack.getData(DataComponentTypes.CONSUMABLE);
        if(consumable != null)
            itemFood.setSeconds(consumable.consumeSeconds());

        UseRemainder remainder = stack.getData(DataComponentTypes.USE_REMAINDER);
        if(remainder != null)
            itemFood.setConvertsToStack(remainder.transformInto());

        return itemFood;
    }

    public static boolean isEatableOverridden(ItemStack stack) {
        return stack.isDataOverridden(DataComponentTypes.FOOD);
    }

    public static void resetEatable(ItemStack stack) {
        stack.resetData(DataComponentTypes.FOOD);
        stack.resetData(DataComponentTypes.CONSUMABLE);
        stack.resetData(DataComponentTypes.USE_REMAINDER);
    }

    public static ItemConditions getConditionFromStack(ItemStack stack) {
        ItemConditions conditions = new ItemConditions();

        conditions.requiresMaterial(stack.getType());

        if(stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if(meta.hasDisplayName()) conditions.requiresName(meta.getDisplayName());
            if(meta.hasLore()) conditions.requiresLore(meta.getLore());
        }

        return conditions;
    }

    public static void transformItemEatable(ItemStack stack, ItemFood itemFood) {
        stack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(Math.max(0, itemFood.getNutrition()))
                .saturation(itemFood.getSaturation())
                .canAlwaysEat(itemFood.isAlwaysEatable()));

        float seconds = itemFood.isFast() ? itemFood.getSeconds() / 2f : itemFood.getSeconds();
        stack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(Math.max(0f, seconds)));

        ItemStack remainder = itemFood.getConvertsToStack();
        if(remainder != null && remainder.getType() != Material.AIR)
            stack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(remainder));
        else stack.resetData(DataComponentTypes.USE_REMAINDER);
    }

    public static boolean create(String name, ItemFood itemFood) {
        ITEMS.put(name, itemFood);
        saveItemFood(name, itemFood);
        return true;
    }

    public static boolean delete(String name) {
        if(ITEMS.containsKey(name)) {
            ITEMS.remove(name);
            CONFIG.setAndSave(name, null);
            return true;
        }

        return false;
    }

    public static ItemFood createEmptyItemFood() {
        return new ItemFood().setConditions(new ItemConditions());
    }

    private static ItemFood getItemFoodFromConfig(String name) {
        ItemFood itemFood = new ItemFood();
        ItemConditions conditions = new ItemConditions();
        itemFood.setSaturation((((float) (double) CONFIG.get(name + ".saturation"))));
        itemFood.setSeconds((((float) (double) CONFIG.get(name + ".time"))));
        itemFood.setNutrition((int) CONFIG.get(name + ".nutrition"));
        itemFood.setFast((boolean) CONFIG.get(name + ".fast"));
        itemFood.setAlwaysEatable((boolean) CONFIG.get(name + ".alwaysEatable"));

        if(CONFIG.get(name + ".convertsTo") != null)
            itemFood.setConvertsToStack((ItemStack) CONFIG.get(name + ".convertsTo"));

        conditions.requiresWorld((String) CONFIG.get(name + ".conditions.worldName"));
        conditions.requiresLore((List<String>) CONFIG.get(name + ".conditions.lore"));
        conditions.requiresMaterial(Material.valueOf((String) CONFIG.get(name + ".conditions.material")));
        conditions.requiresName((String) CONFIG.get(name + ".conditions.displayName"));
        conditions.requiresPermission((String) CONFIG.get(name + ".conditions.permission"));

        itemFood.setConditions(conditions);
        return itemFood;
    }

    public static boolean handleItem(Player player, ItemStack stack) {
        for (Map.Entry<String, ItemFood> entry : ITEMS.entrySet()) {
            if(entry.getValue().getConditions() != null && !entry.getValue().getConditions().matches(player, stack)) {
                continue;
            }

            transformItemEatable(stack, entry.getValue());
            return true;
        }

        return false;
    }

    public static ConfigurationBuilder getConfig() {
        return CONFIG;
    }

    public static String[] getItemGroupsNames() {
        return ITEMS.keySet().toArray(new String[] { });
    }
}
