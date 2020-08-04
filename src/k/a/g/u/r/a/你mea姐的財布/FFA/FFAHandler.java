package k.a.g.u.r.a.你mea姐的財布.FFA;

import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.Config.ConfigCursor;
import c.aqua.neeeeee.utils.InventoryUtil;
import c.aqua.neeeeee.utils.LocationUtil;
import io.netty.util.internal.ConcurrentSet;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.tekokitools.MathUtils;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyStatus;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FFAHandler {

    @Getter private Set<FFAPlayer> ffaPlayers;
    @Getter private Map<FFAPlayer, FFAKit> kitMap;

    @Getter@Setter
    private Location spawn;

    @Getter@Setter
    private Location spawnProt1;
    @Getter
    private Location spawnProt2;

    private double minX;
    private double maxX;
    private double minZ;
    private double maxZ;
    private double minY;
    private double maxY;

    @Getter
    private List<Entity> drops;

    public FFAHandler() {
        this.ffaPlayers = new ConcurrentSet<FFAPlayer>(); //线程安全类，边遍历边删增没问题
        this.spawn = new Location(Bukkit.getWorlds().get(0), 0 ,0 ,0);
        this.spawnProt1 = new Location(Bukkit.getWorlds().get(0), 0 ,0 ,0);
        this.spawnProt2 = new Location(Bukkit.getWorlds().get(0), 0 ,0 ,0);
        this.kitMap = new HashMap<>();
        this.drops = new ArrayList<>();
        this.registerKits();
        this.load();
    }

    public void load() {
        ConfigCursor cursor = new ConfigCursor(Tekoki.tekoki().getFFaConfig(), "ffa");
        this.spawn = LocationUtil.deserialize(cursor.getString("spawn"));
        this.spawnProt1 = LocationUtil.deserialize(cursor.getString("spawnProtection1"));
        this.spawnProt2 = LocationUtil.deserialize(cursor.getString("spawnProtection2"));
        minX = MathUtils.Xmin(spawnProt1, spawnProt2);
        maxX = MathUtils.Xmax(spawnProt1, spawnProt2);
        minZ = MathUtils.Zmin(spawnProt1, spawnProt2);
        maxZ = MathUtils.Zmax(spawnProt1, spawnProt2);
        minY = MathUtils.Ymin(spawnProt1, spawnProt2);
        maxY = MathUtils.Ymax(spawnProt1, spawnProt2);
    }

    public void save() {
        ConfigCursor cursor = new ConfigCursor(Tekoki.tekoki().getFFaConfig(), "ffa");
        cursor.set("spawn", LocationUtil.serialize(spawn));
        cursor.set("spawnProtection1", LocationUtil.serialize(spawnProt1));
        cursor.set("spawnProtection2", LocationUtil.serialize(spawnProt2));
        cursor.save();
    }

    public void refactorSpawn() {
        minX = MathUtils.Xmin(spawnProt1, spawnProt2);
        maxX = MathUtils.Xmax(spawnProt1, spawnProt2);
        minZ = MathUtils.Zmin(spawnProt1, spawnProt2);
        maxZ = MathUtils.Zmax(spawnProt1, spawnProt2);
        minY = MathUtils.Ymin(spawnProt1, spawnProt2);
        maxY = MathUtils.Ymax(spawnProt1, spawnProt2);
    }

    public boolean isInSpawn(FFAPlayer player1) {
        Location player1Loc = player1.toPlayer().getLocation();
        return player1Loc.getX() <= maxX && player1Loc.getX() >= minX && player1Loc.getZ() <= maxZ && player1Loc.getZ() >= minZ && player1Loc.getY() <= maxY && player1Loc.getY() >= minY;
    }

    public FFAKit getKit(FFAPlayer ffaPlayer) {
        return this.getKitMap().get(ffaPlayer);
    }

    public boolean isInSpawn(FFAPlayer player1, FFAPlayer player2) {
        Location player1Loc = player1.toPlayer().getLocation();
        Location player2Loc = player2.toPlayer().getLocation();
        boolean is1 = player1Loc.getX() <= maxX && player1Loc.getX() >= minX && player1Loc.getZ() <= maxZ && player1Loc.getZ() >= minZ && player1Loc.getY() <= maxY && player1Loc.getY() >= minY;
        boolean is2 = player2Loc.getX() <= maxX && player2Loc.getX() >= minX && player2Loc.getZ() <= maxZ && player2Loc.getZ() >= minZ && player2Loc.getY() <= maxY && player2Loc.getY() >= minY;
        return is1 && is2;
    }

    public void handleRespawn(FFAPlayer ffaplayer, FFAKit ffaKit) {
        if (ffaplayer.toPlayer() == null) {
            return;
        }
        if (this.kitMap.get(ffaplayer) != null) {
            this.kitMap.remove(ffaplayer);
        }
        this.kitMap.put(ffaplayer, ffaKit);
        ffaKit.applyKit(ffaplayer.toPlayer());
    }

    public void handleRemoveItemTask(org.bukkit.entity.Item items) {
        Collection<Item> itemss = new ArrayList<>();
        itemss.add(items);
        handleRemoveItemTask(itemss);
    }

    public void handleRemoveItemTask(Collection<? extends Entity> items) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Tekoki.tekoki(),()-> {
            items.forEach(item-> {
                if (item != null){
                    item.remove();
                }
            });
        }, 10 * 20);
    }

    public boolean hasSelectedKit(FFAPlayer ffaPlayer) {
        return this.getKitMap().get(ffaPlayer) != null;
    }

    public void handleJoin(FFAPlayer ffaplayer, FFAKit kit) {
        if (ffaplayer.toPlayer() == null) {
            return;
        }
        this.kitMap.put(ffaplayer, kit);
        this.ffaPlayers.add(ffaplayer);
        Player player = ffaplayer.toPlayer();
        kit.applyKit(player);
        showAll(player);
        player.sendMessage(Color.GREEN + "You have join the FFA game.");
        player.teleport(spawn.add(0,0.2,0));
        ffaplayer.getSkyDog().setStatus(SkyStatus.FFA);
        this.broadcastMessage(player.getDisplayName() + Color.GREEN + " joined the FFA game!");
    }

    private void showAll(Player player) {
        this.ffaPlayers.forEach(ffaPlayer-> {
            Player player1 = ffaPlayer.toPlayer();
            if (player1 == null) {
                ffaPlayers.remove(ffaPlayer);
                return;
            }
            player.showPlayer(player1);
            player1.showPlayer(player);
        });
    }

    private void hideAll(Player player) {
        this.ffaPlayers.forEach(ffaPlayer-> {
            Player player1 = ffaPlayer.toPlayer();
            if (player1 == null) {
                ffaPlayers.remove(ffaPlayer);
                return;
            }
            player.hidePlayer(player1);
            player1.hidePlayer(player);
        });
    }

    public void handleleave(FFAPlayer ffaplayer, boolean disconnected) {
        if (!disconnected) {
            Player player = ffaplayer.toPlayer();
            hideAll(player);
            ffaplayer.getSkyDog().setStatus(SkyStatus.LOBBY);
            SkyDog.sendtolobby(player);
            SkyDog.setup(player);
            player.sendMessage(Color.GREEN + "You have left FFA game !");
        }
        if (!ffaplayer.getDamageMap().isEmpty() && ffaplayer.isInFight()) {
            Map<String, Double> damages = MathUtils.fromHighToLow(ffaplayer.getDamageMap());
            List<String> names = damages.keySet().stream().collect(Collectors.toList());
            String killerName = names.get(0);
            Player killer = Bukkit.getPlayer(killerName);
            ffaplayer.handleDeath(killer, true);
        }
        this.ffaPlayers.remove(ffaplayer);
        this.kitMap.remove(ffaplayer);
    }

    private void registerKits() {
        ConfigCursor cursor = new ConfigCursor(Tekoki.tekoki().getFFaConfig(), "kits.");
        for (String kit : cursor.getKeys()) {
            cursor.setPath("kits." + kit);
            FFAKitType kitType = FFAKitType.valueOf(kit.toUpperCase());
            FFAKit ffaKit = new FFAKit(kit, kitType);
            ffaKit.setArmor(InventoryUtil.deserializeInventory(cursor.getString("armor")));
            ffaKit.setInventory(InventoryUtil.deserializeInventory(cursor.getString("inv")));
            if (cursor.exists("effects")) {
                ffaKit.getEffects().addAll(InventoryUtil.deserizlizeEffects(cursor.getString("effects")));
            }
            FFAKit.getFfaKits().put(kitType, ffaKit);
        }
        Bukkit.getConsoleSender().sendMessage("Loaded " + FFAKit.getFfaKits().size() + " kits"); // 反正也只有3个
    }

    public void broadcastSound(Sound sound) {
        this.ffaPlayers.forEach(ffaPlayer -> {
            Player player = ffaPlayer.toPlayer();
            if (player == null) {
                ffaPlayers.remove(ffaPlayer);
                return;
            }
            player.playSound(player.getLocation(), sound, 100, 100);
        });
    }

    public void broadcastExcept(String message, List<Player> except) {
        this.ffaPlayers.forEach(ffaPlayer-> {
            Player player = ffaPlayer.toPlayer();
            if (player == null) {
                ffaPlayers.remove(ffaPlayer);
                return;
            }
            if (except.contains(player)) {
                return;
            }
            player.sendMessage(message);
        });
    }

    public void broadcastMessage(String message) {
        this.ffaPlayers.forEach(ffaPlayer -> {
            Player player = ffaPlayer.toPlayer();
            if (player == null) {
                ffaPlayers.remove(ffaPlayer);
                return;
            }
            player.sendMessage(message);
        });
    }

    public FFAPlayer getFFAPlayerFromUUID(UUID uuid) {
        for (FFAPlayer ffaPlayer : ffaPlayers) {
            if (ffaPlayer.getUuid().equals(uuid)) {
                return ffaPlayer;
            }
        }
        return null;
    }
}
