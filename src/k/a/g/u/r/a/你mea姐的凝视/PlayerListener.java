package k.a.g.u.r.a.你mea姐的凝视;

import c.aqua.Aqua;
import c.aqua.neeeeee.custom.Tag;
import c.aqua.neeeeee.player.AquaPlayer;
import c.aqua.neeeeee.utils.BukkitUtil;
import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.Cooldown;
import c.aqua.neeeeee.utils.Time;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.tekokitools.GUI.LeaderBoardMenu;
import k.a.g.u.r.a.tekokitools.GUI.PracticeSettingMenu;
import k.a.g.u.r.a.tekokitools.GUI.ffa.SelectFFAKitMenu;
import k.a.g.u.r.a.tekokitools.GUI.kiteditor.SelectLadderKitMenu;
import k.a.g.u.r.a.tekokitools.GUI.match.SelectLadderQueueMenu;
import k.a.g.u.r.a.tekokitools.GUI.party.OtherPartiesMenu;
import k.a.g.u.r.a.tekokitools.GUI.party.PartyEventSelectEventMenu;
import k.a.g.u.r.a.tekokitools.PacketUtil;
import k.a.g.u.r.a.tekokitools.PracticeSetting;
import k.a.g.u.r.a.tekokitools.RunTask;
import k.a.g.u.r.a.你mea姐的財布.Arena.subs.StandaloneArena;
import k.a.g.u.r.a.你mea姐的財布.Event.Event;
import k.a.g.u.r.a.你mea姐的財布.Event.EventState;
import k.a.g.u.r.a.你mea姐的財布.Event.implement.sumo.SumoEvent;
import k.a.g.u.r.a.你mea姐的財布.Event.player.EventPlayerState;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAKit;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAPlayer;
import k.a.g.u.r.a.你mea姐的財布.Kits.Kit.Kit;
import k.a.g.u.r.a.你mea姐的財布.Kits.Kit.NamedKit;
import k.a.g.u.r.a.你mea姐的財布.Kits.Ladder;
import k.a.g.u.r.a.你mea姐的財布.Match.*;
import k.a.g.u.r.a.你mea姐的財布.Match.impl.SoloMatch;
import k.a.g.u.r.a.你mea姐的財布.Match.impl.TeamMatch;
import k.a.g.u.r.a.你mea姐的財布.Match.queue.Queue;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerListener implements Listener{
	
	@EventHandler
	public void onLogin(final AsyncPlayerPreLoginEvent event) {

        if (!Tekoki.tekoki().isLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Server isn't loaded yet! Please wait a moment!");
            return;
        }

        if(SkyDog.getByUUID(event.getUniqueId()) != null) {
            SkyDog.getAll().remove(SkyDog.getByUUID(event.getUniqueId()));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.RED + "You've already logged. Please wait at least 5 seconds and login again if you believe this is in error.");
            return;
        }

        final SkyDog skyDog = new SkyDog(event.getUniqueId(), event.getName());
        skyDog.load();

        if (!skyDog.isLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Failed to load your profile. Try again later.");
            return;
        }

    }

    @EventHandler
    public void PlayerJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        SkyDog skyDog = SkyDog.getskydog(p);
        AquaPlayer aquaPlayer = AquaPlayer.getByUuid(p.getUniqueId());
        SkyDog.getskydog(p).setPlayer(p);
        SkyDog.sendtolobby(p);
        SkyDog.setup(p);
        Ladder.getLadders().forEach(ladder -> {
            if (skyDog.canGetTag(ladder)) {
                Tag tag = Tag.getTag(ladder.getName());
                if (tag != null) {
                    aquaPlayer.getServerTags().add(tag);
                }
            }
        });
        switch ((String) Aqua.getSetting(p, PracticeSetting.TIME)) {
            case "SUNSET":
                PacketUtil.sendTimerPacket(p, PacketUtil.Period.SUNSET);
                break;
            case "NIGHT":
                PacketUtil.sendTimerPacket(p, PacketUtil.Period.NIGHT);
                break;
            case "DAY":
                PacketUtil.sendTimerPacket(p, PacketUtil.Period.DAY);
                break;
        }
        e.setJoinMessage(null);
    }

    @EventHandler
    public void Interact(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        final SkyDog skyDog = SkyDog.getskydog(p);
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.hasItem()) {
            if (skyDog.getStatus() == SkyStatus.LOBBY) {
                if (skyDog.getParty() == null) {
                    switch (event.getItem().getType()) {
                        case REDSTONE_TORCH_ON:
                            new SelectFFAKitMenu(false).openMenu(p);
                            break;
                    case LEVER:
                        new PracticeSettingMenu().openMenu(p);
                        event.setCancelled(true);
                        break;
                    case EMERALD:
                        if (skyDog.getLeaderboardCooldown().hasExpired()){
                            new LeaderBoardMenu().openMenu(p);
                            skyDog.setLeaderboardCooldown(new Cooldown(3000L));
                        } else {
                            p.sendMessage(Color.RED + "You can't view leaderboard for another " + skyDog.getLeaderboardCooldown().getTimeLeft() + "s");
                        }
                        event.setCancelled(true);
                        break;
                    case IRON_SWORD:
                        if (skyDog.getTournament() != null) {
                            p.sendMessage(Color.RED + "You are not allowed to do this while in a tournament!");
                            return;
                        }
                        new SelectLadderQueueMenu(false).openMenu(p);
                        event.setCancelled(true);
                        break;
                    case DIAMOND_SWORD:
                        if (skyDog.getTournament() != null) {
                            p.sendMessage(Color.RED + "You are not allowed to do this while in a tournament!");
                            return;
                        }
                        new SelectLadderQueueMenu(true).openMenu(p);
                        event.setCancelled(true);
                        break;
                    case BOOK:
                        new SelectLadderKitMenu().openMenu(p);
                        event.setCancelled(true);
                        break;
                    case NAME_TAG:
                        if (skyDog.getTournament() != null) {
                            p.sendMessage(Color.RED + "You are not allowed to do this while in a tournament!");
                            return;
                        }
                        p.performCommand("party create");
                        event.setCancelled(true);
                        break;
                    case APPLE:
                        p.performCommand("stopspec");
                        event.setCancelled(true);
                        break;
                    default:
                        break;
                    }
                } else {
                    switch (event.getItem().getType()) {
                    case BOOK:
                        new SelectLadderKitMenu().openMenu(p);
                        event.setCancelled(true);
                        break;
                    case ENCHANTED_BOOK:
                        if (skyDog.getTournament() != null) {
                            p.sendMessage(Color.RED + "You are not allowed to do this while in a tournament!");
                            return;
                        }
                        new OtherPartiesMenu().openMenu(p);
                        event.setCancelled(true);
                        break;
                    case PAPER:
                        p.performCommand("party info");
                        event.setCancelled(true);
                        break;
                    case REDSTONE_TORCH_ON:
                        p.performCommand("party disband");
                        event.setCancelled(true);
                        break;
                    case REDSTONE:
                        p.performCommand("party leave");
                        event.setCancelled(true);
                        break;
                    case DIAMOND_SWORD:
                        if (skyDog.getTournament() != null) {
                            p.sendMessage(Color.RED + "You are not allowed to do this while in a tournament!");
                            return;
                        }
                        new PartyEventSelectEventMenu().openMenu(p);
                        event.setCancelled(true);
                        break;
                    default:
                        break;
                    }
                }
            } else if (skyDog.getStatus() == SkyStatus.QUEUE) {
                switch (event.getItem().getType()) {
                    case REDSTONE:
                        final Queue queue = Queue.getByUuid(skyDog.getQueuePlayer().getQueueUuid());
                        if (queue != null) {
                            queue.removePlayer(skyDog.getQueuePlayer());
                        }
                        event.setCancelled(true);
                        break;
                    default:
                        break;
                }
            } else if (skyDog.isSpectating()) {
                switch (event.getItem().getType()) {
                    case APPLE:
                        if (event.getItem().getItemMeta().getDisplayName().contains("Leave")) {
                            p.performCommand("stopspec");
                            event.setCancelled(true);
                        }
                        break;
                }
            } else if (skyDog.isInEvent()) {
                switch (event.getItem().getType()) {
                    case APPLE:
                        if (event.getItem().getItemMeta().getDisplayName().contains("Leave")) {
                            p.performCommand("event leave");
                            event.setCancelled(true);
                        }
                        break;
                }
            } else if (skyDog.isInFFA()) {
                FFAPlayer ffaPlayer = skyDog.getFfaPlayer();
                FFAKit kit = Tekoki.tekoki().getFfaHandler().getKitMap().get(ffaPlayer);
                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
                    if (kit.getType().getAbilityItem().getType() == event.getItem().getType()){
                        kit.getType().invoke(ffaPlayer);
                    }
                }
            } else if (skyDog.isInMatch()) {
                if (event.hasItem() && event.getItem().hasItemMeta()) {
                    if (event.getItem().getItemMeta().hasDisplayName()) {
                        if (event.getItem().equals(Kit.DEFAULT_KIT)) {
                            event.setCancelled(true);
                            final Kit kit = skyDog.getAbstractMatch().getLadder().getDefaultKit();
                            p.getInventory().setArmorContents(kit.getArmor());
                            p.getInventory().setContents(kit.getContents());
                            skyDog.getAbstractMatch().getLadder().getEffects().forEach(effect -> {
                                p.addPotionEffect(effect);
                            });
                            p.updateInventory();
                            p.sendMessage(Color.GRAY + "You have been given the" + Color.GREEN + " Default "
                                    + Color.GRAY + "kit.");
                            return;
                        }
                        final String displayName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if (displayName.startsWith("Kit: ")) {
                            final String kitName = displayName.replace("Kit: ", "");
                            for (final NamedKit kit2 : skyDog.getKits(skyDog.getAbstractMatch().getLadder())) {
                                if (kit2 != null && ChatColor.stripColor(kit2.getName()).equals(kitName)) {
                                    event.setCancelled(true);
                                    p.getInventory().setArmorContents(kit2.getArmor());
                                    p.getInventory().setContents(kit2.getContents());
                                    skyDog.getAbstractMatch().getLadder().getEffects().forEach(effect -> {
                                        p.addPotionEffect(effect);
                                    });
                                    p.updateInventory();
                                    p.sendMessage(Color.GRAY + "You have been given the " + Color.GREEN + kit2.getName()
                                            + Color.GRAY + " kit.");
                                    return;
                                }
                            }
                        }
                    }
                }
                if (event.hasItem() && event.getItem().getType().equals(Material.MUSHROOM_SOUP)) {
                    if(p.getHealth() <= 19.0D && !p.isDead()) {
                        if(p.getHealth() < 20.0D || p.getFoodLevel() < 20) {
                            p.getItemInHand().setType(Material.BOWL);
                        }
                        p.setHealth(p.getHealth() + 7.0D > 20.0D ? 20.0D : p.getHealth() +
                                7.0D);
                        p.setFoodLevel(p.getFoodLevel() + 2 > 20 ? 20 : p.getFoodLevel() + 2);
                        p.setSaturation(12.8F);
                        p.updateInventory();
                    }
                }
                if (event.hasItem() && (event.getItem().getType() == Material.ENDER_PEARL
                        || (event.getItem().getType() == Material.POTION && event.getItem().getDurability() >= 16000))
                        && skyDog.isInMatch() && skyDog.getAbstractMatch().isStarting()) {
                    event.setCancelled(true);
                    p.updateInventory();
                    return;
                }
                if (event.hasItem() && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() &&  event.getItem().getItemMeta().getDisplayName().contains("Leave")) {
                    p.performCommand("stopspec");
                    event.setCancelled(true);
                    return;
                }
                if (event.hasItem() && event.getItem().getType().equals(Material.ENDER_PEARL)) {
                    if (!skyDog.isInMatch() || (skyDog.isInMatch() && !skyDog.getAbstractMatch().isFighting())) {
                        event.setCancelled(true);
                        return;
                    }
                    if (skyDog.getAbstractMatch().isStarting()) {
                        event.setCancelled(true);
                        return;
                    }
                    if (!skyDog.getEnderpearlCooldown().hasExpired()) {
                        final String time = Time.millisToSeconds(skyDog.getEnderpearlCooldown().getRemaining());
                        final String context = "second" + (time.equalsIgnoreCase("1.0") ? "s" : "");
                        event.setCancelled(true);
                        p.sendMessage(Color.AQUA + "You are on pearl cooldown for " + Color.WHITE + time + " " + context + Color.AQUA + ".");
                        p.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void OnClick(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final Player player = (Player) event.getWhoClicked();
            final SkyDog skyDog = SkyDog.getskydog(player);
            if (event.getClickedInventory() != null && event.getClickedInventory() instanceof CraftingInventory
                    && player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
                return;
            }
            if ((skyDog.getStatus() != SkyStatus.FIGHT && skyDog.getStatus() != SkyStatus.FFA) && player.getGameMode() == GameMode.SURVIVAL) {
                final Inventory clicked = event.getClickedInventory();
                if (skyDog.getKitEditor().isActive()) {
                    if (clicked == null) {
                        event.setCancelled(true);
                        event.setCursor(null);
                        player.updateInventory();
                    } else if (clicked.equals(player.getOpenInventory().getTopInventory())
                            && ((event.getCursor().getType() != Material.AIR
                                    && event.getCurrentItem().getType() == Material.AIR)
                                    || (event.getCursor().getType() != Material.AIR
                                            && event.getCurrentItem().getType() != Material.AIR))) {
                        event.setCancelled(true);
                        event.setCursor(null);
                        player.updateInventory();
                    }
                } else if (clicked != null && clicked.equals(player.getInventory())) {
                    event.setCancelled(true);
                }
            }
            if (skyDog.isInFFA() && Tekoki.tekoki().getFfaHandler().hasSelectedKit(skyDog.getFfaPlayer())) {
                if (event.getCurrentItem() != null) {
                    if (event.getCurrentItem().getType().toString().contains("_BOOTS")
                            || event.getCurrentItem().getType().toString().contains("_CHESTPLATE")
                            || event.getCurrentItem().getType().toString().contains("_HELMET")
                            || event.getCurrentItem().getType().toString().contains("_LEGGINGS")
                            || event.getCurrentItem().getType().toString().contains("_SWORD")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void Hunger(final FoodLevelChangeEvent e) {
        final Player p = (Player) e.getEntity();
        final SkyDog skyDog = SkyDog.getskydog(p);
        if (skyDog.getStatus() != SkyStatus.FIGHT || skyDog.getStatus() != SkyStatus.FFA) {
            e.setCancelled(true);
        } else if (skyDog.isInMatch()){
            if (skyDog.getAbstractMatch() != null){
                if(skyDog.getAbstractMatch().getLadder().isSumo()){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Player attacker = BukkitUtil.getDamager(event);
        if (attacker != null && event.getEntity() instanceof Player) {
            final Player damaged = (Player)event.getEntity();
            final SkyDog damagedData = SkyDog.getskydog(damaged);
            final SkyDog attackerData = SkyDog.getskydog(attacker);
            if (attackerData.isSpectating() || damagedData.isSpectating()) {
                event.setCancelled(true);
                return;
            }
            if (damagedData.isInMatch() && attackerData.isInMatch()) {
                final abstractMatch abstractMatch = attackerData.getAbstractMatch();
                if (!damagedData.getAbstractMatch().getUUID().equals(attackerData.getAbstractMatch().getUUID())) {
                    event.setCancelled(true);
                    return;
                }
                if (!abstractMatch.getMSkyDog(damaged).isAlive() || !abstractMatch.getMSkyDog(attacker).isAlive()) {
                    event.setCancelled(true);
                    return;
                }
                damaged.setFallDistance(0.0f);
                if (abstractMatch.getLadder().getName().contains("Cake")) {
                    if (event.getFinalDamage() > damaged.getHealth()) {
                        healAndResetCakePlayer(damaged, true, attacker);
                        event.setCancelled(true);
                    }
                }
                if (abstractMatch.isSoloMatch()) {
                    attackerData.getAbstractMatch().getMSkyDog(attacker).handleHit();
                    damagedData.getAbstractMatch().getMSkyDog(damaged).resetCombo();
                    if (event.getDamager() instanceof Arrow) {
                        final double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;
                        attacker.sendMessage(Color.formatArrowHitMessage(damaged.getName(), health));
                    }
                } else if (abstractMatch.isTeamMatch()) {
                    final MatchTeam attackerTeam = abstractMatch.getTeam(attacker);
                    final MatchTeam damagedTeam = abstractMatch.getTeam(damaged);
                    if (attackerTeam == null || damagedTeam == null) {
                        event.setCancelled(true);
                    }
                    else if (attackerTeam.equals(damagedTeam)) {
                        event.setCancelled(true);
                    }
                    else {
                        attackerData.getAbstractMatch().getMSkyDog(attacker).handleHit();
                        damagedData.getAbstractMatch().getMSkyDog(damaged).resetCombo();
                        if (event.getDamager() instanceof Arrow) {
                            double health = PacketUtil.getNMSPlayer(damaged).getAbsorptionHearts() + damaged.getHealth() - event.getFinalDamage();
                            if (health <= 0.0) {
                                health = 0.0;
                            }
                            attacker.sendMessage(Color.formatArrowHitMessage(damaged.getName(), Double.parseDouble(new DecimalFormat("#.00").format(health)) ));
                        }
                    }
                } else if (abstractMatch.isFFAMatch()) {
                    attackerData.getAbstractMatch().getMSkyDog(attacker).handleHit();
                    attackerData.getAbstractMatch().getMSkyDog(attacker).setPlayerTarget(damagedData.getAbstractMatch().getMSkyDog(damaged));
                    damagedData.getAbstractMatch().getMSkyDog(damaged).resetCombo();
                    if (event.getDamager() instanceof Arrow) {
                        final double health2 = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0;
                        attacker.sendMessage(Color.formatArrowHitMessage(damaged.getName(), health2));
                    }
                }
            } else if (damagedData.isInEvent() && attackerData.isInEvent()) {
                final Event meaEvent = damagedData.getEvent();
                if (!meaEvent.isFighting() || !meaEvent.isFighting(damaged.getUniqueId()) || !meaEvent.isFighting(attacker.getUniqueId())) {
                    event.setCancelled(true);
                }
            } else if (damagedData.isInFFA() && attackerData.isInFFA()) {
                if (Tekoki.tekoki().getFfaHandler().isInSpawn(attackerData.getFfaPlayer(), damagedData.getFfaPlayer())) {
                    event.setCancelled(true);
                    return;
                }
                attackerData.getFfaPlayer().handleAttack(damaged);
                damagedData.getFfaPlayer().handleDamaged(attacker, event.getFinalDamage());
            }
        }
    }

    private void healAndResetCakePlayer(Player player, boolean killed, Player killer) {
	    SkyDog skyDog = SkyDog.getskydog(player);
	    if (!skyDog.isInMatch() && skyDog.getAbstractMatch().isEnding()) {
	        return;
        }
	    player.setFallDistance(0.0f);
	    player.setHealth(20.0);
	    player.setFoodLevel(20);
	    player.setVelocity(new Vector());
	    player.setFireTicks(0);
	    player.getActivePotionEffects().stream().map(effect-> effect.getType()).collect(Collectors.toList()).forEach(player::removePotionEffect);
	    Kit kit = skyDog.getAbstractMatch().getLadder().getDefaultKit();
        player.getInventory().clear();
        if (killed) {
            killer.sendMessage(Color.GREEN + "You killed " + player.getName() + "!");
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Color.RED + "You were killed by " + killer.getName() + "!");
            Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()-> {
                if (player != null){
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.getInventory().setContents(kit.getContents());
                    player.updateInventory();
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(skyDog.getAbstractMatch().getSpawnMap().get(player));
                }
            }, 40l);
        } else {
            player.getInventory().setArmorContents(kit.getArmor());
            player.getInventory().setContents(kit.getContents());
            player.updateInventory();
            player.sendMessage(Color.RED + "You fell from a high place!");
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
	    Player player = event.getPlayer();
	    SkyDog skyDog = SkyDog.getskydog(event.getPlayer());
	    if ((skyDog.isInLobby() || skyDog.isInQueue()) && player.getLocation().getY() < -5) {
	        SkyDog.sendtolobby(player);
	        return;
        }
	    if (skyDog.isInMatch()){
	        abstractMatch abstractMatch = skyDog.getAbstractMatch();
	        if (abstractMatch.getLadder().isSumo()){
                Location location = abstractMatch.getArena().getSpawn1();
                MSkyDog mSkyDog = abstractMatch.getMSkyDog(event.getPlayer());
                if (mSkyDog.isAlive() && abstractMatch.isFighting()){
                    if (location.getY() - event.getPlayer().getLocation().getY() >= 1.8){
                        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
                        player.damage(100000.0, player.getKiller());
                    }
                }
            } else if (abstractMatch.getLadder().getName().contains("Cake")) {
	            if (abstractMatch.isEnding()) {
	                return;
                }
	            if (player.getLocation().getY() <= 0) {
	                player.setFallDistance(0f);
	                player.teleport(abstractMatch.getSpawnMap().get(player));
	                healAndResetCakePlayer(player, false, null);
                }
            }
        } else if (skyDog.isInEvent()) {
	        Event matchEvent = skyDog.getEvent();
	        if (matchEvent.isSumo()) {
                Location location = ((SumoEvent)matchEvent).getSumoArena().getSpawn1();
                if (matchEvent.getEventPlayer(event.getPlayer().getUniqueId()).getState() == EventPlayerState.FIGHTING) {
                    if (location.getY() - event.getPlayer().getLocation().getY() >= 1.8){
                        matchEvent.handleDeath(event.getPlayer());
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            final SkyDog skydog = SkyDog.getskydog(player);
            if (skydog.isInMatch()) {
                if (skydog.getAbstractMatch().isStarting() || skydog.getAbstractMatch().isEnding()){
                    event.setCancelled(true);
                    return;
                }
                if (!skydog.getAbstractMatch().getMSkyDog(player).isAlive()) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                	event.setDamage(1000.0);
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (event.getFinalDamage() > player.getHealth() && skydog.getAbstractMatch().getLadder().getName().contains("Cake")) {
                        healAndResetCakePlayer(player, false, null);
                        return;
                    }
                }
                if (skydog.getAbstractMatch().isTeamMatch() && !skydog.getAbstractMatch().getMSkyDog(player).isAlive()) {
                    event.setCancelled(true);
                    return;
                }
                if (skydog.getAbstractMatch().getLadder().isSumo() && skydog.getAbstractMatch().isStarting()) {
                	event.setCancelled(true);
                    return;
                }
                if (skydog.getAbstractMatch().getLadder().isSumo() || skydog.getAbstractMatch().getLadder().isSpleef()) {
                    player.updateInventory();
                }
            } else if (skydog.isInEvent()) {
                if (skydog.getEvent().getState() == EventState.WAITING) {
                    event.setCancelled(true);
                    return;
                }
                if (skydog.getEvent().getState() == EventState.ROUND_STARTING) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    event.setDamage(1000.0);
                    return;
                }
                if (skydog.getEvent().isSumo()) {
                    if (!skydog.getEvent().isFighting() || !skydog.getEvent().isFighting(player.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }
                    event.setDamage(0.0);
                    player.setHealth(20.0);
                    player.updateInventory();
                }
            } else if (skydog.isInFFA()) {
                FFAPlayer ffaPlayer = skydog.getFfaPlayer();
                if (Tekoki.tekoki().getFfaHandler().isInSpawn(ffaPlayer)) {
                    event.setCancelled(true);
                    return;
                }
                ffaPlayer.handleDamaged(event.getCause());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
        final SkyDog skydog = SkyDog.getskydog(event.getPlayer());
        if (skydog != null) {
            if (skydog.getParty() != null) {
                if (skydog.getParty().isLeader(event.getPlayer().getUniqueId())) {
                    skydog.getParty().disband();
                } else {
                    skydog.getParty().leave(event.getPlayer(), false , true);
                }
            }
            if (skydog.isInMatch()) {
                if (!skydog.getAbstractMatch().isEnding()) {
                    if (skydog.getAbstractMatch().getMSkyDog(event.getPlayer()).isAlive()) {
                        skydog.getAbstractMatch().handleDeath(event.getPlayer(), null, DeathReason.DISCONNECTED);
                    } else {
                        skydog.getAbstractMatch().getMSkyDog(event.getPlayer()).setDisconnected(true);
                    }
                }
            } else if (skydog.isSpectating()) {
                skydog.getAbstractMatch().removeSpectator(event.getPlayer(), true);
            } else if (skydog.isInQueue()) {
                final Queue queue = Queue.getByUuid(skydog.getQueuePlayer().getQueueUuid());
                if (queue == null) {
                    return;
                }
                queue.removePlayer(skydog.getQueuePlayer());
            } else if (skydog.isInEvent()) {
                skydog.getEvent().handleLeave(event.getPlayer());
            } else if (skydog.isInFFA()) {
                Tekoki.tekoki().getFfaHandler().handleleave(skydog.getFfaPlayer(), true);
            }
            Tekoki.tekoki().getTournamentManager().leaveTournament(event.getPlayer(), true, false);
            RunTask.runAsync(skydog::save);
        }
        SkyDog.getAll().remove(skydog);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final SkyDog skydog = SkyDog.getskydog(event.getPlayer());
        if (skydog.isInMatch()) {
            if (event.getItemDrop().getItemStack().getType() == Material.BOOK
                    || event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK
                    || event.getItemDrop().getItemStack().getType() == Material.MUSHROOM_SOUP) {
                event.setCancelled(true);
                return;
            }
            if (skydog.getAbstractMatch() != null) {
                if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                    event.getItemDrop().remove();
                    return;
                }
                if (event.getItemDrop().getItemStack().getType() == Material.BOWL) {
                    RunTask.runLater(()-> event.getItemDrop().remove(),40l);
                    return;
                }
                Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()->{
                    abstractMatch match = skydog.getAbstractMatch();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (!match.players().contains(player) && !match.getSpectators().contains(player)) {
                            new PacketUtil.Packet(new PacketPlayOutEntityDestroy(event.getItemDrop().getEntityId())).send(player);
                        }
                    });
                },1l);
                skydog.getAbstractMatch().getEntities().add((Entity) event.getItemDrop());
            }
        } else if (skydog.isInFFA()){
            Tekoki.tekoki().getFfaHandler().handleRemoveItemTask(event.getItemDrop());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final SkyDog skyDog = SkyDog.getskydog(player);
        if (skyDog.getAquaPlayer().getStaff().isModed()) {
            event.setCancelled(true);
            return;
        }
        if (skyDog.isInMatch()) {
            final abstractMatch abstractMatch = skyDog.getAbstractMatch();
            if (abstractMatch.getLadder().isBuild()) {
                if (abstractMatch.getLadder().isSpleef()) {
                    if (event.getBlock().getType() == Material.SNOW_BLOCK
                            || event.getBlock().getType() == Material.SNOW) {
                        abstractMatch.getChangedBlocks().add(event.getBlock().getState());
                        event.getBlock().setType(Material.AIR);
                        event.getPlayer().getInventory()
                                .addItem(new ItemStack[]{new ItemStack(Material.SNOW_BALL, 4)});
                        event.getPlayer().updateInventory();
                    } else {
                        event.setCancelled(true);
                    }
                } else if (abstractMatch.getLadder().getName().contains("Cake") && event.getBlock().getType() == Material.CAKE_BLOCK) {
                    event.setCancelled(true);
                    switch (abstractMatch.getType()) {
                        case TEAM:
                            TeamMatch teamMatch = (TeamMatch) abstractMatch;
                            if (teamMatch.getSpawnMap().get(teamMatch.getOpponentTeam(player).getPlayers().get(0)).distance(event.getBlock().getLocation()) <
                                    ((StandaloneArena)abstractMatch.getArena()).getCakeRange()){
                                teamMatch.getOpponentTeam(player).getTeamPlayers().forEach(mSkyDog -> mSkyDog.setAlive(false));
                                teamMatch.handleEnd();
                                abstractMatch.broadcastSound(Sound.EXPLODE);
                            }
                            break;
                        case SOLO:
                            SoloMatch soloMatch = (SoloMatch) abstractMatch;
                            if (soloMatch.getSpawnMap().get(soloMatch.getOpponentPlayer(player)).distance(event.getBlock().getLocation()) <
                                    ((StandaloneArena)abstractMatch.getArena()).getCakeRange()) {
                                soloMatch.handleDeath(abstractMatch.getOpponentPlayer(player), player, DeathReason.CAKE);
                                abstractMatch.broadcastSound(Sound.EXPLODE);
                            }
                            break;
                    }
                } else if (!abstractMatch.getPlacedBlocks().remove(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
            final Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        final SkyDog skyDog = SkyDog.getskydog(event.getPlayer());
        final FFAPlayer ffaPlayer = skyDog.getFfaPlayer();
        if (skyDog.isInMatch()) {
            if (!skyDog.getAbstractMatch().getMSkyDog(event.getPlayer()).isAlive()) {
                event.setCancelled(true);
                return;
            }
            if (skyDog.getAbstractMatch().isEnding()) {
                event.setCancelled(true);
                return;
            }
            if (event.getItem().getItemStack().getType() == Material.BOWL) {
                event.setCancelled(true);
                return;
            }
            if (!skyDog.getAbstractMatch().getEntities().contains(event.getItem())) {
                event.setCancelled(true);
                return;
            }
            Iterator<Entity> entityIterator = skyDog.getAbstractMatch().getEntities().iterator();
            while (entityIterator.hasNext()) {
                final Entity entity = entityIterator.next();
                if (entity instanceof Item && entity.equals(event.getItem())) {
                    entityIterator.remove();
                    return;
                }
            }
        } else if (skyDog.isSpectating()) {
            event.setCancelled(true);
        } else if (skyDog.isInFFA()) {
            if (!Tekoki.tekoki().getFfaHandler().hasSelectedKit(ffaPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
	    if (event.getEntity() instanceof Player) {
	        Player player = (Player) event.getEntity();
	        SkyDog skyDog = SkyDog.getskydog(player);
	        if (skyDog.isInMatch()) {
	            if (!skyDog.getAbstractMatch().getLadder().isRegeneration() && (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.EATING && event.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC && event.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN)) {
	                event.setCancelled(true);
                }
            }
        }
    }

    public void sendFakeItem(List<Item> items, Player player) {
        List<ItemStack> toDrop = items.stream().filter(drop -> drop.getItemStack().getType().toString().contains("_BOOTS")
                || drop.getItemStack().getType().toString().contains("_CHESTPLATE")
                || drop.getItemStack().getType().toString().contains("_HELMET")
                || drop.getItemStack().getType().toString().contains("_LEGGINGS")
                || drop.getItemStack().getType().toString().contains("_SWORD")
        ).map(item -> item.getItemStack()).collect(Collectors.toList());
        List<Item> entities = new ArrayList<>();
        toDrop.forEach(drop -> {
            entities.add(player.getWorld().dropItemNaturally(player.getLocation(), drop, player));
        });
        abstractMatch match = SkyDog.getskydog(player).getAbstractMatch();
        entities.forEach(item ->  {
            Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()->{
                Bukkit.getOnlinePlayers().forEach(player1-> {
                    if (player1 != player && player1 != player.getKiller()){
                        new PacketUtil.Packet(new PacketPlayOutEntityDestroy(item.getEntityId())).send(player1);
                    }
                });
            },1l);
        });
        RunTask.runLater(()->{
            Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()->{
                entities.forEach(item ->  {
                    match.players().forEach(fighter-> new PacketUtil.Packet(new PacketPlayOutEntityDestroy(item.getEntityId())).send(fighter));
                    match.getSpectators().forEach(spec-> new PacketUtil.Packet(new PacketPlayOutEntityDestroy(item.getEntityId())).send(spec));
                    item.remove();
                });
            },1l);
        },100l);
    }

    private void breakItems(List<Item> tobreak, List<Player> receive) {
        Bukkit.getScheduler().runTaskLater(Tekoki.tekoki(),()->{
            tobreak.forEach(item -> {
                new PacketUtil.Packet(new PacketPlayOutEntityDestroy(item.getEntityId())).send(receive);
            });
        },1l);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        event.setDeathMessage(null);
        final SkyDog skydog = SkyDog.getskydog(event.getEntity());
        final Player player = event.getEntity();
        if (skydog.isInMatch()) {
            List<Item> items = new ArrayList<>();
            event.getDrops().forEach(itemStack -> {
                items.add(event.getEntity().getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack,event.getEntity()));
            });
            skydog.getAbstractMatch().addEntity(items);
            event.getDrops().clear();
            if (skydog.getAbstractMatch().getType() == MatchType.SOLO) {
                sendFakeItem(items, player);
            } else {
                List<Player> cantSee = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(online-> {
                    if (!skydog.getAbstractMatch().players().contains(online) && !skydog.getAbstractMatch().players().contains(online)) {
                        cantSee.add(online);
                    }
                });
                breakItems(items, cantSee);
            }
            if (player.getKiller() == null) {
                switch (player.getLastDamageCause().getCause()) {
                    case LAVA:
                        skydog.getAbstractMatch().handleDeath(event.getEntity(), null, DeathReason.LAVA);
                        break;
                    case VOID:
                        skydog.getAbstractMatch().handleDeath(event.getEntity(), null, DeathReason.VOID);
                        break;
                    case FALL:
                        skydog.getAbstractMatch().handleDeath(event.getEntity(), null, DeathReason.FALL);
                        break;
                    default:
                        if (skydog.getAbstractMatch().getLadder().isSumo()) {
                            skydog.getAbstractMatch().handleDeath(event.getEntity(), null, DeathReason.SUMO);
                        } else {
                            skydog.getAbstractMatch().handleDeath(event.getEntity(), null, DeathReason.OTHER);
                        }
                        break;
                }
            } else {
                if (skydog.getAbstractMatch().getLadder().isSumo()) {
                    skydog.getAbstractMatch().handleDeath(event.getEntity(), event.getEntity().getKiller(), DeathReason.SUMO);
                } else {
                    skydog.getAbstractMatch().handleDeath(event.getEntity(), event.getEntity().getKiller(), DeathReason.PLAYER);
                }
            }
        } else if (skydog.isInFFA()) {
            FFAPlayer ffaPlayer = skydog.getFfaPlayer();
            List<Item> items = new ArrayList<>();
            event.getDrops().forEach(itemStack -> {
                items.add(event.getEntity().getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack,event.getEntity()));
            });
            Tekoki.tekoki().getFfaHandler().handleRemoveItemTask(items);
            event.getDrops().clear();
            switch (player.getLastDamageCause().getCause()) {
                case PROJECTILE:
                    Projectile projectile = (Projectile) player.getLastDamageCause().getEntity();
                    if (projectile instanceof Arrow) {
                        Arrow arrow = (Arrow) projectile;
                        if (arrow.getShooter() instanceof Player) {
                            ffaPlayer.handleDeath((Player) arrow.getShooter(), false);
                        }
                    } else if (projectile instanceof ThrownPotion) {
                        ThrownPotion thrownPotion = (ThrownPotion) projectile;
                        if (thrownPotion.getShooter() instanceof Player) {
                            ffaPlayer.handleDeath((Player) thrownPotion.getShooter(), false);
                        }
                    }
                    break;
                case MAGIC:
                case POISON:
                case FIRE:
                case FIRE_TICK:
                    if (player.getKiller() != null) {
                        ffaPlayer.handleDeath(player.getKiller(), false);
                    }
                    break;
                case CUSTOM:
                case LAVA:
                case VOID:
                case FALL:
                    ffaPlayer.handleDeath(player.getLastDamageCause().getCause(), false);
                    break;
                case ENTITY_ATTACK:
                    ffaPlayer.handleDeath(player.getKiller(), false);
                    break;
            }
        }
	}

}
