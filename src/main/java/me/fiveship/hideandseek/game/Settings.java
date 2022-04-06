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
    @JsonProperty("WaitingTime")
    public Integer waitingTime = 30;
    @JsonProperty("StartingTime")
    public Integer startingTime = 5;
    @JsonProperty("HideTime")
    public Integer hideTime = 60;
    @JsonProperty("SeekTime")
    public Integer seekTime = 300;
    @JsonProperty("EndTime")
    public Integer endTime = 10;
    @JsonProperty("NoPlayersForStartAsWaiting")
    public Integer waiting_startNo = 3;
    @JsonProperty("NoPlayersForStartAsStarting")
    public Integer start_startNo = 15;
    @JsonProperty("TurningToBlockTime")
    public Integer turningToBlockTime = 3;
    @JsonProperty("MoveAsPlayer")
    public Boolean moveAsPlayer = false;
    @JsonProperty("SeekerHealth")
    public Double seekerHP = 5.0;
    @JsonProperty("HiderHealth")
    public Double hiderHP = 2.0;
    @JsonProperty("MiningFatigueForSeeker")
    public Integer seekerMiningFatigue = 0;
    @JsonProperty("MiningFatigueForHider")
    public Integer hiderMiningFatigue = 0;
    @JsonProperty("DeadCanSeek")
    public Boolean deadCanSeek = false;
    @JsonProperty("MaxSeeker")
    public Integer maxSeeker = -1;
    /**
     * Let n be this value. (def 5)<br>
     * Let h be the number of hiders.<br>
     * Let s be the number of seekers.<br>
     * Let p be the number of players. (p = s + h)<br>
     * s = (int)(p / n) + 1;<br>
     * So with default value (5)<br>
     * 2-4 players: 1 seeker<br>
     * 5-9 players: 2 seeker<br>
     * 10-14 players: 3 seeker<br>
     * ...
     */
    @JsonProperty("SeekersByPlayer")
    public Integer seekersByPlayer = 5;
    @JsonProperty("MaxPlayers")
    public Integer maxPlayers = -1;


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

    public void keepNotNull(Settings global) {
        for (var field : getClass().getFields()) {
            try {
                var current = field.get(this);
                if (current == null) {
                    field.set(this, field.get(global));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
