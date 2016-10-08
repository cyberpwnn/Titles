package org.cyberpwn.titles;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phantomapi.gui.Click;
import org.phantomapi.gui.Element;
import org.phantomapi.gui.PhantomElement;
import org.phantomapi.gui.PhantomWindow;
import org.phantomapi.gui.Slot;
import org.phantomapi.gui.Window;
import org.phantomapi.util.C;

public class TitleDesigner
{
	
	public TitleDesigner(Player p, String finalTitle)
	{
		Window w = new PhantomWindow("Set the color of your title.", p);
		w.setViewport(2);
		int s = 0;
		
		for(C i : C.values())
		{
			if(i.isColor())
			{
				Element e = new PhantomElement(Material.STAINED_GLASS_PANE, i.getItemMeta(), new Slot(s), i.toString() + "" + finalTitle)
				{
					@Override
					public void onClick(Player p, Click c, Window w)
					{
						Titles.instance().getTitleController().setTitle(p, i.toString() + finalTitle);
						w.close();
						p.sendMessage(C.GREEN + "Title Changed: " + i.toString() + finalTitle);
					}
				};
				
				s++;
				w.addElement(e);
			}
		}
		
		w.open();
	}
}
