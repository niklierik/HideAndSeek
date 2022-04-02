package me.fiveship.hideandseek;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.fiveship.hideandseek.cfg.Config;
import me.fiveship.hideandseek.cmd.MainCmd;
import me.fiveship.hideandseek.game.Map;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;

public final class HNS extends JavaPlugin {

    public static final ObjectMapper JSON = new ObjectMapper();
    public static File pluginFolder;
    public static Config cfg;
    public static final HashSet<Map> maps = new HashSet<>();

    public static File mapsFolder() {
        return new File(pluginFolder, "Maps");
    }

    @Override
    public void onEnable() {
        pluginFolder = getDataFolder();
        pluginFolder.mkdirs();
        mapsFolder().mkdirs();
        cfg = Config.load();
        cfg.save();
        loadMaps();
    }

    private void loadMaps() {
        for (File f : Objects.requireNonNull(mapsFolder().listFiles())) {
            try {
                var m = JSON.readValue(f, Map.class);
                maps.add(m);
            } catch (Exception ignored) {
            }
        }
        for (Map m : maps) {
            try {
                m.init();
            } catch (Exception ignored) {
            }
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("hns")).setExecutor(new MainCmd());
    }

    @Override
    public void onDisable() {
        try {
            for (var map : maps) {
                try {
                    map.onDestroy();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        if (cfg.saveCfgOnStop) {
            cfg.save();
        }
    }
}
