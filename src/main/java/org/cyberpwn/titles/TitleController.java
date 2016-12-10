package org.cyberpwn.titles;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.phantomapi.clust.Configurable;
import org.phantomapi.clust.DataCluster;
import org.phantomapi.clust.Keyed;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Controller;
import org.phantomapi.lang.GList;
import org.phantomapi.sync.TaskLater;

public class TitleController extends Controller implements Configurable
{
	private DataCluster cc;
	private PlayerDataController pdc;
	private CommandController commandController;
	private PlaceholderHook hook;
	private GList<Player> locks;
	
	@Keyed("titles.titles")
	public GList<String> titles = new GList<String>(new String[] {"Salty", "Chilled", "Frozen", "0K"});
	
	@Keyed("titles.blacklist")
	public GList<String> bl = new GList<String>(new String[] {"blacklisted-cASe-SenSiTiVE"});
	
	public TitleController(Controllable parentController)
	{
		super(parentController);
		
		locks = new GList<Player>();
		cc = new DataCluster();
		pdc = new PlayerDataController(this);
		commandController = new CommandController(this);
		hook = new PlaceholderHook(getPlugin(), "titles");
		hook.hook();
		
		register(pdc);
		register(commandController);
		
		getPlugin().getCommand("titles").setExecutor(commandController);
	}
	
	public boolean hasTitle(Player p, String s)
	{
		if(isLocked(p))
		{
			return false;
		}
		
		return pdc.get(p).titles.contains(s);
	}
	
	public boolean hasTitleSelected(Player p)
	{
		if(isLocked(p))
		{
			return false;
		}
		
		return pdc.get(p).currentTitle.length() > 0;
	}
	
	public boolean hasTitles(Player p)
	{
		if(isLocked(p))
		{
			return false;
		}
		
		return !pdc.get(p).titles.isEmpty();
	}
	
	public GList<String> getTitles(Player p)
	{
		if(isLocked(p))
		{
			return new GList<String>();
		}
		
		return pdc.get(p).titles.copy();
	}
	
	public boolean isBlackListed(String s)
	{
		return bl.contains(s);
	}
	
	public void addTitle(Player p, String s)
	{
		if(isLocked(p))
		{
			return;
		}
		
		if(!hasTitle(p, s))
		{
			pdc.get(p).titles.add(s);
			pdc.flush(p);
		}
	}
	
	public void removeTitle(Player p, String s)
	{
		if(isLocked(p))
		{
			return;
		}
		
		pdc.get(p).titles.remove(s);
		pdc.flush(p);
	}
	
	public void addRandomTitle(Player p)
	{
		if(isLocked(p))
		{
			return;
		}
		
		addTitle(p, getRandomUnownedTitle(p));
	}
	
	public String getRandomUnownedTitle(Player p)
	{
		if(isLocked(p))
		{
			return null;
		}
		
		GList<String> tr = titles.copy();
		
		for(String i : tr.copy())
		{
			if(hasTitle(p, i) || isBlackListed(i))
			{
				tr.remove(i);
			}
		}
		
		if(tr.isEmpty())
		{
			return null;
		}
		
		if(tr.size() == 1)
		{
			return tr.get(0);
		}
		
		return tr.pickRandom();
	}
	
	public String getTitle(Player p)
	{
		if(isLocked(p))
		{
			return "";
		}
		
		return pdc.get(p).currentTitle;
	}
	
	public void setTitle(Player p, String s)
	{
		if(isLocked(p))
		{
			return;
		}
		
		pdc.get(p).currentTitle = s;
		pdc.flush(p);
	}
	
	@Override
	public void onStart()
	{
		loadCluster(this);
	}
	
	@Override
	public void onNewConfig()
	{
		// Dynamic
	}
	
	@Override
	public void onReadConfig()
	{
		// Dynamic
	}
	
	@Override
	public DataCluster getConfiguration()
	{
		return cc;
	}
	
	@Override
	public String getCodeName()
	{
		return "config";
	}
	
	@Override
	public void onStop()
	{
		
	}
	
	public boolean isLocked(Player p)
	{
		return locks.contains(p);
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		locks.add(e.getPlayer());
		
		new TaskLater(100)
		{
			@Override
			public void run()
			{
				locks.remove(e.getPlayer());
			}
		};
	}
}
