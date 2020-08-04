package k.a.g.u.r.a.你mea姐的財布.Board;

import c.aqua.Aqua;
import c.aqua.neeeeee.utils.Board.Board;
import c.aqua.neeeeee.utils.Board.BoardAdapter;
import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.Time;
import k.a.g.u.r.a.Tekoki;
import k.a.g.u.r.a.tekokitools.PracticeSetting;
import k.a.g.u.r.a.你mea姐的財布.Event.Event;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAPlayer;
import k.a.g.u.r.a.你mea姐的財布.Match.MSkyDog;
import k.a.g.u.r.a.你mea姐的財布.Match.abstractMatch;
import k.a.g.u.r.a.你mea姐的財布.Match.queue.Queue;
import k.a.g.u.r.a.你mea姐的財布.Party.Party;
import k.a.g.u.r.a.你mea姐的財布.tournament.Tournament;
import k.a.g.u.r.a.你mea姐的財布.tournament.TournamentState;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PracticeBoardAdapter implements BoardAdapter
{
    public String getTitle(final Player player) {
        return Tekoki.serverName + Color.DARK_GRAY + " (Practice)";
    }
    
    public List<String> getScoreboard(final Player player, final Board board) {
        final SkyDog skydog = SkyDog.getskydog(player);
        if (!(boolean)Aqua.getSetting(player, PracticeSetting.SHOW_SCOREBOARD)) {
            return null;
        }
        try {
            if (skydog.isInLobby()) {
                return this.getLobbyBoard(player, board);
            } else if (skydog.isInQueue()) {
                return this.getQueuingBoard(player, board);
            } else if (skydog.isInMatch()) {
                return this.getMatchBoard(player, board);
            } else if (skydog.isSpectating()) {
                return this.getSpectatingBoard(player, board);
            } else if (skydog.isInEvent()) {
                return this.getEventBoard(player, board);
            } else if (skydog.isInFFA()) {
                return this.getFFABoard(player, board);
            }
        } catch (Exception e) {
            List<String> list = new ArrayList<String>();
            list.add(Color.RED + "ScoreBoard Err");
            list.add(Color.DARK_RED + "Contact developer");
            e.printStackTrace();
            return list;
        }
        List<String> list = new ArrayList<String>();
        list.add(Color.RED + "ScoreBoard Err");
        list.add(Color.DARK_RED + "Contact developer");
        return list;
    }
    
    public List<String> getLobbyBoard(final Player player, final Board board){
    	List<String> toReturn = new ArrayList<String>();
    	final SkyDog skydog = SkyDog.getskydog(player);
    	Party party = skydog.getParty();
        toReturn.add(Color.WHITE + "Online: " + Color.GRAY + Bukkit.getOnlinePlayers().size());
        toReturn.add(Color.WHITE + "In Queues: " + Color.GRAY + Tekoki.tekoki().getQueueingCount());
        toReturn.add(Color.WHITE + "In Fights: " + Color.GRAY + Tekoki.tekoki().getFightingCount());
        if(Tekoki.tekoki().getTournamentManager().getTournaments().size() > 0) {
            Tournament tournament = Tekoki.tekoki().getTournamentManager().getTournament(Tekoki.tekoki().getTournamentManager().getLastCreatedId());

            if(tournament != null) {
            	toReturn.add(Color.BORDER_LINE_SCOREBOARD);
            	toReturn.add(Color.DARK_GREEN + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName());
            	toReturn.add(Color.GREEN + (tournament.getTournamentState() != TournamentState.STARTING ? "Remaining: " : "Players: ") + Color.GRAY + tournament.getPlayers().size() + "/" + tournament.getSize());

                if(tournament.getTournamentState() != TournamentState.STARTING) {
                	toReturn.add(Color.GREEN + "Round: " + Color.GRAY + tournament.getCurrentRound());
                }
            } else if(party != null) {
            	toReturn.add(Color.BORDER_LINE_SCOREBOARD);
            	toReturn.add(Color.GREEN + "Your Party: " + Color.GRAY + party.getPlayers().size());
            }
        } else if(party != null) {
        	toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        	toReturn.add(Color.GREEN + "Your Party: " + Color.GRAY + party.getPlayers().size());
        }
        toReturn.add(0, Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add("  " + Tekoki.serverIp);
    	return toReturn;
    }

    public List<String> getFFABoard(final Player player, final Board board) {
        List<String> toReturn = new ArrayList<>();
        SkyDog skyDog = SkyDog.getskydog(player);
        FFAPlayer ffaPlayer = skyDog.getFfaPlayer();
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.DARK_GREEN + "FFA");
        toReturn.add(Color.WHITE + " Kills: " + Color.GREEN + ffaPlayer.getKills());
        toReturn.add(Color.WHITE + " Deaths: " + Color.GREEN + ffaPlayer.getDeaths());
        if (ffaPlayer.getKillStreaks() != 0) {
            toReturn.add(Color.WHITE + " KillStreaks: " + Color.GREEN + ffaPlayer.getKillStreaks());
        }
        toReturn.add("");
        toReturn.add(Color.WHITE + " State: " + (ffaPlayer.isInFight() ? Color.RED + "Fighting" + " (" + Time.millisToSeconds(ffaPlayer.getTagRemaining()) + ")" : Color.GREEN + "Free"));
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add("  " + Tekoki.serverIp);
        return toReturn;
    }
    
    public List<String> getQueuingBoard(final Player player, final Board board){
    	List<String> toReturn = new ArrayList<String>();
    	final SkyDog skydog = SkyDog.getskydog(player);
        final Queue queue = skydog.getQueuePlayer().getQueue();
        toReturn.add(Color.WHITE + "Online: " + Color.GRAY + Bukkit.getOnlinePlayers().size());
        toReturn.add(Color.WHITE + "In Queues: " + Color.GRAY + Tekoki.tekoki().getQueueingCount());
        toReturn.add(Color.WHITE + "In Fights: " + Color.GRAY + Tekoki.tekoki().getFightingCount());
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.WHITE + "Queued:");
        toReturn.add(" " + Color.GREEN + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getLadder().getName());
        toReturn.add(Color.WHITE + "Time:");
        toReturn.add(" " + Color.GREEN + Time.millisToTimer(skydog.getQueuePlayer().getPassed()));
        if (queue.isRanked()) {
            toReturn.add(Color.BORDER_LINE_SCOREBOARD);
            toReturn.add(Color.WHITE + "Range:");
            toReturn.add(" " + Color.GRAY + skydog.getQueuePlayer().getMinRange() + " -> " + skydog.getQueuePlayer().getMaxRange());
        }
        toReturn.add(0, Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add("  " + Tekoki.serverIp);
        return toReturn;
    }
    
    public List<String> getMatchBoard(final Player player, final Board board){
    	List<String> toReturn = new ArrayList<String>();
    	final SkyDog skydog = SkyDog.getskydog(player);
        final abstractMatch abstractMatch = skydog.getAbstractMatch();
        if (abstractMatch == null) {
            return null;
        }
        switch (abstractMatch.getType()) {
            case SOLO:
                if (!abstractMatch.isEnding()) {
                    final MSkyDog opponent = abstractMatch.getOpponentMSkyDog(player);
                    toReturn.add(Color.WHITE + "Opponent: " + Color.GREEN + opponent.getName());
                    //toReturn.add(Color.WHITE + "Ping: " + Color.PINK + ((player != null) ? ((CraftPlayer)player).getHandle().ping : "~") + "ms " + Color.WHITE + Color.BOLD + "|" + Color.RESET + Color.PINK + " " + ((Bukkit.getPlayer(opponent.getName()) != null) ? ((CraftPlayer)opponent.toPlayer()).getHandle().ping : "~") + "ms");
                    toReturn.add(Color.WHITE + "Duration: " + Color.GREEN + abstractMatch.getDuration());
                } else {
                    toReturn.add(Color.GREEN + "Winner: " + Color.WHITE + abstractMatch.getWinningPlayer().getName());
                    toReturn.add(Color.GRAY + "Duration: " + Color.GRAY + abstractMatch.getDuration());
                }
                break;
            case TEAM:
                toReturn.add(Color.WHITE + "Opponents: " + Color.GREEN + abstractMatch.getOpponentsLeft(player) + Color.WHITE +"/" + Color.GREEN + abstractMatch.getOpponentTeam(player).getTeamPlayers().size());
                toReturn.add(Color.WHITE + "Duration: " + Color.GREEN + abstractMatch.getDuration());
                if (abstractMatch.getTeam(player).getTeamPlayers().size() >= 8) {
                    toReturn.add(Color.WHITE + "Your Team: " + Color.GREEN + abstractMatch.getTeam(player).getTeamPlayers().size());
                }
                else {
                    toReturn.add("");
                    toReturn.add(Color.WHITE + "Your Team:");
                    abstractMatch.getTeam(player).getTeamPlayers()
                            .forEach(teamPlayer ->
                                    toReturn.add(" " + ((teamPlayer.isDisconnected()
                                            || !teamPlayer.isAlive()) ? Color.STRIKE_THROUGH : "") + teamPlayer.getName()));
                }
                break;
            case FFA:
                toReturn.add(Color.WHITE + "Duration: " + Color.GREEN + abstractMatch.getDuration());
                toReturn.add(Color.WHITE + "Alives: " + Color.GREEN + abstractMatch.FFATeam().getAliveCount() + Color.WHITE +"/" + Color.GREEN + abstractMatch.FFATeam().getTeamPlayers().size());
                if (abstractMatch.getMSkyDog(player).getPlayerTarget() != null
                        && Bukkit.getPlayer(abstractMatch.getMSkyDog(player).getPlayerTarget().getUuid()) != null
                        && abstractMatch.getMSkyDog(player).getPlayerTarget().isAlive()) {
                    MSkyDog target = abstractMatch.getMSkyDog(player).getPlayerTarget();
                    double health = target.toPlayer().getHealth();
                    String formated = new DecimalFormat("#.000").format(health);
                    toReturn.add("");
                    toReturn.add(" " + Color.WHITE + SkyDog.getskydog(target.toPlayer()).getNick() + ": ");
                    toReturn.add("  " + (health > 5.0 ?
                            (health > 7.5 ? Color.DARK_GREEN : Color.GREEN) :
                            (health < 3.5 ? Color.DARK_RED : Color.RED)) +
                            formated + Color.UNICODE_HEART);
                }
                break;
        }
    	toReturn.add(0, Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add("  " + Tekoki.serverIp);
        return toReturn;
    }
    
    public List<String> getSpectatingBoard(final Player player, final Board board){
    	List<String> toReturn = new ArrayList<String>();
    	final SkyDog skydog = SkyDog.getskydog(player);
        final abstractMatch abstractMatch = skydog.getAbstractMatch();
        toReturn.add(Color.WHITE + "Ladder: " + Color.GREEN + abstractMatch.getLadder().getName());
        toReturn.add(Color.WHITE + "Duration: " + Color.GREEN + abstractMatch.getDuration());
        if (abstractMatch.isSoloMatch()) {
            toReturn.add(Color.WHITE + "Players:");
            toReturn.add(" " + abstractMatch.getA().getName() + Color.GRAY + " (" + ((abstractMatch.A() != null) ? ((CraftPlayer) abstractMatch.A()).getHandle().ping : "~") + ")");
            toReturn.add(" " + abstractMatch.getB().getName() + Color.GRAY + " (" + ((abstractMatch.B() != null) ? ((CraftPlayer) abstractMatch.B()).getHandle().ping : "~") + ")");
        } else if (abstractMatch.isTeamMatch()) {
            toReturn.add(Color.WHITE + "Players:");
            toReturn.add(" " + abstractMatch.TeamA().getLeader().getName() + "'s Team");
            toReturn.add(" " + abstractMatch.TeamB().getLeader().getName() + "'s Team");
        } else if (abstractMatch.isFFAMatch()) {
        	toReturn.add(Color.WHITE + "Alives: " + Color.GREEN + abstractMatch.FFATeam().getAliveCount() + "/" + abstractMatch.FFATeam().getTeamPlayers().size());
		}
    	toReturn.add(0, Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add("  " + Tekoki.serverIp);
        return toReturn;
    }
    
    public List<String> getEventBoard(final Player player, final Board board){
    	List<String> toReturn = new ArrayList<String>();
    	final SkyDog skydog = SkyDog.getskydog(player);
        final Event event = skydog.getEvent();
        toReturn.add(Color.GRAY + "Event: " + Color.GREEN + event.getName());
        if (event.isWaiting()) {
            toReturn.add(Color.GRAY + "Players: " + Color.WHITE + event.getEventPlayers().size() + "/" + event.getMaxPlayers());
            toReturn.add("");
            if (event.getCooldown() == null) {
                toReturn.add(Color.GRAY + Color.ITALIC + "Waiting for players...");
            }
            else {
                toReturn.add(Color.GRAY + Color.ITALIC + "Starting in " + Time.millisToSeconds(event.getCooldown().getRemaining()) + "s");
            }
        }
        else {
            toReturn.add(Color.GREEN + "Remaining: " + Color.WHITE + event.getRemainingPlayers() + "/" + event.getMaxPlayers());
            toReturn.add(Color.GREEN + "Duration: " + Color.WHITE + event.getRoundDuration());
            toReturn.add(Color.GREEN + "Players:");
            toReturn.add(" " + event.getRoundPlayerA().getName() + Color.GRAY + " (" + event.getRoundPlayerA().toPlayer().spigot().getPing() + " ms)");
            toReturn.add(" " + event.getRoundPlayerB().getName() + Color.GRAY + " (" + event.getRoundPlayerB().toPlayer().spigot().getPing()  + " ms)");
        }
    	toReturn.add(0, Color.BORDER_LINE_SCOREBOARD);
        toReturn.add(Color.BORDER_LINE_SCOREBOARD);
        toReturn.add("  " + Tekoki.serverIp);
        return toReturn;
    }
    
    public long getInterval() {
        return 2L;
    }
    
    public void preLoop() {
    }
    
    public void onScoreboardCreate(final Player player, final Scoreboard scoreboard) {
    }
}

