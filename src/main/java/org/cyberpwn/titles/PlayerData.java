package org.cyberpwn.titles;

import org.bukkit.entity.Player;
import org.phantomapi.clust.Configurable;
import org.phantomapi.clust.DataCluster;
import org.phantomapi.clust.Keyed;
import org.phantomapi.clust.Tabled;
import org.phantomapi.lang.GList;

@Tabled("playerdata_titles")
public class PlayerData implements Configurable
{
	private DataCluster cc;
	private Player player;
	
	@Keyed("t")
	public GList<String> titles = new GList<String>();
	
	@Keyed("c")
	public String currentTitle = "";
	
	public PlayerData(Player player)
	{
		this.player = player;
		cc = new DataCluster();
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
		return player.getUniqueId().toString();
	}
}
