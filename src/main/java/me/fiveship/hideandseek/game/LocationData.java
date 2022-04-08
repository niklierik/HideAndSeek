package me.fiveship.hideandseek.game;

import org.bukkit.Location;
import org.bukkit.Material;

public class LocationData {

    public String name = "";
    public Location location;
    public Material icon;

    @Override
    public String toString() {
        return name + ": " + (int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ() + " in " + location.getWorld().getName();
    }

    public String toColoredString() {
        return "&b" + name + "&7: &e" + (int) location.getX() + "&7, &e" + (int) location.getY() + "&7, &e" + (int) location.getZ() + " &7in &e" + location.getWorld().getName();
    }

}
