package me.fiveship.hideandseek.localization;

import java.util.ArrayList;
import java.util.List;

public class Localization {

    public static List<String> helpCmd = new ArrayList<>();
    public static String invalidCmd = "";

    public static void set(Lang l) {
        switch (l) {
            case ENGLISH -> {
                invalidCmd = "&cCommand was not found. Try &6/{0} help&c.";
                helpCmd.add(c("&7----&b<&eCommands&b>&7----"));
                helpCmd.add(c("  &f/{0} &ehelp &7&l- &bShows the list of commands."));
            }
            case HUNGARIAN -> {
                invalidCmd = "&cA parancs nem található. Próbáld meg a &6/{0} help&c parancsot.";
                helpCmd.add(c("&7----&b<&eParancsok&b>&7----"));
                helpCmd.add(c("  &f/{0} &ehelp &7&l- &bKilistázza a parancsokat."));
            }
        }
    }

    private static String c(String s) {
        return CStr.of(s);
    }

    public static List<String> args(List<String> l, Object... args) {
        List<String> list = new ArrayList<>(l);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, args(l.get(i), args));
        }
        return list;
    }

    public static String args(String s, Object... args) {
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", args[i].toString());
        }
        return s;
    }

}
