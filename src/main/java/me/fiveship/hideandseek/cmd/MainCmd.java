package me.fiveship.hideandseek.cmd;

import me.fiveship.hideandseek.localization.Localization;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MainCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (args.length == 0) {
            helpCmd(player, label);
        } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "help" -> {
                    helpCmd(player, label);
                }
                case "test" -> {
                    if (player != null) {
                        var block = Material.valueOf(args[1].toUpperCase());
                        MiscDisguise miscDisguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, block, 0);
                        miscDisguise.setEntity(player);
                        miscDisguise.startDisguise();
                    }
                }
            }
        }
        return false;
    }

    private void invalidCmd(CommandSender sender, String lbl) {
        sender.sendMessage(Localization.args(Localization.invalidCmd, lbl));
    }

    private void helpCmd(Player player, String lbl) {
        for (var s : Localization.args(Localization.helpCmd, lbl)) {
            player.sendMessage(s);
        }
    }
}
