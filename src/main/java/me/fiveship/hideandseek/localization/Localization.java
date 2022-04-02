package me.fiveship.hideandseek.localization;

import java.util.ArrayList;
import java.util.List;

public class Localization {

    public static List<String> helpCmd = new ArrayList<>();

    public static void set(Lang l) {
        switch (l) {
            case ENGLISH -> {
                helpCmd.add(CStr.of("&7----&b<&eCommands&b>&7----"));
                helpCmd.add(CStr.of(" &7&l- &f/{0}"));
            }
            case HUNGARIAN -> {
                //TODO
            }
        }
    }

    public static List<String> args(List<String> l, Object... args) {
        List<String> list = new ArrayList<>(l);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, pushArgs(l.get(i), args));
        }
        return list;
    }

    public static String pushArgs(String s, Object... args) {
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", args[i].toString());
        }
        return s;
    }

}
