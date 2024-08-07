package de.rayzs.eatable.api.item;

import org.bukkit.inventory.ItemStack;

public class ItemFood {

    private ItemConditions conditions;
    private float saturation = 1.0f, seconds = 5.0f;
    private int nutrition = 1;
    private ItemStack convertsTo;
    private boolean fast, alwaysEatable;

    public ItemFood setNutrition(int nutrition) {
        this.nutrition = nutrition;
        return this;
    }

    public ItemFood setSeconds(float seconds) {
        this.seconds = seconds;
        return this;
    }

    public ItemFood setConditions(ItemConditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public ItemFood setAlwaysEatable(boolean alwaysEatable) {
        this.alwaysEatable = alwaysEatable;
        return this;
    }

    public ItemFood setSaturation(float saturation) {
        this.saturation = saturation;
        return this;
    }

    public ItemFood setConvertsToStack(ItemStack itemstack) {
        this.convertsTo = itemstack;
        return this;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public ItemStack getConvertsToStack() {
        return convertsTo;
    }

    public boolean isFast() {
        return fast;
    }

    public boolean isAlwaysEatable() {
        return alwaysEatable;
    }

    public boolean hasConvertsToStack() {
        return conditions != null;
    }

    public float getSeconds() {
        return seconds;
    }

    public int getNutrition() {
        return nutrition;
    }

    public float getSaturation() {
        return saturation;
    }

    public ItemConditions getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return "ItemFood{" +
                "conditions=" + conditions +
                ", saturation=" + saturation +
                ", nutrition=" + nutrition +
                ", convertsTo=" + convertsTo +
                ", fast=" + fast +
                ", alwaysEatable=" + alwaysEatable +
                '}';
    }
}
