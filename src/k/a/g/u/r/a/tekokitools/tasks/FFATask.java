package k.a.g.u.r.a.tekokitools.tasks;

import c.aqua.neeeeee.utils.Color;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.Äãmea½ãµÄØ”²¼.FFA.FFAHandler;
import org.bukkit.entity.Player;

public class FFATask implements Runnable {

    @Override
    public void run() {
        FFAHandler ffaHandler = Tekoki.tekoki().getFfaHandler();
        ffaHandler.getFfaPlayers().forEach(ffaplayer-> {
            Player player = ffaplayer.toPlayer();
            if (player == null) {
                ffaHandler.getFfaPlayers().remove(ffaplayer);
                return;
            }
            if (ffaplayer.getSpawnTag() != -1l) {
                Long spawnTag = ffaplayer.getSpawnTag();
                if (spawnTag <= System.currentTimeMillis()) {
                    ffaplayer.resetPlayer();
                    ffaplayer.setSpawnTag(-1l);
                    player.teleport(ffaHandler.getSpawn());
                    ffaHandler.handleRespawn(ffaplayer,  Tekoki.tekoki().getFfaHandler().getKit(ffaplayer));
                    player.sendMessage(Color.GREEN + "You have been re-spawned.");
                }
            }
        });
    }

}
