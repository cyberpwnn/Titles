package org.cyberpwn.titles;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.phantomapi.Phantom;
import org.phantomapi.clust.Configurable;
import org.phantomapi.clust.DataCluster;
import org.phantomapi.clust.Keyed;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Controller;
import org.phantomapi.gui.Click;
import org.phantomapi.gui.Element;
import org.phantomapi.gui.PhantomElement;
import org.phantomapi.gui.PhantomWindow;
import org.phantomapi.gui.Slot;
import org.phantomapi.gui.Window;
import org.phantomapi.lang.GList;
import org.phantomapi.sync.TaskLater;
import org.phantomapi.text.Tabulator;
import org.phantomapi.util.C;
import org.phantomapi.util.F;

public class CommandController extends Controller implements CommandExecutor, Configurable
{
	private DataCluster cc;
	
	@Keyed("lang.msg.no-titles")
	public String MSG_NO_TITLES = "&cYou do not have any titles! Unlock some!";
	
	@Keyed("gui.gui-title")
	public String GUI_TITLE = "Select a Title";
	
	@SuppressWarnings("deprecation")
	@Keyed("gui.title-icon.id")
	public int GUI_TITLE_ID = Material.STAINED_GLASS_PANE.getId();
	
	@Keyed("gui.title-icon.meta")
	public int GUI_TITLE_MD = 2;
	
	@Keyed("gui.title-icon.meta-has")
	public int GUI_TITLE_HAS = 3;
	
	@Keyed("queue")
	public GList<String> queued = new GList<String>();
	
