package k.a.g.u.r.a;

import c.aqua.Aqua;
import c.aqua.neeeeee.custom.Tag;
import c.aqua.neeeeee.utils.Board.BoardManager;
import c.aqua.neeeeee.utils.Color;
import c.aqua.neeeeee.utils.Config.ConfigCursor;
import c.aqua.neeeeee.utils.Config.ConfigUtil;
import c.aqua.neeeeee.utils.InventoryUtil;
import c.aqua.neeeeee.utils.LocationUtil;
import c.aqua.neeeeee.utils.settings.PlayerSettings;
import com.google.gson.JsonParser;
import gg.twtter.langapi.LangAPI;
import k.a.g.u.r.a.api.KnockbackHandler;
import k.a.g.u.r.a.api.MeaProvider;
import k.a.g.u.r.a.tekokitools.PracticeSetting;
import k.a.g.u.r.a.tekokitools.SimpleLocation;
import k.a.g.u.r.a.tekokitools.StatisticsCounter;
import k.a.g.u.r.a.tekokitools.knockback.PankerHandler;
import k.a.g.u.r.a.tekokitools.knockback.iSpigotHandler;
import k.a.g.u.r.a.tekokitools.languages.CHS;
import k.a.g.u.r.a.tekokitools.languages.ENG;
import k.a.g.u.r.a.tekokitools.tasks.*;
import k.a.g.u.r.a.你mea姐的凝视.*;
import k.a.g.u.r.a.你mea姐的指令.*;
import k.a.g.u.r.a.你mea姐的指令.duel.AcceptCommand;
import k.a.g.u.r.a.你mea姐的指令.duel.DuelCommand;
import k.a.g.u.r.a.你mea姐的指令.management.ArenaCommand;
import k.a.g.u.r.a.你mea姐的指令.management.EventManageCommand;
import k.a.g.u.r.a.你mea姐的指令.management.LadderCommand;
import k.a.g.u.r.a.你mea姐的財布.Arena.ArenaHandler;
import k.a.g.u.r.a.你mea姐的財布.Board.PracticeBoardAdapter;
import k.a.g.u.r.a.你mea姐的財布.Event.EventHandler;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAHandler;
import k.a.g.u.r.a.你mea姐的財布.FFA.FFAKitType;
import k.a.g.u.r.a.你mea姐的財布.Kits.Ladder;
import k.a.g.u.r.a.你mea姐的財布.Kits.LadderHandler;
import k.a.g.u.r.a.你mea姐的財布.Match.queue.Queue;
import k.a.g.u.r.a.你mea姐的財布.Match.queue.QueueThread;
import k.a.g.u.r.a.你mea姐的財布.MeaMongo;
import k.a.g.u.r.a.你mea姐的財布.tournament.TournamentHandler;
import k.a.g.u.r.a.你mea姐的財布.天狗.SkyDog;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Tekoki extends MeaProvider {

	public static String serverName = Color.GRAY + Color.BOLD + "Mine" + Color.GREEN + Color.BOLD + "X";

	public static String serverIp = Color.translate("&7&lMine&a&lX&7.wtf");

	//twtter try to skid zonix's code :)
	
	private static Tekoki kksk;
	private ConfigUtil ladderconfig;
	private ConfigUtil arenaConfig;
	@Getter private ConfigUtil fFaConfig;
	private ConfigUtil MeaConfig;
	private ConfigUtil EventConfig;
	private EventHandler eventManager;
	private TournamentHandler tournamentManager;
	@Getter private FFAHandler ffaHandler;
	private MeaMongo meaMongo;
	//private LunarClientHandler lunarClientHandler;
	public static JsonParser PARSER = new JsonParser();
	private SimpleLocation simpleLocation;

	@Getter
	private static KnockbackHandler knockbackHandler;

	@Getter
	@Setter
	private StatisticsCounter statisticsCounter;
	
	@Setter 
	@Getter
	private long lastTournamentHostTime;
	
	private boolean isloaded = false;
	
	public static Tekoki tekoki() {
		return kksk;
	}
	
	public boolean isLoaded() {
		return this.isloaded;
	}

	@Override
	public void onLoad() {
		LangAPI.getCore().getUsers().add(this);
	}

	@Override
	public void onEnable() {
		kksk = this;
		if (!new File("config.yml").exists()) {
			getConfig().options().copyDefaults();
		}
		if (Bukkit.getPluginManager().getPlugin("Aqua-Practice") != null) {
			Bukkit.getConsoleSender().sendMessage(Color.AQUA + "MeAqua贴贴!");
			//this.lunarClientHandler = new LunarClientHandler();
			this.setupConfigs();
			this.setupDataBase();
		    this.registerHandlers();
			this.registerCommands();
			this.registerEvents();
			this.registerRunnables();
			this.registerSettings();
			this.registerKnockback();
			//this.registerLanguages();
			this.registerTags();
		} else {
			Bukkit.getConsoleSender().sendMessage("Aqua 找不到了!");
			Bukkit.getPluginManager().disablePlugin(this);
			onDisable();
		}
	}

	private void registerTags() {
		Ladder.getLadders().stream().filter(Ladder::canRanked).map(Ladder::getName).forEach(Tag::addFakeTag);
	}

	private void registerKnockback() {
		for (int i = 0;i <3 ;i++) {
			Bukkit.getConsoleSender().sendMessage(Color.RED + "Warning! This version is still in beta.");
		}
		try {
			Class.forName("spg.lgdev.iSpigot");
			knockbackHandler = new iSpigotHandler();
			Bukkit.getConsoleSender().sendMessage(Color.WHITE + "Use iSpigot as Knockback Controller");
		} catch (Exception e) {
			try {
				Class.forName("cc.pancer.panker.Panker");
				knockbackHandler = new PankerHandler();
				Bukkit.getConsoleSender().sendMessage(Color.WHITE + "Use Panker as Knockback Controller");
			} catch (Exception e1) {
				throw new RuntimeException("Could not find supported spigot.");
			}
		}
	}

	private void registerLanguages() {
		new CHS(this);
		new ENG(this);
	}

	private void registerSettings() {
		PlayerSettings.registerDefault(PracticeSetting.ALLOW_DUEL, true);
		PlayerSettings.registerDefault(PracticeSetting.SHOW_SCOREBOARD, true);
		PlayerSettings.registerDefault(PracticeSetting.ALLOW_SPECTATORS, true);
		PlayerSettings.registerDefault(PracticeSetting.TIME, "DAY");
		PlayerSettings.registerDefault(PracticeSetting.KILL_EFFECT, "LIGHTING");
	}

	private void setupDataBase() {
		this.meaMongo = new MeaMongo();
	}
	
	private void registerCommands() {
		this.getCommand("duel").setExecutor(new DuelCommand());
		this.getCommand("ladder").setExecutor(new LadderCommand());
		this.getCommand("arena").setExecutor(new ArenaCommand());
		this.getCommand("accept").setExecutor(new AcceptCommand());
		this.getCommand("viewinv").setExecutor(new MatchCommand());
		this.getCommand("party").setExecutor(new PartyCommand());
		this.getCommand("spec").setExecutor(new SpectateCommand());
		this.getCommand("stopspec").setExecutor(new SpectateCommand());
		this.getCommand("event").setExecutor(new EventCommand());
		this.getCommand("eventmanage").setExecutor(new EventManageCommand());
		this.getCommand("tournament").setExecutor(new TournamentCommand());
		this.getCommand("practice").setExecutor(new MeaCommand());
		this.getCommand("sprac").setExecutor(new MeaCommand());
		this.getCommand("day").setExecutor(new TimeCommand());
		this.getCommand("sunset").setExecutor(new TimeCommand());
		this.getCommand("night").setExecutor(new TimeCommand());
		this.getCommand("settings").setExecutor(new SettingsCommand());
		this.getCommand("ffa").setExecutor(new FFACommand());
		/*this.getCommand("lunarclient").setExecutor(new LunarClientCommand());
		this.getCommand("emote").setExecutor(new EmoteCommand());*/
	}
	
	private void registerEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ServerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ProjectileListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WorldListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MatchListener(), this);
		/*Bukkit.getServer().getPluginManager().registerEvents(new LunarListener(this.lunarClientHandler), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ClientListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new HologramListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WaypointListener(), this);*/
	}
	
	private void registerRunnables() {
		this.getServer().getWorlds().forEach(world -> {
			world.setDifficulty(Difficulty.HARD);
			world.getEntities().forEach(entity -> {
				if (!(entity instanceof Player)) {
					entity.remove();
				}
			});
			return;
		});
		Aqua.Aqua().setBoardManager(new BoardManager(this, new PracticeBoardAdapter()));
		this.getServer().getScheduler().runTaskTimer(this, new PearlTask(), 2L, 2L);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataTask(), 6000L, 6000L);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 0L, 10L);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new TimePacketTask(),200l,200l);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new BroadCastTask(), 0l, 2400l);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SnapShotsRemoveTask(), 2l,1200l);
		Bukkit.getServer().getScheduler().runTaskLater(this, () ->{
			this.isloaded = true;
		}, 80L);
		this.statisticsCounter = new StatisticsCounter();
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this,new StatisticsUpdater(), 2000L,4800L);
		lastTournamentHostTime = System.currentTimeMillis();
	}

	public void doStatisticsRefresh() {
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			SkyDog.getAll().forEach(SkyDog::save);
			this.statisticsCounter.clear();
			this.statisticsCounter.update();
		});
	}
	
	private void registerHandlers() {
		this.simpleLocation = new SimpleLocation(new ConfigCursor(MeaConfig, "spawn"),"spawn.lobby.");
		new LadderHandler();
		new ArenaHandler();
		this.loadQueues();
		new QueueThread().start();
		(this.eventManager = new EventHandler()).init();
		this.tournamentManager = new TournamentHandler();
		this.ffaHandler = new FFAHandler();
		/*this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Lunar-Client");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "Lunar-Client", this);*/ //LunarApi only work on 1.8
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(Color.WHITE + "Mea Disabled!");
	}
	
	public EventHandler getEventManager() {
        return this.eventManager;
    }
	
	public ConfigUtil getLadderConfig() {
		return this.ladderconfig;
	}
	
	public MeaMongo getMeaMongo() {
		return this.meaMongo;
	}
	
	public ConfigUtil getArenaConfig() {
		return this.arenaConfig;
	}
	
	public ConfigUtil getMainConfig() {
		return this.MeaConfig;
	}
	
    private void loadQueues() {
        for (final Ladder ladder : Ladder.getLadders()) {
            if (ladder.isEnabled() && ladder.canSolo()) {
                new Queue(ladder, false);
                if (ladder.canRanked()){
                	new Queue(ladder, true);
				}
            }
        }
    }
    
    public SimpleLocation getSimpleLocation() {
    	return this.simpleLocation;
    }
	
	public void setupConfigs() {
		this.ladderconfig = new ConfigUtil(this, "ladders.yml");
		this.arenaConfig = new ConfigUtil(this, "arenas.yml");
		this.MeaConfig = new ConfigUtil(this, "config.yml");
		if (!new File(this.getDataFolder(),"ffa.yml").exists()) {
			this.fFaConfig = new ConfigUtil(this, "ffa.yml");
			ConfigCursor ffacursor = new ConfigCursor(fFaConfig, "ffa");
			ffacursor.setPath("ffa.spawn");
			ffacursor.set(LocationUtil.serialize(new Location(Bukkit.getWorlds().get(0),0,0,0)));
			ffacursor.setPath("ffa.spawnProtection2");
			ffacursor.set(LocationUtil.serialize(new Location(Bukkit.getWorlds().get(0),0,0,0)));
			ffacursor.setPath("ffa.spawnProtection1");
			ffacursor.set(LocationUtil.serialize(new Location(Bukkit.getWorlds().get(0),0,0,0)));
			ffacursor.save();
			for (FFAKitType type : FFAKitType.values()) {
				ffacursor.setPath("kits." + type.getName() + ".");
				ffacursor.set("armor", InventoryUtil.serializeInventory(new ItemStack[4]));
				ffacursor.set("inv", InventoryUtil.serializeInventory(new ItemStack[36]));
				ffacursor.save();
			}
		} else {
			this.fFaConfig = new ConfigUtil(this, "ffa.yml");
		}
		if (!new File(this.getDataFolder(),"event.yml").exists()) {
			this.EventConfig = new ConfigUtil(this, "event.yml");
			ConfigCursor a = new ConfigCursor(this.EventConfig, "events");
			a.set("sumo");
			for (int i = 1;i<6;i++) {
				ConfigCursor cursor = new ConfigCursor(this.EventConfig, "events.sumo.sumohub" + i);
					for (int s = 1;s<5;s++) {
						cursor.set("sumospec" + s, LocationUtil.serialize(new Location(Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0)));
					}
					for (int f = 1;f<3;f++) {
						cursor.set("fighter" + f, LocationUtil.serialize(new Location(Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0)));
					}
					cursor.save();
			}
		} else {
			this.EventConfig = new ConfigUtil(this, "event.yml");
		}
	}
	
	public ConfigUtil getEventConfig() {
		return this.EventConfig;
	}

	public TournamentHandler getTournamentManager() {
		return this.tournamentManager;
	}
}
