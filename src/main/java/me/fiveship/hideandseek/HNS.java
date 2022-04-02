package me.fiveship.hideandseek;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.fiveship.hideandseek.cfg.Config;
import me.fiveship.hideandseek.game.Map;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;

public final class HNS extends JavaPlugin {

    public static final ObjectMapper JSON = new ObjectMapper();
    public static File pluginFolder;
    public static Config cfg;
    public static final HashSet<Map> maps = new HashSet<>();

    @Override
    public void onEnable() {
        pluginFolder = getDataFolder();
        pluginFolder.mkdirs();
        cfg = Config.load();
        cfg.save();
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
    }
}
