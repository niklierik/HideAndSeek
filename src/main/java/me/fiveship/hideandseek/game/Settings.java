package me.fiveship.hideandseek.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {

    @JsonProperty("CanSelectSpawn")
    public Boolean canSelectSpawn = true;
    @JsonProperty("CanSelectBlock")
    public Boolean canSelectBlock = true;
    @JsonProperty("CanChangeBlock")
    public Boolean canChangeBlock = false;
    @JsonProperty("HideTime")
    public Integer hideTime = 60;
    @JsonProperty("SeekTime")
    public Integer seekTime = 300;
    @JsonProperty("TurningToBlockTime")
    public Integer turningToBlockTime = 3;
    @JsonProperty("MoveAsPlayer")
    public Boolean moveAsPlayer = false;


    @JsonCreator
    public Settings() {

    }

    public Settings(Settings s) {
        for (var field : getClass().getFields()) {
            try {
                field.set(s, field.get(this));
            } catch (Exception ignored) {
            }
        }
    }

    public Settings copy() {
        return new Settings(this);
    }

    public Settings keepNotNull(Settings global) {
        for (var field : getClass().getFields()) {
            try {
                var current = field.get(this);
                if (current == null) {
                    field.set(this, field.get(global));
                }
            } catch (Exception e) {
            }
        }
        return this;
    }
}
