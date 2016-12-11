package org.cyberpwn.titles;

import org.bukkit.entity.Player;
import org.phantomapi.clust.Configurable;
import org.phantomapi.clust.DataCluster;
import org.phantomapi.clust.Keyed;
import org.phantomapi.clust.PD;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Controller;
import org.phantomapi.lang.GList;

public class TitleController extends Controller implements Configurable
{
	private DataCluster cc;
	private PlayerDataController pdc;
	private CommandController commandController;
	private PlaceholderHook hook;
	
	@Keyed("titles.titles")
	public GList<String> titles = new GList<String>(new String[] {"Salty", "Chilled", "Frozen", "0K"});
	
	@Keyed("titles.blacklist")
	public GList<String> bl = new GList<String>(new String[] {"blacklisted-cASe-SenSiTiVE"});
	
	public TitleController(Controllable parentController)
	{
		super(parentController);
		
		cc = new DataCluster();
		pdc = new PlayerDataController(this);
		commandController = new CommandController(this);
		hook = new PlaceholderHook(getPlugin(), "titles");
		hook.hook();
		
		register(pdc);
		register(commandController);
		
		getPlugin().getCommand("titles").setExecutor(commandController);
	}
	
	public DataCluster g(Player p)
	{
		return PD.get(p).getConfiguration();
	}
	
	public boolean hasTitle(Player p, String s)
	{
		return g(p).getStringList("titles.t").contains(s);
	}
	
	public boolean hasTitleSelected(Player p)
	{
		return g(p).getString("titles.c").length() > 0;
	}
	
	public boolean hasTitles(Player p)
	{
		return !g(p).getStringList("titles.t").isEmpty();
	}
	
	public GList<String> getTitles(Player p)
	{
		return new GList<String>(g(p).getStringList("titles.t"));
	}
	
	public boolean isBlackListed(String s)
	{
		return bl.contains(s);
	}
	
	public void addTitle(Player p, String s)
	{
		if(!hasTitle(p, s))
		{
			g(p).set("titles.t", getTitles(p).copy().qadd(s));
		}
	}
	
	public void removeTitle(Player p, String s)
	{
		g(p).set("titles.t", getTitles(p).copy().qdel(s));
	}
	
	public void addRandomTitle(Player p)
	{
		addTitle(p, getRandomUnownedTitle(p));
	}
	
	public String getRandomUnownedTitle(Player p)
	{
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
		return g(p).getString("titles.c");
	}
	
	public void setTitle(Player p, String s)
	{
		g(p).set("titles.c", s);
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
}
