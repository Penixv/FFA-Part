package k.a.g.u.r.a.你mea姐的凝视;

import c.aqua.neeeeee.utils.Cooldown;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.tekokitools.PacketUtil;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileListener implements Listener{
	
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof ThrownPotion && event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player)event.getEntity().getShooter();
            final SkyDog shooterData = SkyDog.getskydog(shooter);
            ThrownPotion thrownPotion = (ThrownPotion) event.getEntity();
            if (shooterData.isInMatch() && shooterData.getAbstractMatch().isFighting()) {
                if (thrownPotion.getItem().getDurability() == 16421 || thrownPotion.getItem().getDurability() == 16453) {
                    shooterData.getAbstractMatch().getMSkyDog(shooter).incrementPotionsThrown();
                } else if (thrownPotion.getItem().getDurability() == 16424
                        || thrownPotion.getItem().getDurability() == 16456
                        || thrownPotion.getItem().getDurability() == 16388
                        || thrownPotion.getItem().getDurability() == 16420
                        || thrownPotion.getItem().getDurability() == 16452
                        || thrownPotion.getItem().getDurability() == 16426
                        || thrownPotion.getItem().getDurability() == 16458) {
                    shooterData.getAbstractMatch().getMSkyDog(shooter).incrementDebuffThrown();
                }
                Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()->{
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!player.canSee(shooter)) {
                            new PacketUtil.Packet(new PacketPlayOutEntityDestroy(event.getEntity().getEntityId())).send(player);
                        }
                    });
                }, 1l);
            }
        } else if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player){
            final Player shooter = (Player)event.getEntity().getShooter();
            final SkyDog shooterData = SkyDog.getskydog(shooter);
            if ((shooterData.isInMatch() && shooterData.getAbstractMatch().isFighting()) || shooterData.isInFFA()) {
                shooterData.setEnderpearlCooldown(new Cooldown(16000L));
                Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()->{
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!player.canSee(shooter)) {
                            new PacketUtil.Packet(new PacketPlayOutEntityDestroy(event.getEntity().getEntityId())).send(player);
                        }
                    });
                }, 1l);
            }
        }
    }
    
    
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(final ProjectileHitEvent event) {
    	if ((event.getEntity().getShooter() instanceof Player)) {
        	
    	final Player shooter = (Player)event.getEntity().getShooter();
        final SkyDog shooterData = SkyDog.getskydog(shooter);
        if (event.getEntity() instanceof Arrow) {
            if (shooterData.isInMatch()) {
                shooterData.getAbstractMatch().getEntities().add((Entity)event.getEntity());
                shooterData.getAbstractMatch().getMSkyDog(shooter).handleHit();
            } 
        }
       }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(final PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            final Player shooter = (Player)event.getPotion().getShooter();
            final SkyDog shooterData = SkyDog.getskydog(shooter);
            ThrownPotion thrownPotion = event.getPotion();
            event.getAffectedEntities().forEach(entity -> {
                if (entity instanceof Player) {
                    if (!shooter.canSee((Player)entity)) {
                        event.setIntensity(entity,0.0d);
                    }
                }
            });
            if (shooterData.isInMatch()) {
                if (shooterData.getAbstractMatch().isFighting()) {
                    if (event.getIntensity(shooter) <= 0.6 && (thrownPotion.getItem().getDurability() == 16421 || thrownPotion.getItem().getDurability() == 16453)) {
                        shooterData.getAbstractMatch().getMSkyDog(shooter).incrementPotionsMissed();
                    }
                    if (thrownPotion.getItem().getDurability() == 16424
                            || thrownPotion.getItem().getDurability() == 16456
                            || thrownPotion.getItem().getDurability() == 16388
                            || thrownPotion.getItem().getDurability() == 16420
                            || thrownPotion.getItem().getDurability() == 16452
                            || thrownPotion.getItem().getDurability() == 16426
                            || thrownPotion.getItem().getDurability() == 16458) {
                        boolean missed = true;
                        for (LivingEntity entity : event.getAffectedEntities()) {
                            if (event.getIntensity(entity) >= 0.7) {
                                missed = false;
                                break;
                            }
                        }
                        if (missed) {
                            shooterData.getAbstractMatch().getMSkyDog(shooter).incrementDebuffMissed();
                        }
                    }
                }
            }
        }
    }

}
