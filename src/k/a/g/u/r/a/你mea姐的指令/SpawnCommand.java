package k.a.g.u.r.a.你mea姐的指令;

import c.aqua.neeeeee.utils.Color;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAPlayer;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Color.RED + "You must be a player to execute this command!");
            return false;
        }
        Player player = (Player) commandSender;
        SkyDog skyDog = SkyDog.getskydog(player);
        FFAPlayer ffaPlayer = skyDog.getFfaPlayer();
        if (strings.length == 0) {
            if (!ffaPlayer.isInFight()) {
                ffaPlayer.setSpawnTag(System.currentTimeMillis() + 10 * 1000);
                player.sendMessage(Color.GREEN + "You will respawn in 10 seconds.");
                return true;
            } else {
                player.sendMessage(Color.RED + "You can't respawn yet!");
                return false;
            }
        }
        return false;
    }
}
