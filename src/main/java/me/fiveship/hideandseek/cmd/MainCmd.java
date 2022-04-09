package me.fiveship.hideandseek.cmd;

import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.game.Map;
import me.fiveship.hideandseek.localization.CStr;
import me.fiveship.hideandseek.localization.Localization;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
                            HNS.editorContext.put(player, new EditorMode());
                        } else {
                            invalidCmd(sender, label);
                        }
                    }
                    case "join" -> {
                        if (player != null) {
                            if (args.length == 2) {
                                Map map = HNS.maps.getOrDefault(args[1], null);
                                if (map == null) {
                                    player.sendMessage(new CStr("&cInvalid map ID.").toString());
                                } else {
                                    map.join(player);
                                }
                            } else {
                                player.sendMessage(new CStr("&cInvalid syntax, try &6/" + label + " join &7<map>&c.").toString());
                            }
                        } else {
                            invalidCmd(sender, label);
                        }
                    }
                    case "forcestart", "start" -> {
                        if (args.length == 2) {
                            Map map = HNS.maps.getOrDefault(args[1], null);
                            if (map == null) {
                                sender.sendMessage(new CStr("&cInvalid map ID.").toString());
                            } else {
                                map.start();
                            }
                        } else {
                            sender.sendMessage(new CStr("&cInvalid syntax, try &6/" + label + " " + args[1] + " &7<map>&c.").toString());
                        }
                    }
                    case "block" -> {
                        if (player == null) {
                            invalidCmd(sender, label);
                        } else {
                            Map map = Map.playerIn(player);
                            if (map == null) {
                                invalidCmd(sender, label);
                            } else {
                                map.blockChooser(player);
                            }
                        }
                    }
                    case "spawn" -> {
                        if (player == null) {
                            invalidCmd(sender, label);
                        } else {
                            Map map = Map.playerIn(player);
                            if (map == null) {
                                invalidCmd(sender, label);
                            } else {
                                map.spawnChooser(player);
                            }
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
