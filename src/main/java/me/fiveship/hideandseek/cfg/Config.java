package me.fiveship.hideandseek.cfg;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.game.Settings;

import java.io.File;

public class Config {

    @JsonProperty("Global")
    public Settings global = new Settings();

    @JsonProperty("SaveMapsOnStop")
    public boolean saveMapsOnStop = true;
    @JsonProperty("SaveCfgOnStop")
    public boolean saveCfgOnStop = false;

    public static Config load() {
        try {
            return HNS.JSON.readValue(new File(HNS.pluginFolder, "config.json"), Config.class);
        } catch (Exception e) {
            return new Config();
        }
    }

    public void save() {
        try {
            HNS.JSON.writerWithDefaultPrettyPrinter().writeValue(new File(HNS.pluginFolder, "config.json"), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
