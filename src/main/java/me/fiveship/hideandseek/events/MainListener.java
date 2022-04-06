package me.fiveship.hideandseek.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.cmd.EditorMode;
import me.fiveship.hideandseek.game.Map;
import me.fiveship.hideandseek.game.Team;
import me.fiveship.hideandseek.localization.CStr;
import me.fiveship.hideandseek.localization.Localization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Pattern;

public class MainListener implements Listener {


    @RegExp
    private static final String PATTERN = "[A-Za-z][A-Za-z0-9]*";

    @EventHandler
    public void onPlayerMoved(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        double d = from.distanceSquared(to);
        var player = event.getPlayer();
        if (Map.playerIn(player) != null) {
            if (d >= 0.001) {
                // moved more than a little
                moved(player, from.getY());
            }
        }
    }

    @EventHandler
    public void onBlockHit(PlayerInteractEvent event) {
        Player hitter = event.getPlayer();
        var map = Map.playerIn(hitter);
        if (map != null) {
            var hitterTeam = map.teams.get(hitter);
            if (hitterTeam == Team.SEEKER) {
                Block hittedBlock = event.getClickedBlock();
                var victim = HNS.blocksR.get(hittedBlock);
                if (victim != null) {
                    moved(victim, victim.getLocation().getY());
                }
            }
        }
    }

    private void moved(Player player, double y) {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            var l = player.getLocation();
            l.setY(y);
            player.teleport(l);
        }
        player.setGameMode(GameMode.ADVENTURE);
        HNS.timer.put(player, 0.0);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        EditorMode mode = HNS.editorContext.getOrDefault(player, new EditorMode());
        if (HNS.editorContext.containsKey(player) && player.isOp() && (!mode.inChat || msg.startsWith("#"))) {
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
            } else if (msg.startsWith("create")) {
                if ("create".equalsIgnoreCase(msg)) {
                    player.sendMessage(Localization.helpCreate);
                } else {
                    msg = msg.substring("create ".length());
                    if (HNS.maps.containsKey(msg)) {
                        player.sendMessage(args(Localization.takenName, msg));
                    } else {
                        if (Pattern.matches(PATTERN, msg)) {
                            Map map = new Map(msg);
                            HNS.maps.put(map.id, map);
                            player.sendMessage(args(Localization.mapCreated, msg));
                        } else {
                            player.sendMessage(args(Localization.invalidMapName, msg));
                        }
                    }
                }
            } else if (msg.startsWith("map")) {
                if ("map".equalsIgnoreCase(msg)) {
                    player.sendMessage(Localization.helpMap);
                } else {
                    msg = msg.substring("map ".length());
                    var map = HNS.maps.getOrDefault(msg, null);
                    if (map != null) {
                        HNS.editorContext.get(player).map = map;
                        player.sendMessage(args(Localization.mapSelected, msg));
                    } else {
                        player.sendMessage(args(Localization.mapDoesNotExist, msg));
                    }
                }
            } else if (msg.startsWith("list")) {
                if (mode.map == null) {
                    player.sendMessage(Localization.mapNotSelected);
                } else {
                    if ("list".equalsIgnoreCase(msg)) {
                        for (var s : Localization.helpList) {
                            player.sendMessage(s);
                        }
                    } else {
                        msg = msg.substring("list ".length());
                        switch (msg.toUpperCase()) {
                            case "seekerspawn", "seekerspawns", "seeker-spawns", "seeker-spawn" -> {
                                for (int i = 0; i < mode.map.seekerSpawns.size(); i++) {
                                    var s = mode.map.seekerSpawns.get(i);
                                    player.sendMessage(new CStr(" &b" + i + ": &7" + s.toString()).toString());
                                }
                                player.sendMessage("");
                            }
                            case "hiderspawn", "hiderspawns", "hider-spawns", "hider-spawn" -> {
                                for (int i = 0; i < mode.map.hiderSpawns.size(); i++) {
                                    var s = mode.map.hiderSpawns.get(i);
                                    player.sendMessage(new CStr(" &b" + i + ": &7" + s.toString()).toString());
                                }
                                player.sendMessage("");
                            }
                        }
                    }
                }
            } else if (msg.equalsIgnoreCase("exit")) {
                HNS.editorContext.remove(player);
            } else {
                player.sendMessage(Localization.invalidEditorCmd);
            }
        }
        HNS.editorContext.put(player, mode);
    }

    private String args(String l, Object... args) {
        return Localization.args(l, args);

    }

}
