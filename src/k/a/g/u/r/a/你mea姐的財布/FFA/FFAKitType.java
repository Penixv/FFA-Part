package k.a.g.u.r.a.Äãmea½ãµÄØ”²¼.FFA;

import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.Cooldown;
import c.aqua.neeeeee.utils.ItemBuilder;
import k.a.g.u.r.a.Tekoki;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public enum  FFAKitType {

    IRON {
        @Override
        public String getName() {
            return "Iron";
        }

        @Override
        public ItemStack getAbilityItem() {
            return new ItemBuilder(Material.IRON_INGOT).name(Color.WHITE + "BOOST!").build();
        }

        @Override
        public void invoke(FFAPlayer FFAPlayer) {
            if (FFAPlayer.toPlayer() == null) {
                Bukkit.getConsoleSender().sendMessage(FFAPlayer.getName() + " null player in ffa");
                return;
            }
            Player player = FFAPlayer.toPlayer();
            if (!FFAPlayer.getAbilityCooldown().hasExpired()) {
                player.sendMessage(Color.RED + "Your ability isn't ready yet!");
                return;
            }
            Collection<PotionEffect> old = player.getActivePotionEffects();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 1));
            player.sendMessage(Color.GREEN + "Your ability is now active !");
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 100, 100);
            FFAPlayer.setAbilityCooldown(new Cooldown(20 * 1000l));
            Bukkit.getScheduler().runTaskLaterAsynchronously(Tekoki.tekoki(),()-> {
                if (player != null) {
                    old.forEach(player::addPotionEffect);
                }
            }, 20*11);
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(Material.IRON_CHESTPLATE).name(Color.WHITE + Color.BOLD + "Iron").build();
        }
    },
    DIAMOND {
        @Override
        public String getName() {
            return "Diamond";
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(Material.DIAMOND_CHESTPLATE).name(Color.AQUA + Color.BOLD + "Diamond").build();
        }
    },
    GOLD {

        @Override
        public String getName() {
            return "Gold";
        }

        @Override
        public void invoke(FFAPlayer FFAPlayer) {
            if (FFAPlayer.toPlayer() == null) {
                Bukkit.getConsoleSender().sendMessage(FFAPlayer.getName() + " null player in ffa");
                return;
            }
            Player player = FFAPlayer.toPlayer();
            if (!FFAPlayer.getAbilityCooldown().hasExpired()) {
                player.sendMessage(Color.RED + "Your ability isn't ready yet!");
                return;
            }
            Collection<PotionEffect> old = player.getActivePotionEffects();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20* 10, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 *5, 1));
            player.sendMessage(Color.GREEN + "Your ability is now active !");
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 100, 100);
            FFAPlayer.setAbilityCooldown(new Cooldown(15 * 1000l));
            Bukkit.getScheduler().runTaskLaterAsynchronously(Tekoki.tekoki(),()-> {
                if (player != null) {
                    old.forEach(player::addPotionEffect);
                }
            }, 20*11);
        }

        @Override
        public ItemStack getAbilityItem() {
            return new ItemBuilder(Material.SUGAR).name(Color.GOLD + "BOOST!").build();
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(Material.GOLD_CHESTPLATE).name(Color.GOLD + Color.BOLD + "Gold").build();
        }
    };

    public void invoke(FFAPlayer player) {
    }

    public String getName() {
        return null;
    }

    public ItemStack getIcon() {
        return null;
    }

    public ItemStack getAbilityItem() {
        return null;
    }
}
