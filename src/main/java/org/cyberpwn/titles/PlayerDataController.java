package org.cyberpwn.titles;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.phantomapi.Phantom;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Controller;
import org.phantomapi.lang.GMap;
import org.phantomapi.lang.GSet;

public class PlayerDataController extends Controller
{
	private GMap<Player, PlayerData> cache;
	private GSet<Player> flush;
	
	public PlayerDataController(Controllable parentController)
	{
		super(parentController);
		
		flush = new GSet<Player>();
		cache = new GMap<Player, PlayerData>();
	}
	
	@Override
	public void onStart()
	{
		
	}
	
	@Override
	public void onStop()
	{
		for(Player i : Phantom.instance().onlinePlayers())
		{
			save(i);
		}
	}
	
	public boolean contains(Player player)
	{
		return cache.contains(player);
	}
	
	public PlayerData get(Player p)
	{
		if(!contains(p))
		{
			load(p);
		}
		
		return cache.get(p);
	}
	
	public void flush()
	{
		for(Player i : flush)
		{
			saveMysql(cache.get(i));
		}
		
		flush.clear();
	}
	
	public void load(Player player)
	{
		if(!contains(player))
		{
			PlayerData pd = new PlayerData(player);
			loadMysql(pd);
			cache.put(player, pd);
		}
	}
	
	public void flush(Player player)
	{
		if(contains(player))
		{
			flush.add(player);
		}
	}
	
	public void save(Player player)
	{
		if(contains(player))
		{
			saveMysql(cache.get(player));
			cache.remove(player);
		}
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		save(e.getPlayer());
	}
}
