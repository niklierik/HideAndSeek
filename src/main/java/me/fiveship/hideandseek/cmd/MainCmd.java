package me.fiveship.hideandseek.cmd;

import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.localization.Localization;
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
        try {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if (args.length == 0) {
                helpCmd(sender, label);
            } else {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "help" -> {
                        helpCmd(sender, label);
                    }
                    case "block" -> {
                        if (player != null && player.isOp()) {
                            Material block = null;
                            int v = 0;
                            if (args.length >= 2) {
                                block = Material.valueOf(args[1].toUpperCase());
                            }
                            if (args.length >= 3) {
                                v = Integer.parseInt(args[2]);
                            }
                            HNS.disguise(player, block, v);
                            player.sendMessage("Success!");
                        } else {
                            invalidCmd(sender, label);
                        }
                    }
                    case "center" -> {
                        if (player != null && player.isOp()) {
                            player.teleport(HNS.toCenter(player.getLocation()));
                        } else {
                            invalidCmd(sender, label);
                        }
                    }
                    case "editor" -> {
                        if (player != null && player.isOp()) {
                            player.sendMessage(Localization.args(Localization.entersEditor));
                            HNS.inEditor.add(player);
                        } else {
                            invalidCmd(sender, label);
                        }
                    }
                    default -> {
                        invalidCmd(sender, label);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void invalidCmd(CommandSender sender, String lbl) {
        sender.sendMessage(Localization.args(Localization.invalidCmd, lbl));
    }

    private void helpCmd(CommandSender player, String lbl) {
        for (var s : player.isOp() ? Localization.args(Localization.helpCmdIfOp, lbl) : Localization.args(Localization.helpCmd, lbl)) {
            player.sendMessage(s);
        }
    }
}
