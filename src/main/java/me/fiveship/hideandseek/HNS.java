package me.fiveship.hideandseek;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.fiveship.hideandseek.cfg.Config;
import me.fiveship.hideandseek.cmd.EditorMode;
import me.fiveship.hideandseek.cmd.MainCmd;
import me.fiveship.hideandseek.events.InvEvents;
import me.fiveship.hideandseek.events.MainListener;
import me.fiveship.hideandseek.game.Map;
import me.fiveship.hideandseek.localization.Localization;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public final class HNS extends JavaPlugin {

    public static final ObjectMapper JSON = new ObjectMapper();
    public static File pluginFolder;
    public static Config cfg;
    public static final HashMap<String, Map> maps = new HashMap<>();
    public static final HashMap<Player, Double> timer = new HashMap<>();
    public static final HashMap<Player, MiscDisguise> disguises = new HashMap<>();
    public static final HashMap<Player, Block> blocks = new HashMap<>();
    public static final HashMap<Block, Player> blocksR = new HashMap<>();
    public static double deltaTime;
    public static HNS plugin;
    private static long currentTime;
    private static long lastTime;
    public static final HashSet<UUID> players = new HashSet<>();
    public static HashMap<Player, EditorMode> editorContext = new HashMap<>();
    public static Team inGameTeam;

    public static File mapsFolder() {
        return new File(pluginFolder, "Maps");
    }

    @Override
    public void onEnable() {
        plugin = this;
        initJSON();
        pluginFolder = getDataFolder();
        pluginFolder.mkdirs();
        mapsFolder().mkdirs();
        cfg = Config.load();
        cfg.save();
        Localization.set(cfg.lang);
        loadMaps();
        registerCommands();
        registerListeners();
        lastTime = currentTime = System.nanoTime();
        inGameTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("HNS_InGame");
        if (inGameTeam == null) {
            inGameTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("HNS_InGame");
            inGameTeam.setAllowFriendlyFire(true);
            inGameTeam.setCanSeeFriendlyInvisibles(false);
            inGameTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            inGameTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
    }

    private void initJSON() {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        SimpleModule m = new SimpleModule("Hide And Seek JSON Module");
        m.addDeserializer(Location.class, new JsonDeserializer<>() {
            @Override
            public Location deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                JsonNode node = p.getCodec().readTree(p);
                var yaw = 0f;
                var pitch = 0f;
                if (node.hasNonNull("Yaw")) {
                    yaw = (float) node.get("Yaw").asDouble();
                }
                if (node.hasNonNull("Pitch")) {
                    pitch = (float) node.get("Pitch").asDouble();
                }
                return new Location(Bukkit.getWorld(node.get("World").asText()), node.get("X").asDouble(), node.get("Y").asDouble(), node.get("Z").asDouble(), yaw, pitch);
            }
        });
        m.addSerializer(Location.class, new JsonSerializer<>() {
            @Override
            public void serialize(Location value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeStringField("World", value.getWorld().getName());
                gen.writeNumberField("X", value.getX());
                gen.writeNumberField("Y", value.getY());
                gen.writeNumberField("Z", value.getZ());
                gen.writeNumberField("Yaw", value.getYaw());
                gen.writeNumberField("Pitch", value.getPitch());
                gen.writeEndObject();
            }
        });
        m.addDeserializer(Entity.class, new JsonDeserializer<>() {
            @Override
            public Entity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                return Bukkit.getEntity(UUID.fromString(p.readValueAs(String.class)));
            }
        });
        m.addSerializer(Entity.class, new JsonSerializer<>() {
            @Override
            public void serialize(Entity value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.getUniqueId().toString());
            }
        });
        JSON.registerModule(m);
    }

    private void loadMaps() {
        for (File f : Objects.requireNonNull(mapsFolder().listFiles())) {
            try {
                var m = JSON.readValue(f, Map.class);
                maps.put(m.id, m);
            } catch (Exception ignored) {
            }
        }
        for (Map m : maps.values()) {
            try {
                m.init();
            } catch (Exception ignored) {
            }
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("hns")).setExecutor(new MainCmd());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        Bukkit.getPluginManager().registerEvents(new InvEvents(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            onTick(deltaTime);
            lastTime = currentTime;
        }, 1L, 1L);
    }

    private void onTick(double deltaTime) {
        for (Map m : maps.values()) {
            m.onTick(deltaTime);
        }
    }

    @Deprecated
    public static void disguise(Player player, Material m, int v) {
        var old = disguises.getOrDefault(player, null);
        if (old != null) {
            old.stopDisguise();
        }
        if (m != null) {
            MiscDisguise d = new MiscDisguise(DisguiseType.FALLING_BLOCK, m, v);
            d.setEntity(player);
            d.startDisguise();
            disguises.put(player, d);
        } else {
            disguises.put(player, null);
        }
    }

    public static Location toCenter(Location l) {
        double x = l.getX() - 0.5;
        double y = l.getY();
        double z = l.getZ() - 0.5;
        x = Math.round(x) + 0.5;
        z = Math.round(z) + 0.5;
        var l2 = new Location(l.getWorld(), x, y, z);
        l2.setPitch(l.getPitch());
        l2.setYaw(l.getYaw());
        return l2;
    }

    @Override
    public void onDisable() {
        try {
            for (var map : maps.values()) {
                try {
                    map.onDestroy();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        if (cfg.saveCfgOnStop) {
            cfg.save();
        }
        try {
            JSON.writeValue(new File(pluginFolder, "players.json"), players);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
