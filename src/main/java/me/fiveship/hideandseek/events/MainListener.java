package me.fiveship.hideandseek.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.cmd.EditorMode;
import me.fiveship.hideandseek.game.Map;
import me.fiveship.hideandseek.localization.Localization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class MainListener implements Listener {

    private HashMap<Player, EditorMode> editorContext = new HashMap<>();

    @EventHandler
    public void onPlayerMoved(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        double d = from.distanceSquared(to);
        var player = event.getPlayer();
        if (Map.playerIn(player) != null) {
            if (d >= 0.001) {
                // moved more than a little
                moved(player);
            }
        }
    }

    @EventHandler
    public void onBlockHit(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (Map.playerIn(player) != null) {
            moved(player);
        }
    }

    private void moved(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        HNS.timer.put(player, 0.0);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        EditorMode mode = editorContext.getOrDefault(player, new EditorMode());
        if (HNS.inEditor.contains(player) && player.isOp() && (!mode.inChat || msg.startsWith("#"))) {
            if (msg.startsWith("#")) {
                msg = msg.substring(1);
            }
            event.setCancelled(true);
            if (msg.startsWith("chat ")) {
                msg = msg.substring("chat ".length());
                event.message(Component.text(msg));
                event.setCancelled(false);
            } else if (msg.equalsIgnoreCase("help")) {
                for (var s : Localization.args(Localization.helpEditor)) {
                    player.sendMessage(s);
                }
            } else if (msg.equalsIgnoreCase("chat-on")) {
                mode.inChat = true;
            } else if (msg.equalsIgnoreCase("chat-off")) {
                mode.inChat = false;
            } else if (msg.startsWith("create ")) {
                msg = msg.substring("create ".length());

            } else if (msg.startsWith("map ")) {
                msg = msg.substring("map ".length());

            } else if (msg.equalsIgnoreCase("exit")) {
                HNS.inEditor.remove(player);
            } else {
                player.sendMessage(Localization.invalidEditorCmd);
            }
        }
        editorContext.put(player, mode);
    }

}
