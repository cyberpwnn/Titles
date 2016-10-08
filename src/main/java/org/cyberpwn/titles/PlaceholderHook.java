package org.cyberpwn.titles;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.phantomapi.util.C;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderHook extends EZPlaceholderHook
{
	public PlaceholderHook(Plugin plugin, String placeholderName)
	{
		super(plugin, placeholderName);
	}

	@Override
	public String onPlaceholderRequest(Player p, String h)
	{
		if(h.equalsIgnoreCase("title"))
		{
			return Titles.instance().getTitleController().getTitle(p).isEmpty() ? "" : C.DARK_GRAY + "[" + C.RESET + Titles.instance().getTitleController().getTitle(p) + C.RESET + C.DARK_GRAY + "]";
		}
		
		return null;
	}
}
