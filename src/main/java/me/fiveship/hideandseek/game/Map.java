package me.fiveship.hideandseek.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.fiveship.hideandseek.HNS;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class Map implements Serializable {

    @JsonProperty("ID")
    public final String id;

    @JsonProperty("Overriding")
    public Settings override = new Settings();

    @JsonCreator
    public Map(@JsonProperty("ID") String id) {
        this.id = id;
    }

    public void init() {
        override.keepNotNull(HNS.cfg.global);
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
        }
    }

}
