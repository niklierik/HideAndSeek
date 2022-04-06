package me.fiveship.hideandseek.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.fiveship.hideandseek.HNS;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Map {

    @JsonProperty("ID")
    public final String id;

    @JsonProperty("Overriding")
    public Settings override = new Settings();
    @JsonProperty("HiderSpawns")
    public List<Location> hiderSpawns = new ArrayList<>();
    @JsonProperty("SeekerSpawns")
    public List<Location> seekerSpawns = new ArrayList<>();
    @JsonProperty("Enabled")
    public boolean enabled = false;

    @JsonIgnore
    public HashSet<Player> players = new HashSet<>();
    @JsonIgnore
    public HashMap<Player, Material> materials = new HashMap<>();
    @JsonIgnore
    public HashMap<Player, Integer> datas = new HashMap<>();

    @JsonCreator
    public Map(@JsonProperty("ID") String id) {
        this.id = id;
    }

    public void init() {
        override.keepNotNull(HNS.cfg.global);
    }

    public void onTick(double delta) {
        for (var player : players) {
            player.setSaturation(10.0f);
            double d = HNS.timer.getOrDefault(player, 0.0);
            d += delta;
            var block = HNS.toCenter(player.getLocation()).getBlock();
            if (d >= override.turningToBlockTime && block.getType() == Material.AIR) {
                d = override.turningToBlockTime;
                player.teleport(HNS.toCenter(player.getLocation()));
               // HNS.disguise(player, null, 0);
                player.setGameMode(GameMode.SPECTATOR);
                block.setType(block.getType(), true);
                HNS.blocks.put(player, block);
            }
            HNS.timer.put(player, d);
        }
    }

    public void save() {
        try {
            File file = new File(HNS.mapsFolder(), id + ".json");
            HNS.JSON.writeValue(file, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (HNS.cfg.saveMapsOnStop) {
            save();
            for (var p : players) {
                HNS.players.add(p.getUniqueId());
            }
            players.clear();
        }
    }

    public static Map playerIn(Player pl) {
        for (Map m : HNS.maps.values()) {
            if (m.players.contains(pl)) {
                return m;
            }
        }
        return null;
    }

}
