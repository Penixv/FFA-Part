package k.a.g.u.r.a.你mea姐的財布.FFA;

import c.aqua.neeeeee.player.PlayerInfo;
import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.Cooldown;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.tekokitools.GUI.ffa.SelectFFAKitMenu;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class FFAPlayer extends PlayerInfo {

    private Map<String, Integer> hitMap;  //攻击了谁

    private Map<String, Double> damageMap;

    private SkyDog skyDog;
    private int kills;
    private int deaths;

    private int killStreaks;
    private int deathStreaks;

    private Long lastHit;

    private Player target;

    private Cooldown abilityCooldown;

    public FFAPlayer(SkyDog skyDog) {
        super(skyDog.getuuid(), skyDog.getName());
        this.skyDog = skyDog;
        this.lastHit = -1l;
        this.damageMap = new HashMap<>();
        this.hitMap = new HashMap<>();
        this.abilityCooldown = new Cooldown(0);
    }

    public void handleAttack(Player damaged) {
        this.lastHit = System.currentTimeMillis();
        if (this.hitMap.get(damaged.getName()) == null) {
            this.hitMap.put(damaged.getName(), 1);
        } else {
            Integer integer = this.hitMap.get(damaged.getName()) + 1;
            this.hitMap.replace(damaged.getName(), integer);
        }
        setTarget(damaged);
    }

    public void handleDamaged(EntityDamageEvent.DamageCause cause) {
        this.lastHit = System.currentTimeMillis();
    }

    public void handleDamaged(Player attacker, double damage) {
        handleDamaged(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        if (this.damageMap.get(attacker.getName()) != null) {
            double old = this.damageMap.get(attacker.getName());
            this.damageMap.replace(attacker.getName(), old + damage);
        } else {
            this.damageMap.put(attacker.getName(), damage);
        }
    }

    public void handleDeath(EntityDamageEvent.DamageCause cause, boolean left) {
        this.lastHit = -1l;
        double total = 0.0;
        for (double damage : this.damageMap.values()) {
            total += damage;
        }
        final double totalDamage = total;
        this.damageMap.keySet().forEach(name-> {
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                return;
            }
            if (this.toPlayer() == null) {
                String formatedDamage = new DecimalFormat("#.00").format(damageMap.get(name) / totalDamage * 100);
                player.sendMessage(Color.GRAY + "You damaged " + Color.GREEN +  this.getName() + Color.GRAY + " for " + Color.WHITE + formatedDamage + "%");
            } else if (this.toPlayer() != null && this.toPlayer().getKiller() != null && player != this.toPlayer().getKiller()) {
                String formatedDamage = new DecimalFormat("#.00").format(damageMap.get(name) / totalDamage * 100);
                player.sendMessage(Color.GRAY + "You damaged " + Color.GREEN +  this.getName() + Color.GRAY + " for " + Color.WHITE + formatedDamage + "%");
            }
        });
        this.damageMap.clear();
        this.resetKillStreaks();
        this.addDeaths();
        this.addDeathStreaks();
        if (this.toPlayer() != null && !left) {
            this.toPlayer().spigot().respawn();
            this.toPlayer().teleport(Tekoki.tekoki().getFfaHandler().getSpawn());
            this.resetPlayer(this.toPlayer());
            this.resetKit();
            new SelectFFAKitMenu(true).openMenu(this.toPlayer());
            this.toPlayer().sendMessage(Color.RED + "You died!");
        }
    }

    private void resetPlayer(Player player) {
        Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()-> {
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setFallDistance(0f);
            player.setHealth(20.0);
            player.setVelocity(new Vector());
        }, 1l);
    }

    private void resetKit() {
        Tekoki.tekoki().getFfaHandler().getKitMap().remove(this);
        setAbilityCooldown(new Cooldown(0));
    }

    public void handleDeath(Player killer, boolean left) {
        this.handleDeath(EntityDamageEvent.DamageCause.ENTITY_ATTACK, left);
        if (killer != null) {
            killer.sendMessage(Color.RED + "You've just killed " + Color.WHITE +  this.getName() + Color.RED +" !");
        }
        FFAPlayer ffaPlayer = SkyDog.getskydog(killer).getFfaPlayer();
        if (this.toPlayer() != null) {
            this.toPlayer().sendMessage(Color.RED + "You were killed by " + ffaPlayer.getName());
        }
        Tekoki.tekoki().getFfaHandler().broadcastMessage(this.getName() + Color.RED + " was killed by " + Color.WHITE + ffaPlayer.getName() + Color.RED + " !");
        ffaPlayer.handleKill(this.toPlayer());
    }

    public void handleKill(Player killed) {
        if (this.target == killed) {
            setTarget(null);
        }
        if (killed != null) {
            SkyDog skyDog = SkyDog.getskydog(killed);
            if (skyDog != null) {
                skyDog.getFfaPlayer().setLastHit(-1l);
            }
        }
        this.setLastHit(System.currentTimeMillis() - 5 * 1000);
        this.addKills();
        this.addKillStreaks();
        this.resetDeathStreaks();
    }

    public boolean isInFight() {
        if (this.lastHit == -1l) {
            return false;
        }
        return System.currentTimeMillis() - this.lastHit < 25 * 1000l;
    }

    public void resetKillStreaks() {
        this.killStreaks = 0;
    }

    public void resetDeathStreaks() {
        this.deathStreaks = 0;
    }

    public long getTagRemaining() {
        return 25 * 1000l  - (System.currentTimeMillis() - this.lastHit);
    }

    public void addKills() {
        ++this.kills;
    }

    public void addDeaths() {
        ++this.deaths;
    }

    public void addKillStreaks() {
        ++this.killStreaks;
    }

    public void addDeathStreaks() {
        ++this.deathStreaks;
    }
}
