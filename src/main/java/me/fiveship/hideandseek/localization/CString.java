package me.fiveship.hideandseek.localization;

import org.bukkit.ChatColor;

public class CString {

    private String s;

    public static final char COLOR_CODE_MARKER = '&';

    public CString(CString cs) {
        this.s = cs.s;
        refresh();
    }

    public CString(String s) {
        this.s = s;
        refresh();
    }

    public void refresh() {
        s = of(s);
    }

    public String str() {
        return s;
    }

    public static String of(String s) {
        return ChatColor.translateAlternateColorCodes(COLOR_CODE_MARKER, s);
    }

    @Override
    public String toString() {
        return str();
    }
}