	public CommandController(Controllable parentController)
	{
		super(parentController);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("titles"))
		{
			TitleController t = (TitleController) getParentController();
			
			if(sender.hasPermission("titles.god"))
			{
				if(args.length > 2)
				{
					if(args[0].equalsIgnoreCase("give"))
					{
						if(Phantom.instance().canFindPlayer(args[1]))
						{
							Player p = Phantom.instance().findPlayer(args[1]);
							String title = args[2].toLowerCase();
							Boolean f = false;
							
							for(String i : t.titles)
							{
								if(i.toLowerCase().equals(title))
								{
									title = i;
									f = true;
									
									break;
								}
							}
							
							if(args[2].equals("$r"))
							{
								title = t.getRandomUnownedTitle(p);
								
								if(title == null)
								{
									return true;
								}
								
								t.addTitle(p, title);
								p.sendMessage(C.GREEN + "Unlocked Title: " + C.RESET + C.BOLD + title);
								sender.sendMessage(C.GREEN + "Title Given: " + p.getName() + " <> " + C.RESET + C.BOLD + title);
								return true;
							}
							
							if(f)
							{
								if(!t.hasTitle(p, title))
								{
									t.addTitle(p, title);
									p.sendMessage(C.GREEN + "Unlocked Title: " + C.RESET + C.BOLD + title);
									sender.sendMessage(C.GREEN + "Title Given: " + p.getName() + " <> " + C.RESET + C.BOLD + title);
								}
								
								else
								{
									sender.sendMessage(C.RED + p.getName() + " already has the title " + title);
								}
							}
							
							else
							{
								sender.sendMessage(C.RED + "No title found.");
							}
							
							return true;
						}
						
						else
						{
							sender.sendMessage(C.RED + "No Player Found under or close to " + args[1]);
							sender.sendMessage(C.YELLOW + "Queued for execution when " + args[1] + " becomes online.");
							queue(cmd, args, args[1]);
							
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("remove"))
					{
						if(Phantom.instance().canFindPlayer(args[1]))
						{
							Player p = Phantom.instance().findPlayer(args[1]);
							String title = args[2].toLowerCase();
							Boolean f = false;
							
							for(String i : t.titles)
							{
								if(i.toLowerCase().equals(title))
								{
									title = i;
									f = true;
									
									break;
								}
							}
							
							if(f)
							{
								if(t.hasTitle(p, title))
								{
									t.removeTitle(p, title);
									p.sendMessage(C.RED + "Lost Title: " + C.RESET + C.BOLD + title);
									sender.sendMessage(C.RED + "Title Removed: " + p.getName() + " <> " + C.RESET + C.BOLD + title);
								}
								
								else
								{
									sender.sendMessage(C.RED + p.getName() + " doesnt have the title " + title);
								}
							}
							
							else
							{
								sender.sendMessage(C.RED + "No title found.");
							}
							
							return true;
						}
						
						else
						{
							sender.sendMessage(C.RED + "No Player Found under or close to " + args[1]);
							
							return true;
						}
					}
				}
				
				if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("list") && sender.hasPermission("titles.god"))
					{
						if(Phantom.instance().canFindPlayer(args[1]))
						{
							Player p = Phantom.instance().findPlayer(args[1]);
							
							for(String i : t.getTitles(p))
							{
								sender.sendMessage("- " + i);
							}
							
							return true;
						}
						
						else
						{
							sender.sendMessage(C.RED + "No Player Found under or close to " + args[1]);
							
							return true;
						}
					}
				}
				
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("list"))
					{
						sender.sendMessage(C.GREEN + t.titles.toString(", "));
						
						return true;
					}
				}
			}
			
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				
				if(!t.hasTitles(p))
				{
					p.sendMessage(F.color(MSG_NO_TITLES));
				}
				
				else
				{
					if(args.length == 2 && args[0].equalsIgnoreCase("color"))
					{
						String set = F.color(args[1]);
						String current = Titles.instance().getTitleController().getTitle(p);
						
						if(current != null)
						{
							if(args[1].toLowerCase().contains("&k"))
							{
								sender.sendMessage(C.RED + "Your input: " + set + C.RESET + C.RED + " contains magic.");
								return true;
							}
							
							if(C.stripColor(current).equals(C.stripColor(set)))
							{
								Titles.instance().getTitleController().setTitle(p, set);
								sender.sendMessage(C.GREEN + "Title colored: " + set);
							}
							
							else
							{
								sender.sendMessage(C.RED + "Your input: " + C.stripColor(set) + C.RED + " does not match " + C.stripColor(current));
							}
						}
						
						else
						{
							sender.sendMessage(C.RED + "Select a title first! (/title)");
						}
						
						return true;
					}
					
					else if(args.length == 1 && args[0].equalsIgnoreCase("clear"))
					{
						Titles.instance().getTitleController().setTitle(p, "");
						sender.sendMessage(C.GREEN + "Poof! It's gone.");
						return true;
					}
					
					else if(args.length == 2 && args[0].equalsIgnoreCase("apt"))
					{
						return true;
					}
					
					if(p.hasPermission("titles.all"))
					{
						for(String i : t.titles)
						{
							if(!t.hasTitle(p, i) && !t.isBlackListed(i))
							{
								t.addTitle(p, i);
								p.sendMessage(C.GREEN + "Unlocked Title: " + C.RESET + C.BOLD + i);
							}
						}
					}
					
					GList<Window> wins = new GList<Window>();
					Tabulator<String> tabulator = new Tabulator<String>(45);
					
					for(String i : t.getTitles(p))
					{
						tabulator.add(i);
					}
					
					for(int i = 0; i < tabulator.getTabCount(); i++)
					{
						boolean first = i == 0;
						boolean last = i == tabulator.getTabCount() - 1;
						int v = i + 1;
						int ix = 0;
						
						Window w = new PhantomWindow(F.color(GUI_TITLE) + " " + v + " / " + tabulator.getTabCount(), p);
						
						for(String j : tabulator.getTab(i))
						{
							Element e = new PhantomElement(Material.getMaterial(GUI_TITLE_ID), (byte) GUI_TITLE_MD, new Slot(ix), C.BOLD + j)
							{
								@Override
								public void onClick(Player p, Click c, Window w)
								{
									if(c.equals(Click.LEFT))
									{
										new TaskLater()
										{
											@Override
											public void run()
											{
												t.setTitle(p, j);
												p.sendMessage(C.GREEN + "Set Title: " + C.RESET + C.BOLD + title);
											}
										};
									}
									
									else
									{
										new TaskLater()
										{
											@Override
											public void run()
											{
												new TitleDesigner(p, j);
											}
										};
									}
									
									w.close();
								}
							};
							
							e.addText(C.GREEN + "Click this to set your new title");
							e.addText(C.GREEN + "Right Click this to customize!");
							e.addText(C.GREEN + "- Use " + C.YELLOW + "/title color <colored title>");
							e.addText(C.GREEN + "- Use " + C.YELLOW + "/title clear" + C.GREEN + " to clear this title.");
							
							if(t.hasTitleSelected(p) && C.stripColor(t.getTitle(p).toLowerCase()).equals(j.toLowerCase()))
							{
								e.setMetadata((byte) GUI_TITLE_HAS);
							}
							
							w.addElement(e);
							
							ix++;
						}
						
						Element fx = new PhantomElement(Material.BARRIER, new Slot(0, 6), C.GREEN + "Previous Page")
						{
							@Override
							public void onClick(Player p, Click c, Window w)
							{
								Titles.instance().getTitleController().setTitle(p, "");
								sender.sendMessage(C.GREEN + "Poof! It's gone.");
								w.close();
							}
						};
						
						w.addElement(fx);
						
						if(!first)
						{
							Element f = new PhantomElement(Material.SLIME_BALL, new Slot(-4, 6), C.GREEN + "Previous Page")
							{
								@Override
								public void onClick(Player p, Click c, Window w)
								{
									wins.get(v - 2).open();
								}
							};
							
							w.addElement(f);
						}
						
						if(!last)
						{
							Element f = new PhantomElement(Material.SLIME_BALL, new Slot(4, 6), C.GREEN + "Next Page")
							{
								@Override
								public void onClick(Player p, Click c, Window w)
								{
									wins.get(v).open();
								}
							};
							
							w.addElement(f);
						}
						
						wins.add(w);
					}
					
					wins.get(0).open();
				}
				
				return true;
			}
			
			return true;
		}
		
		return false;
	}
	
	private void queue(Command cmd, String[] args, String name)
	{
		String c = cmd.getName() + " " + new GList<String>(args).toString(" ");
		queued.add(name + ";;" + c);
	}
	
	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		for(String i : queued.copy())
		{
			try
			{
				String n = i.split(";;")[0];
				
				if(n.equals(e.getPlayer().getName()))
				{
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), i.split(";;")[1]);
					s("Dispatched Title > /" + i.split(";;")[1]);
					queued.remove(i);
				}
			}
			
			catch(Exception ex)
			{
				
			}
		}
	}
	
	public int getColorIndexAfter(int i)
	{
		if(i < C.values().length - 1)
		{
			return i + 1;
		}
		
		return 0;
	}
	
	public void hr(Player p)
	{
		p.sendMessage(C.UNDERLINE.toString() + C.DARK_GRAY + "                                    ");
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
		return "lang";
	}
	
	@Override
	public void onStart()
	{
		
	}
	
	@Override
	public void onStop()
	{
		
	}
}
