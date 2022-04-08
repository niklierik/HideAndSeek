package me.fiveship.hideandseek.cfg;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.game.Settings;
import me.fiveship.hideandseek.localization.Lang;
import org.bukkit.Location;

import java.io.File;

public class Config {

    @JsonProperty("Global")
    public Settings global = new Settings();

    @JsonProperty("SaveMapsOnStop")
    public boolean saveMapsOnStop = true;
    @JsonProperty("SaveCfgOnStop")
    public boolean saveCfgOnStop = false;
    @JsonProperty("Lang")
    public Lang lang = Lang.ENGLISH;
    @JsonProperty("Lobby")
    public Location lobby;
    @JsonProperty("TeleportToLobby")
    public boolean teleportToLobby;

    public Config() {
    }

    public static Config load() {
        Config c;
        try {
            c = HNS.JSON.readValue(new File(HNS.pluginFolder, "config.json"), Config.class);
        } catch (Exception e) {
            c = new Config();
        }
        Settings d = new Settings();
        d.setDefault();
        c.global.keepNotNull(d);
        return c;
    }

    public void save() {
        try {
            HNS.JSON.writerWithDefaultPrettyPrinter().writeValue(new File(HNS.pluginFolder, "config.json"), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
