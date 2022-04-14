package me.fiveship.hideandseek.cmd;

import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.game.Map;
import me.fiveship.hideandseek.localization.CStr;
import me.fiveship.hideandseek.localization.Localization;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
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
                            sender.sendMessage(new CStr("&cInvalid syntax, try &6/" + label + " " + args[0] + " &7<map>&c.").toString());
                        }
                    }
                    case "block", "blocks" -> {
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
                    case "spawn", "spawns" -> {
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
                    case "leave" -> {
                        if (player == null) {
                            invalidCmd(sender, label);
                        } else {
                            Map map = Map.playerIn(player);
                            if (map == null) {
                                invalidCmd(sender, label);
                            } else {
                                Map.kickPlayer(player, player.getName() + " kilépett.");
                            }
                        }
                    }
                    case "facing", "rotate" -> {
                        try {
                            Block block = HNS.blocks.get(player);
                            if (block == null) {
                                sender.sendMessage(ChatColor.RED + "Csak blokk formában használhatod ezt a parancsot.");
                                return false;
                            }
                            var state = block.getState();
                            var data = state.getBlockData();
                            if (data instanceof Directional d) {
                                String direction = "next";
                                try {
                                    direction = args[1];
                                } catch (Exception ignored) {
                                }
                                int index = 0;
                                for (; index < BlockFace.values().length; index++) {
                                    if (BlockFace.values()[index] == d.getFacing()) {
                                        break;
                                    }
                                }
                                BlockFace target;
                                if ("next".equalsIgnoreCase(direction)) {
                                    target = BlockFace.values()[index + 1];
                                } else if ("prev".equalsIgnoreCase(direction)) {
                                    target = BlockFace.values()[index - 1];
                                } else {
                                    target = BlockFace.valueOf(direction.toUpperCase());
                                }
                                d.setFacing(target);
                                state.setBlockData(data);
                            }
                        } catch (Exception e) {
                            sender.sendMessage(e.getMessage());
                        }
                    }
                    case "up", "top" -> {
                        try {
                            Block block = HNS.blocks.get(player);
                            if (block == null) {
                                sender.sendMessage(ChatColor.RED + "Csak blokk formában használhatod ezt a parancsot.");
                                return false;
                            }
                            var state = block.getState();
                            var data = state.getBlockData();
                            if (data instanceof Bisected b) {
                                b.setHalf(Bisected.Half.TOP);
                                state.setBlockData(data);
                            }
                        } catch (Exception e) {
                            sender.sendMessage(e.getMessage());
                        }
                    }
                    case "down", "bottom" -> {
                        try {
                            Block block = HNS.blocks.get(player);
                            if (block == null) {
                                sender.sendMessage(ChatColor.RED + "Csak blokk formában használhatod ezt a parancsot.");
                                return false;
                            }
                            var state = block.getState();
                            var data = state.getData();
                            if (data instanceof Bisected b) {
                                b.setHalf(Bisected.Half.BOTTOM);
                            }
                        } catch (Exception e) {
                            sender.sendMessage(e.getMessage());
                        }
                    }
                    case "seek" -> {
                        if (player != null) {
                            Map map = Map.playerIn(player);
                            if (map == null) {
                                invalidCmd(player, label);
                            } else {
                                map.wantToSeek.add(player);
                            }
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
