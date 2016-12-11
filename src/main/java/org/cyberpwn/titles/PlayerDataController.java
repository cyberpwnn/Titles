package org.cyberpwn.titles;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.phantomapi.clust.ConfigurationHandler;
import org.phantomapi.clust.PD;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Controller;
import org.phantomapi.lang.GList;
import org.phantomapi.sync.TaskLater;

public class PlayerDataController extends Controller
{
	public PlayerDataController(Controllable parentController)
	{
		super(parentController);
	}
	
	@Override
	public void onStart()
	{
		
	}
	
	@Override
	public void onStop()
	{
		
	}
	
	public void load(Player player)
	{
		PlayerData pd = new PlayerData(player);
		
		try
		{
			if(ConfigurationHandler.rowExists(pd, getSQL()))
			{
				loadMysql(pd);
				PD.get(player).getConfiguration().set("titles.t", pd.titles);
				PD.get(player).getConfiguration().set("titles.c", pd.currentTitle);
				
				new TaskLater()
				{
					@Override
					public void run()
					{
						try
						{
							ConfigurationHandler.dropRow(pd, getSQL());
						}
						
						catch(Exception e)
						{
							
						}
					}
				};
			}
			
			List<String> titles = PD.get(player).getConfiguration().getStringList("titles.t");
			String title = PD.get(player).getConfiguration().getString("titles.c");
			
			if(titles == null)
			{
				titles = new GList<String>();
			}
			
			if(title == null)
			{
				title = "";
			}
			
			PD.get(player).getConfiguration().set("titles.t", titles);
			PD.get(player).getConfiguration().set("titles.c", title);
			PD.get(player).flush();
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		load(e.getPlayer());
	}
}
