package k.a.g.u.r.a.你mea姐的指令;

import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.LocationUtil;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAHandler;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAKit;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAKitType;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAPlayer;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class FFACommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        //Player only
        //Add spawn command later
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Color.RED + "You must be a player to execute this command!");
            return false;
        }
        Player player = (Player) commandSender;
        SkyDog skyDog = SkyDog.getskydog(player);
        FFAPlayer ffaPlayer = skyDog.getFfaPlayer();
        if (strings.length == 1) {
            switch (strings[0].toLowerCase()) {
                case "leave":
                    Tekoki.tekoki().getFfaHandler().handleleave(ffaPlayer, false);
                    break;
                case "setspawn":
                    if (!player.hasPermission("mea.ffa.manage")) {
                        player.sendMessage(Color.RED + "You don't have permission to do this!");
                        return false;
                    }
                    Tekoki.tekoki().getFfaHandler().setSpawn(player.getLocation());
                    Tekoki.tekoki().getFfaHandler().save();
                    player.sendMessage(Color.GREEN + "You have updated ffa spawn.");
                    break;
                default:
                    player.sendMessage(USAGE);
                    break;
            }
            // /ffa setkit <kit>
        } else if (strings.length == 2) {
            FFAKitType type;
            FFAKit ffaKit;
            switch (strings[0].toLowerCase()) {
                case "setpoint":
                    if (!player.hasPermission("mea.ffa.manage")) {
                        player.sendMessage(Color.RED + "You don't have permission to do this!");
                        return false;
                    }
                    String pointNumber = strings[1];
                    Integer point;
                    try {
                        point = Integer.parseInt(pointNumber);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(Color.RED + "/ffa setpoint <1/2>");
                        break;
                    }
                    if (point != 1 && point != 2) {
                        player.sendMessage(Color.RED + "/ffa setpoint <1/2>");
                        break;
                    }
                    FFAHandler ffaHandler = Tekoki.tekoki().getFfaHandler();
                    try {
                        Field field = FFAHandler.class.getDeclaredField("spawnProt" + point);
                        field.setAccessible(true);
                        field.set(ffaHandler, player.getLocation());
                        ffaHandler.save();
                        ffaHandler.refactorSpawn();
                        player.sendMessage(Color.GREEN + "You set FFA spawn protect area point " + Color.YELLOW + point + Color.GREEN + " to " + Color.YELLOW + LocationUtil.serialize((Location) field.get(ffaHandler)));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        break;
                    }
                    break;
                case "loadkit":
                    if (!player.hasPermission("mea.ffa.manage")) {
                        player.sendMessage(Color.RED + "You don't have permission to do this!");
                        return false;
                    }
                    try {
                        type = FFAKitType.valueOf(strings[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(Color.RED + "The kit you type doesn't exist! Please check again!");
                        return false;
                    }
                    ffaKit = FFAKit.getFfaKits().get(type);
                    player.getInventory().setContents(ffaKit.getInventory());
                    player.getInventory().setArmorContents(ffaKit.getArmor());
                    ffaKit.getEffects().forEach(player::addPotionEffect);
                    player.sendMessage(Color.GREEN + "You have loaded the kit: " + Color.RESET + ffaKit.getKitName());
                    break;
                case "setkit":
                    if (!player.hasPermission("mea.ffa.manage")) {
                        player.sendMessage(Color.RED + "You don't have permission to do this!");
                        return false;
                    }
                    try {
                        type = FFAKitType.valueOf(strings[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(Color.RED + "The kit you type doesn't exist! Please check again!");
                        return false;
                    }
                    ffaKit = FFAKit.getFfaKits().get(type);
                    ffaKit.setInventory(player.getInventory().getContents());
                    ffaKit.setArmor(player.getInventory().getArmorContents());
                    ffaKit.getEffects().addAll(player.getActivePotionEffects());
                    ffaKit.save();
                    player.sendMessage(Color.GREEN + "You have updated the kit for " + Color.RESET + ffaKit.getKitName());
                    break;
                default:
                    player.sendMessage(USAGE);
                    break;
            }
        } else {
            player.sendMessage(USAGE);
            return false;
        }
        return false;
    }

    private String[] USAGE = new String[]{
            Color.GRAY + Color.STRIKE_THROUGH + "---------------------------------------------",
            Color.GREEN + "Tournament Help",
            Color.WHITE + "/ffa leave" + Color.GRAY + " Leave current FFA game",
            Color.GRAY + Color.STRIKE_THROUGH + "---------------------------------------------",
    };
}
