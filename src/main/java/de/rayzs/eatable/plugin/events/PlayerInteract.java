package de.rayzs.eatable.plugin.events;

import org.bukkit.event.player.PlayerInteractEvent;
import de.rayzs.eatable.api.EatableItems;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if(stack == null) return;
        boolean alreadyFood = stack.hasItemMeta() && stack.getItemMeta().hasFood();

        if(!EatableItems.handleItem(player, stack)) return;
        if(!alreadyFood) event.setCancelled(true);
    }
}
