package org.cyberpwn.titles;

import org.phantomapi.construct.PhantomPlugin;
import org.phantomapi.util.DMSRequire;
import org.phantomapi.util.DMSRequirement;

@DMSRequire(DMSRequirement.SQL)
public class Titles extends PhantomPlugin
{
	private static Titles instance;
	private TitleController titleController;
	
	public void enable()
	{
		instance = this;
		
		titleController = new TitleController(this);
		
		register(titleController);
	}

	public TitleController getTitleController()
	{
		return titleController;
	}
	
	public static Titles instance()
	{
		return instance;
	}

	@Override
	public void disable()
	{
		
	}
}
