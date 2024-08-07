package de.rayzs.eatable.api.item;

import de.rayzs.eatable.utils.ArrayUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.*;
import java.util.*;

public class ItemConditions {

    private String name = null, worldName = null, permission = null;

    private List<String> lore = null;
    private Material material = null;

    public ItemConditions requiresName(String name) {
        this.name = name;
        return this;
    }

    public ItemConditions requiresLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemConditions requiresLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemConditions requiresMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemConditions requiresPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public ItemConditions requiresWorld(String worldName) {
        this.worldName = worldName;
        return this;
    }

    public boolean matches(Player player, ItemStack stack) {
        return matchesPlayer(player) && matchesItem(stack);
    }

    public boolean matchesPlayer(Player player) {
        return matchesPermission(player) && matchesWorld(player.getWorld());
    }

    public boolean matchesPermission(Player player) {
        return this.permission == null || player.hasPermission(permission);
    }

    public boolean matchesWorld(World world) {
        return this.worldName == null || world != null && this.worldName.equals(world.getName());
    }

    public boolean matchesItem(ItemStack stack) {
        return matchesDisplayName(stack) && matchesMaterial(stack) && matchesLore(stack);
    }

    public boolean matchesDisplayName(ItemStack stack) {
        return this.name == null
                || stack.hasItemMeta()
                && stack.getItemMeta().hasDisplayName()
                && this.name.equals(stack.getItemMeta().getDisplayName());
    }

    public boolean matchesLore(ItemStack stack) {
        return this.lore == null
                || stack.hasItemMeta()
                && stack.getItemMeta().hasLore()
                && ArrayUtils.compareStringArrays(this.lore, stack.getItemMeta().getLore());
    }

    public boolean matchesMaterial(ItemStack stack) {
        return this.material == null || this.material == stack.getType();
    }

    public String getWorldName() {
        return worldName;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public ItemConditions clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (ItemConditions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "ItemConditions{" +
                "name='" + name + '\'' +
                ", worldName='" + worldName + '\'' +
                ", permission='" + permission + '\'' +
                ", lore=" + lore +
                ", material=" + material +
                '}';
    }
}
