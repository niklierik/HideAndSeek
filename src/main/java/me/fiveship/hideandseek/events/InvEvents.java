package me.fiveship.hideandseek.events;

import me.fiveship.hideandseek.game.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class InvEvents implements Listener {


    public static HashMap<Player, String> invs = new HashMap<>();

    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Map m = Map.playerIn(player);
        if (m != null) {
            var inv = invs.get(player);
            if (inv != null) {
                int slot = event.getSlot();
                switch (inv.toLowerCase()) {
                    case "blocks":
                        var block = m.blockOf(slot);
                        if (block != null) {
                            m.materials.put(player, block);
                        }
                        break;
                    case "spawns":
                        var spawn = m.spawn(slot, player);
                        if (spawn != null) {
                            m.desiredSpawn.put(player, spawn.location);
                        }
                        break;
                }
                event.setCancelled(true);
            }
        }
    }

    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        invs.remove(player);
    }


}
