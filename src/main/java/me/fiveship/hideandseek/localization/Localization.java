package me.fiveship.hideandseek.localization;

import java.util.ArrayList;
import java.util.List;

public class Localization {

    public static List<String> helpCmd = new ArrayList<>();
    public static List<String> helpCmdIfOp = new ArrayList<>();
    public static List<String> helpEditor = new ArrayList<>();
    public static String invalidCmd = "";
    public static String entersEditor;
    public static String invalidEditorCmd;
    public static String invalidMapName;
    public static String takenName;
    public static String mapCreated;
    public static String mapSelected;
    public static String mapDoesNotExist;
    public static List<String> helpList = new ArrayList<>();
    public static String mapNotSelected;
    public static String helpMap;
    public static String helpCreate;
    public static String notSetBoundMin;
    public static String notSetBoundMax;
    public static String notSetBlocks;
    public static String notSetHiderSpawns;
    public static String notSetSeekerSpawns;

    public static void set(Lang l) {
        set(l.toString());
    }

    public static void set(String l) {
        switch (l.toUpperCase()) {
            case "ENGLISH" -> {
                helpCmd.add(c("&7----&b<&eCommands&b>&7----"));
                helpCmd.add(c("  &f/{0} &ehelp &7&l- &bShows the list of commands"));
                helpCmd.add(" ");
                /////
                helpCmdIfOp.addAll(helpCmd);
                helpCmdIfOp.add(c("  &f/{0} &eeditor &7&l- &bEditor mode"));
                helpCmdIfOp.add(" ");
                /////
                helpEditor.add(c("&7----&b<&eCommands&b>&7----"));
                helpEditor.add(c("  &e#{command} &7&l- &bActivates this command even if you send it in chat-mode"));
                helpEditor.add(c("  &ehelp &7&l- &bList of commands"));
                helpEditor.add(c("  &eexit &7&l- &bExit the editor"));
                helpEditor.add(c("  &echat &7&l- &bSends your message to the chat"));
                helpEditor.add(c("  &echat-on &7&l- &bSends your next messages to the chat"));
                helpEditor.add(c("  &e[#]chat-off &7&l- &bDisables chat-mode and "));
                helpEditor.add(c(" "));
                /////
                helpList.add(c("&6list &b<mode>"));
                helpList.add(c("&7----&b<&eModes&b>&7----"));
                helpList.add(c("  &eseekerspawn&6|&eseeker-spawn&6|&eseeker-spawns&6|&eseekerspawns&6|&ess &7&l:"));
                helpList.add(c("    &bList the seeker spawns with indicies"));
                helpList.add(c("  &ehiderspawn&6|&ehider-spawn&6|&ehider-spawns&6|&ehiderspawns&6|&ehs &7&l:"));
                helpList.add(c("    &bList the hider spawns with indicies"));
                helpList.add("");
                /////
                invalidCmd = "&cCommand was not found. Try &6/{0} help&c.";
                entersEditor = c("You opened the &bEditor&f! Just write to the chat to enter your commands. Try &6help &fto list these commands.");
                invalidEditorCmd = c("This command does not exist. Try &6help &ffor help or &6chat &fto write to chat or &cexit &fto exit the editor.");
                invalidMapName = c("&cInvalid map name.");
                takenName = c("&cThe map's id is already taken. &6Use &cselect {0} &6to select this map.");
                mapCreated = c("&6Map &b{0} &6is created and selected.");
                mapSelected = c("&6Map &b{0} &6is selected.");
                mapDoesNotExist = c("&cMap &6{0} &cdoes not exist.");
                mapNotSelected = c("&cYou need to select a map before you use this command.");
                helpMap = c("&cInvalid syntax, try &6map &c<map-ID>&c.");
                helpCreate = c("&cInvalid syntax, try &6create &c<map-ID>&c.");
                notSetBoundMin = c("&cMap's bound is not set.");
                notSetBoundMax = c("&cMap's bound is not set.");
                notSetBlocks = c("&cBlocks that hiders can use are not set.");
                notSetHiderSpawns = c("&cThere is no valid hider spawn.");
                notSetSeekerSpawns = c("&cThere is no valid seeker spawn.");
            }
            case "HUNGARIAN" -> {
                helpCmd.add(c("&7----&b<&eParancsok&b>&7----"));
                helpCmd.add(c("  &f/{0} &ehelp &7&l- &bKilist??zza a parancsokat"));
                helpCmd.add(" ");
                /////
                helpCmdIfOp.addAll(helpCmd);
                helpCmdIfOp.add(c("  &f/{0} &eeditor &7&l- &bEditor m??d"));
                helpCmdIfOp.add(" ");
                /////
                helpEditor.add(c("&7----&b<&eParancsok&b>&7----"));
                helpEditor.add(c("  &e#{command} &7&l- &bBeaktiv??lja a parancsot att??l f??ggetlen??l, hogy chat mode-ba vagy-e vagy sem"));
                helpEditor.add(c("  &ehelp &7&l- &bParancsok list??ja"));
                helpEditor.add(c("  &eexit &7&l- &bKil??psz a szerkeszt??b??l"));
                helpEditor.add(c("  &echat &7&l- &bParancs ??rtelmez??se helyett a chat-re k??ld??d amit ??rsz "));
                helpEditor.add(c("  &echat-on &7&l- &bChat-mode-ba v??ltasz ??s a k??vetkez?? ??zeneteket nem parancsk??nt lesz ??rtelmezve"));
                helpEditor.add(c("  &e[#]chat-off &7&l- &bKikapcsolja a chat-mode-ot"));
                helpEditor.add(c(" "));
                /////
                helpList.add(c("&6list &b<mode>"));
                helpList.add(c("&7----&b<&eModes&b>&7----"));
                helpList.add(c("  &eseekerspawn&6|&eseeker-spawn&6|&eseeker-spawns&6|&eseekerspawns&6|&ess &7&l:"));
                helpList.add(c("    &bKilist??zza a keres??k spawnjait indexekkel"));
                helpList.add(c("  &ehiderspawn&6|&ehider-spawn&6|&ehider-spawns&6|&ehiderspawns&6|&ehs &7&l:"));
                helpList.add(c("    &bKilist??zza a b??j??k spawnjait indexekkel"));
                helpList.add("");
                /////
                invalidCmd = c("&cA parancs nem tal??lhat??. Pr??b??ld meg a &6/{0} help&c parancsot.");
                entersEditor = c("Bel??pt??l az &bEditor&f-ba! Csak a chat-be kell be??rnod norm??lis sz??vegk??nt a parancsokat. Pr??b??ld ki a &6help &fparancsot, hogy ezeket megn??zd.");
                invalidEditorCmd = c("Ez a parancs nem l??tezik. Pr??b??ld meg a &6help &fparancsot seg??ts??g??rt, &6chat &fparancsot, hogy a chat-re ??rj vagy az &cexit &fparancsot, hogy kil??pj a szerkeszt??b??l.");
                invalidMapName = c("&cA map ID-je csak az angol ABC bet??it tartalmazhatja, illetve sz??mokat, de sz??mmal nem kezd??dhet.");
                takenName = c("&cM??r l??tezik map ilyen ID-vel.");
                mapSelected = c("&6A(z) &b{0} &6map elk??sz??lt ??s ki lett v??lasztva.");
                mapCreated  = c("&6A(z) &b{0} &6map elk??sz??lt.");
                mapNotSelected = c("&cEl??sz??r egy mapot kell kiv??lasztanod, hogy ezt a parancsot lefuttasd.");
                helpMap = c("&cHelytelen szintaxis, pr??b??ld meg ezt: &6map &c<map-ID>&c.");
                helpCreate = c("&cHelytelen szintaxis, pr??b??ld meg ezt: &6create &c<map-ID>&c.");
                notSetBoundMin = c("&cMap hat??ra nincs be??ll??tva.");
                notSetBoundMax = c("&cMap hat??ra nincs be??ll??tva.");
                notSetBlocks = c("&cBlokkok, amiket a b??jok tudnak haszn??lni nincsenek be??ll??tva.");
                notSetHiderSpawns = c("&cB??j??k spawnja nincs be??ll??tva.");
                notSetSeekerSpawns = c("&cKeres??k spawnja nincs be??ll??tva.");
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
