package me.fiveship.hideandseek.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.events.MainListener;
import me.fiveship.hideandseek.localization.CStr;
import me.fiveship.hideandseek.localization.Localization;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class Map {


    @JsonProperty("ID")
    public final String id;

    @JsonProperty("Overriding")
    public Settings override = new Settings();
    @JsonProperty("HiderSpawns")
    public List<LocationData> hiderSpawns = new ArrayList<>();
    @JsonProperty("SeekerSpawns")
    public List<LocationData> seekerSpawns = new ArrayList<>();
    @JsonProperty("Enabled")
    public boolean enabled = false;
    @JsonProperty("BlockTypes")
    public TreeSet<Material> blockTypes = new TreeSet<>(Comparator.comparing(Enum::toString));

    @JsonIgnore
    public HashSet<Player> players = new HashSet<>();
    @JsonIgnore
    public HashMap<Player, Material> materials = new HashMap<>();
    @JsonIgnore
    public Phase phase = Phase.WAITING;
    @JsonIgnore
    public HashMap<Player, Team> teams = new HashMap<>();
    @JsonIgnore
    public HashMap<Player, Location> desiredSpawn = new HashMap<>();

    @JsonIgnore
    private double timer;
    @JsonIgnore
    private final Random rng = new Random();

    @JsonCreator
    public Map(@JsonProperty("ID") String id) {
        this.id = id;
    }

    public void init() {
        override.keepNotNull(HNS.cfg.global);
    }

    public void onTick(double delta) {
        if (!enabled) {
            return;
        }
        if (players.isEmpty()) {
            phase = Phase.WAITING;
            return;
        }
        for (var entry : teams.entrySet()) {
            var player = entry.getKey();
            var team = entry.getValue();
            if (team == Team.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        int startTime = 0;
        int noPlayers = players.size();
        if (phase == Phase.WAITING) {
            startTime = override.waitingTime;
            if (override.waiting_startNo <= noPlayers) {
                timer += delta;
                if (timer >= override.waitingTime) {
                    start();
                }
            }
            if (override.start_startNo <= noPlayers) {
                phase = Phase.STARTING;
                timer = 0;
            }
        }
        if (phase == Phase.HIDING) {
            startTime = override.hideTime;
            timer += delta;
            if (timer >= override.hideTime) {
                toSeekPhase();
            }
        }
        if (phase == Phase.SEEKING) {
            startTime = override.seekTime;
            timer += delta;
            if (timer >= override.seekTime) {
                toEndPhase();
            }
        }
        if (phase == Phase.ENDING) {
            startTime = override.endTime;
            timer += delta;
            if (timer >= override.endTime) {
                end();
            }
        }
        if (phase == Phase.SEEKING || phase == Phase.HIDING) {
            for (var entry : teams.entrySet()) {
                var player = entry.getKey();
                var team = entry.getValue();
                player.setSaturation(10.0f);
                if (team == Team.HIDER) {
                    double d = HNS.timer.getOrDefault(player, 0.0);
                    d += delta;
                    var block = HNS.toCenter(player.getLocation()).getBlock();
                    if (d >= override.turningToBlockTime && block.getType() == Material.AIR) {
                        d = override.turningToBlockTime;
                        player.teleport(HNS.toCenter(player.getLocation()));
                        // HNS.disguise(player, null, 0);
                        player.setGameMode(GameMode.SPECTATOR);
                        try {
                            block.setType(materials.get(player), true);
                            HNS.blocks.put(player, block);
                            HNS.blocksR.put(block, player);
                        } catch (Exception ignored) {
                        }
                    }
                    HNS.timer.put(player, d);
                }
            }
        }
        int remain = (int) (startTime - timer);
        for (var player : players) {
            player.setLevel(remain);
            player.setExp(0);
        }
    }

    public void start() {
        phase = Phase.HIDING;
        timer = 0;
        var l = new ArrayList<>(players);
        Collections.shuffle(l);
        int noSeekers = l.size() / override.seekersByPlayer + 1;
        for (int i = 0; i < noSeekers; i++) {
            var p = l.get(i);
            teams.put(p, Team.SEEKER);
            desiredSpawn.put(p, seekerSpawns.get(rng.nextInt(seekerSpawns.size())).location);
        }
        for (var entry : teams.entrySet()) {
            if (entry.getValue() == Team.HIDER) {
                entry.getKey().teleport(desiredSpawn.get(entry.getKey()));
            }
        }
    }

    private void toSeekPhase() {
        phase = Phase.SEEKING;
        timer = 0;
    }

    private void toEndPhase() {
        phase = Phase.ENDING;
        timer = 0;
        for (var p : players) {
            MainListener.moved(p, p.getLocation().getY());
        }
    }

    private void end() {
        kickPlayers();
        phase = Phase.WAITING;
        timer = 0;
    }

    public void save() {
        try {
            File file = new File(HNS.mapsFolder(), id + ".json");
            HNS.JSON.writeValue(file, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void kickPlayer(Player p, String msg) {
        Map m = Map.playerIn(p);
        if (m != null) {
            m.announce(msg);
            m._kickPlayer(p);
        }
    }

    public void announce(String msg) {
        if (msg == null) {
            return;
        }
        for (var p : players) {
            p.sendMessage(msg);
        }
    }

    public void announceOnActionBar(String text) {
        for (var p : players) {
            p.sendActionBar(Component.text(text));
        }
    }

    public void _kickPlayer(Player p) {
        players.remove(p);
        teams.remove(p);
        p.teleport(HNS.cfg.lobby);
    }

    public void kickPlayers() {
        for (var player : new HashSet<>(players)) {
            HNS.players.remove(player.getUniqueId());
        }
        players.clear();
        teams.clear();
    }

    public void onDestroy() {
        if (HNS.cfg.saveMapsOnStop) {
            save();
        }
    }

    /**
     * @return error message or null if valid
     */
    public String validate() {
        if (blockTypes.isEmpty()) {
            return Localization.notSetBlocks;
        }
        if (hiderSpawns.isEmpty()) {
            return Localization.notSetHiderSpawns;
        }
        if (seekerSpawns.isEmpty()) {
            return Localization.notSetSeekerSpawns;
        }
        return null;
    }

    public static Map playerIn(Player pl) {
        for (Map m : HNS.maps.values()) {
            if (m.players.contains(pl)) {
                return m;
            }
        }
        return null;
    }

    public void join(Player player) {
        if (!enabled) {
            player.sendMessage(new CStr("&cMap is not enabled.").toString());
            return;
        }
        players.add(player);
        teams.put(player, Team.HIDER);
        materials.put(player, new ArrayList<>(blockTypes).get(rng.nextInt(blockTypes.size())));
        desiredSpawn.put(player, hiderSpawns.get(rng.nextInt(hiderSpawns.size())).location);
    }

    public void blockChooser(Player player) {
        var inv = Bukkit.createInventory(null, 54, Component.text("Blokkok"));
        
    }
}
