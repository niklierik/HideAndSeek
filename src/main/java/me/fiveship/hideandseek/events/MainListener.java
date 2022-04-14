package me.fiveship.hideandseek.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.cmd.EditorMode;
import me.fiveship.hideandseek.game.LocationData;
import me.fiveship.hideandseek.game.Map;
import me.fiveship.hideandseek.game.Phase;
import me.fiveship.hideandseek.game.Team;
import me.fiveship.hideandseek.localization.CStr;
import me.fiveship.hideandseek.localization.Localization;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
    public void onJoin(PlayerJoinEvent event) {
        if (HNS.cfg.teleportToLobby && HNS.cfg.lobby != null) {
            event.getPlayer().teleport(HNS.cfg.lobby);
        }
        try {
            HNS.inGameTeam.removeEntity(event.getPlayer());
        } catch (Exception ignored) {
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
            event.setCancelled(true);
        }
    }

    public static void moved(Player player, double y) {
        var map = Map.playerIn(player);
        if (map != null) {
            var team = map.teams.get(player);
            if (team == Team.HIDER) {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                    var l = player.getLocation();
                    l.setY(y);
                    player.teleport(l);
                    player.setGameMode(GameMode.ADVENTURE);
                    var block = HNS.blocks.getOrDefault(player, null);
                    if (block != null) {
                        HNS.blocksR.remove(block);
                        block.setType(Material.AIR, true);
                        HNS.blocks.remove(player);
                    }
                }
                HNS.timer.put(player, 0.0);
            }
        }
    }

    private static final List<EntityRegainHealthEvent.RegainReason> REGEN_REASONS = List.of(EntityRegainHealthEvent.RegainReason.REGEN, EntityRegainHealthEvent.RegainReason.SATIATED);

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            Map map = Map.playerIn(player);
            if (map != null && REGEN_REASONS.contains(event.getRegainReason())) {
                event.setCancelled(true);
            }
        }
    }

    private static final List<EntityDamageEvent.DamageCause> CAUSES_TO_IGNORE = List.of(EntityDamageEvent.DamageCause.FALL, EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.SUFFOCATION, EntityDamageEvent.DamageCause.STARVATION);

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        Entity victim = event.getEntity();
        if (victim instanceof Player player) {
            Map map = Map.playerIn(player);
            if (map != null) {
                if (map.phase == Phase.HIDING || map.phase == Phase.SEEKING) {
                    var team = map.teams.get(player);
                    if (team == Team.HIDER && !CAUSES_TO_IGNORE.contains(event.getCause())) {
                        var rng = new Random();
                        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                            if (event instanceof EntityDamageByEntityEvent event2) {
                                if (event2.getDamager() instanceof Player damager) {
                                    if (map.teams.getOrDefault(damager, null).equals(Team.SEEKER)) {
                                        event.setDamage(20 * rng.nextDouble());
                                    } else {
                                        event.setDamage(0);
                                    }
                                }
                            }
                        }
                        /*
                        map.announce(new CStr("&6" + player.getName() + " &c(BÚJÓ) &7kiesett.").toString());
                        if (map.override.deadCanSeek) {
                            map.teams.put(player, Team.SEEKER);
                            map.desiredSpawn.put(player, map.seekerSpawns.get(map.rng.nextInt(map.seekerSpawns.size())).location);
                            player.teleport(map.desiredSpawn.get(player));
                        } else {
                            map.teams.put(player, Team.SPECTATOR);
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                         */
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        var player = event.getPlayer();
        Map map = Map.playerIn(player);
        if (map != null) {
            player.spigot().respawn();
            var team = map.teams.get(player);
            if (team == Team.SEEKER) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(HNS.plugin, () -> {
                    player.teleport(map.desiredSpawn.get(player));
                    player.sendMessage("bocsi c:");
                }, 20L);
                player.teleport(map.desiredSpawn.get(player));
                map.announce(new CStr("&6" + player.getName() + "&c(KERESŐ) &7ki lett ütve.").str());
            } else if (team == Team.HIDER) {
                map.announce(new CStr("&6" + player.getName() + " &c(BÚJÓ) &7kiesett.").toString());
                if (map.override.deadCanSeek) {
                    map.teams.put(player, Team.SEEKER);
                    map.desiredSpawn.put(player, map.seekerSpawns.get(map.rng.nextInt(map.seekerSpawns.size())).location);
                    player.teleport(map.desiredSpawn.get(player));
                } else {
                    map.teams.put(player, Team.SPECTATOR);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        EditorMode mode = HNS.editorContext.getOrDefault(player, null);
        if (mode != null && player.isOp() && (!mode.inChat || msg.startsWith("#"))) {
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
                if ("list".equalsIgnoreCase(msg)) {
                    for (var s : Localization.helpList) {
                        player.sendMessage(s);
                    }
                } else {
                    msg = msg.substring("list ".length());
                    if (msg.equalsIgnoreCase("maps")) {
                        player.sendMessage("Maps:");
                        for (var map : HNS.maps.values()) {
                            player.sendMessage(map.id);
                        }
                    } else if (mode.map == null) {
                        player.sendMessage(Localization.mapNotSelected);
                    } else {
                        switch (msg.toLowerCase()) {
                            case "seekerspawn", "seekerspawns", "seeker-spawns", "seeker-spawn" -> {
                                for (int i = 0; i < mode.map.seekerSpawns.size(); i++) {
                                    var s = mode.map.seekerSpawns.get(i);
                                    player.sendMessage(new CStr(" &b" + i + ": &7" + s.toColoredString()).toString());
                                }
                                player.sendMessage("");
                            }
                            case "hiderspawn", "hiderspawns", "hider-spawns", "hider-spawn" -> {
                                for (int i = 0; i < mode.map.hiderSpawns.size(); i++) {
                                    var s = mode.map.hiderSpawns.get(i);
                                    player.sendMessage(new CStr(" &b" + i + ": &7" + s.toColoredString()).toString());
                                }
                                player.sendMessage("");
                            }
                        }
                    }
                }
            } else if (msg.startsWith("blocks")) {
                boolean append = false;
                if (msg.equalsIgnoreCase("blocks -a")) {
                    append = true;
                }
                HashSet<Material> materials = new HashSet<>();
                for (ItemStack stack : player.getInventory()) {
                    if (stack != null && stack.getType() != Material.AIR) {
                        materials.add(stack.getType());
                    }
                }
                if (append) {
                    mode.map.blockTypes.addAll(materials);
                } else {
                    mode.map.blockTypes = new ArrayList<>(materials);
                }
                player.sendMessage("Blocks updated");
            } else if (msg.startsWith("spawn")) {
                if (msg.equalsIgnoreCase("spawn")) {
                    player.sendMessage("spawn <name> <s|h|sh|hs> <icon>");
                } else {
                    msg = msg.substring("spawn ".length());
                    String[] words = msg.split(" ");
                    if (words.length == 3) {
                        boolean seeker = words[1].contains("s");
                        boolean hider = words[1].contains("h");
                        LocationData d = new LocationData();
                        d.name = words[0];
                        d.location = HNS.toCenter(player.getLocation());
                        d.icon = Material.valueOf(words[2].toUpperCase());
                        if (seeker) {
                            mode.map.seekerSpawns.add(d);
                        }
                        if (hider) {
                            mode.map.hiderSpawns.add(d);
                        }
                    } else {
                        player.sendMessage("spawn <name> <s|h|sh|hs> <icon>");
                    }
                }
            } else if (msg.equalsIgnoreCase("exit")) {
                HNS.editorContext.remove(player);
                HNS.editorContext.put(player, null);
                if (mode.map != null) {
                    mode.map.save();
                }
                mode = null;
                player.sendMessage("You exited from the editor.");
            } else if (msg.equalsIgnoreCase("enable")) {
                var s = mode.map.validate();
                if (s == null) {
                    mode.map.enabled = true;
                    player.sendMessage(new CStr("&aMap enabled.").toString());
                } else {
                    player.sendMessage(s);
                }
            } else if (msg.equalsIgnoreCase("disable")) {
                mode.map.enabled = false;
                player.sendMessage(new CStr("&cMap disabled.").toString());
            } else {
                player.sendMessage(Localization.invalidEditorCmd);
            }
            if (mode != null && mode.map != null) {
                mode.map.save();
            }
        }
        if (mode != null) {
            HNS.editorContext.put(player, mode);
        }
    }

    private String args(String l, Object... args) {
        return Localization.args(l, args);

    }

}
