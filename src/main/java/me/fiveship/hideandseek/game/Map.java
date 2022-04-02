package me.fiveship.hideandseek.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.fiveship.hideandseek.HNS;

import java.io.Serializable;
import java.util.HashMap;

public class Map implements Serializable {

    @JsonProperty("Overriding")
    public Settings override = new Settings();

    public Map() {

    }

    public void init() {
        override.keepNotNull(HNS.cfg.global);
    }

    public void onDestroy() {

    }

}
