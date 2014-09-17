package net.scratchforfun.xioco;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.scratchforfun.debugg.Debug;
import net.scratchforfun.xioco.PlayerInfo.BannedPlayer;
import net.scratchforfun.xioco.PlayerInfo.BannedPlayer.Reason;
import net.scratchforfun.xioco.clock.Clock;
import net.scratchforfun.xioco.clock.Clock.Date;
import net.scratchforfun.xioco.threads.GodThread;
import net.scratchforfun.xioco.threads.ThreadTPA;
import no.xioco.commands.*;
import no.xioco.listeners.BlockListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class XioCo extends JavaPlugin{

	//public static XioCo instance;
	
	//private GuestReminder guestReminder = new GuestReminder();
	//private BlockListener blockListener = new BlockListener();
	private ChatListener chatListener = new ChatListener();
	//private LoginListener loginListener = new LoginListener();
	//private AdminStick adminStickListener = new AdminStick();
	
	private ThreadTPA threadTPA;
	private GodThread threadGod;
	XioCo plugin;
	//public AutoRestart autoRestart;
	
	public static String adminShopPassword;
	public static boolean DEBUG = true; 
	
	public void onEnable(){
		//instance = this;		
		
		// The folder all the config files are placed in!
		File configFile = new File("configs");	
		
		// If this folder does not exist, create it!
		if(!configFile.exists()) configFile.mkdir();
		
		// Initializes all the config files! If they do not exist it creates them!
		// Must be done before SQL!
		Configs.initializeFiles(configFile);
		
		
		// Reads password++ from the config
		SQLConnection.grabInformation(Configs.sqlFile);
		
		// Logs inn
		SQLConnection.openConnection(true);
		
		
		// Initializes BlockProtection SQL connections
		new BlockProtection();

		// Initializes Blacklist
		new BlockProtection.Blacklist(Configs.blockProtectionBlacklistFile);
		
		// Initializes Group SQL connections
		new Group();
		
		// Initializes the tpa thread
		threadTPA = new ThreadTPA();
		
		// Initializes the god thread
		threadGod = new GodThread();
		
		// Reads all the spam words
		chatListener.getBadWords();
		
		// Grabs the AdminShop config values
		adminShopPassword = Configs.getValue(Configs.chestShopFile, "adminShop");
		
		// Reads the helpop config
		Helpop.configHelpop();
				
		
		// Registers the listeners
        System.out.println("Initialiserer events");
        registerListeners();
		//Commands
        System.out.println("Initialiserer kommandoer");
        getCommand("helpop").setExecutor(new HelpOpCommand());
        getCommand("helplist").setExecutor(new HelpListCommand(this));
        getCommand("chat").setExecutor(new ChatCommand(this));
        getCommand("open").setExecutor(new OpenInvCommand());
        getCommand("c").setExecutor(new StabchatCommand());
        getCommand("bc").setExecutor(new BroadcastCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        // Creates the broadcaster
		//new XioCoBroadcaster();
		//autoRestart = new AutoRestart(this);
		//guestReminder.thread.start();
	}

	public void onDisable(){
		//Closes of the SQL connection | prob. not necessary :/
		SQLConnection.closeConnection();
		
		// Closes the broadcaster thread
		//XioCoBroadcaster.closeThread();
		//autoRestart.closeThread();
		//guestReminder.thread.stop();
		threadTPA.stop();
		threadGod.stop();
	}
	
	public void registerListeners(){
		PluginManager manager = getServer().getPluginManager();
		
		manager.registerEvents(new BlockListener(), this);
		manager.registerEvents(new GreifListener(), this);
		manager.registerEvents(chatListener, this);
		manager.registerEvents(new AdminStick(), this);
		manager.registerEvents(new LoginListener(), this);
	}
	
	public static ItemStack createItem(ItemStack material, String name, List<String> lore){
		if(material == null || material.getType() == Material.AIR || (name == null && lore == null)) return null;
		
		ItemMeta im = material.getItemMeta();
		
		if(name != null) im.setDisplayName(name);
		if(lore != null) im.setLore(lore);
		material.setItemMeta(im);
		
		return material;
	}
	
	/*
	public static void askQuestion(Player player, String question){
		IChatBaseComponent comp = ChatSerializer
	            .a("{\"text\":\"" + question + "\", "+
		
	            "\"extra\":[{\"text\":\"" + " (Trykk: " + ChatColor.GREEN + "Ja" + 
	            "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + 
	            "/green" + "\"}}" +
	            
	            ", " + "{\"text\":\"" + " - " + "\"}" + ", " +
	            
	            "{\"text\":\"" + ChatColor.RED + "Nei" + ChatColor.WHITE + ")" + 
	            "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + 
	            "/red" + "\"}}]" + 
		
	            "}");
		
		PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}*/
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Debug debug = Debug.start();
		command:{
			if(sender instanceof Player){
				Player player = (Player) sender;
				PermissionUser user = PermissionsEx.getUser(player);
    if(cmd.getName().equalsIgnoreCase("login")){
					//Needs permission
					if(user.has("xioco.login")){
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						if(playerInfo.loginInfo.equalsIgnoreCase("true")) playerInfo.loginInfo = "false";
						else playerInfo.loginInfo = "true";
						
						playerInfo.writePlayerInfo();
						player.sendMessage(ChatColor.GREEN + "Suksess! Loggin notifikasjoner " + (playerInfo.loginInfo.equalsIgnoreCase("true")?"aktivert!":"deaktivert!"));	
						break command;
					}else{
						sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
						break command;
					}
				}else if(cmd.getName().equalsIgnoreCase("work")){
					//Needs permission
					if(user.has("xioco.work")){
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						//If you are not in workmode
						if(playerInfo.inventory.equals("")){
							//Saves your inventory
							playerInfo.grabInventory(player);
							
							//Clears inventory for the work items
							PlayerInventory inventory = player.getInventory();
							for(int i = 0; i < inventory.getSize(); i++){
								inventory.setItem(i, null);
							}
							if(user.has("xioco.creativeaccess")){
								//Adds the work items
								inventory.setItem(0, WorkMode.ADMINSTICK);
								inventory.setItem(1, WorkMode.TELEPORTER);
								inventory.setItem(2, WorkMode.WOODENAXE);
								inventory.setItem(3, WorkMode.TOOL);
								inventory.setItem(4, WorkMode.WATER);
								inventory.setItem(5, WorkMode.LAVA);
								inventory.setItem(6, WorkMode.FIRE);
								inventory.setItem(7, WorkMode.BEDROCK);
								if(user.has("xioco.addexio")) inventory.setItem(8, WorkMode.MIDLERTIDIG);
	
								Bukkit.dispatchCommand(getServer().getConsoleSender(), "gamemode 1 " + player.getName());					
							}else{
								player.setSaturation(10000);
								player.setFoodLevel(1000);
								player.setAllowFlight(true);
								
								ItemStack ADMINSTICK = WorkMode.ADMINSTICK;
								ItemStack TELEPORTER = WorkMode.TELEPORTER;
								ItemStack WOODENAXE = WorkMode.WOODENAXE;
								ItemStack TOOL = WorkMode.TOOL;
								ItemStack WATER = WorkMode.WATER;
								ItemStack LAVA = WorkMode.LAVA;
								ItemStack FIRE = WorkMode.FIRE;
								ItemStack BEDROCK = WorkMode.BEDROCK;
								
								ADMINSTICK.setAmount(-1);
								TELEPORTER.setAmount(-1);
								WOODENAXE.setAmount(-1);
								TOOL.setAmount(-1);
								WATER.setAmount(-1);
								LAVA.setAmount(-1);
								FIRE.setAmount(-1);
								BEDROCK.setAmount(-1);
								
								//Adds the work items
								inventory.setItem(0, ADMINSTICK);
								inventory.setItem(1, TELEPORTER);
								inventory.setItem(2, WOODENAXE);
								inventory.setItem(3, TOOL);
								inventory.setItem(4, WATER);
								inventory.setItem(5, LAVA);
								inventory.setItem(6, FIRE);
								inventory.setItem(7, BEDROCK);
							}
						}else{
							//player.setSaturation(100);
							player.setFoodLevel(20);
							playerInfo.addItemsToPlayer(player);
							player.setAllowFlight(false);
							Bukkit.dispatchCommand(getServer().getConsoleSender(), "gamemode 0 " + player.getName());	
						}
						
						//Updates the SQL Database
						playerInfo.writePlayerInfo();
						player.sendMessage(((playerInfo.inventory==null || !playerInfo.inventory.equals(""))?ChatColor.GREEN+"Du er n� i work!":ChatColor.RED+"Du er n� ute av work!"));	
						break command;
					}else{
						sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
						break command;
					}
				}else if(cmd.getName().equalsIgnoreCase("del")){
					if(user.has("xioco.del")){
						if(args.length == 1){
							WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
							String accusedPlayer = getServer().getOfflinePlayer(args[0]).getUniqueId().toString();
							if(worldEdit != null && accusedPlayer != null){						
								Selection selection = worldEdit.getSelection(player);
								
								World world = selection.getWorld();
								Location a = selection.getMinimumPoint();
								Location b = selection.getMaximumPoint();
								
								int amountOfBlocksRemoved = 0;
								
								for(int x = a.getBlockX(); x <= b.getBlockX(); x++){
									for(int y = a.getBlockY(); y <= b.getBlockY(); y++){
										for(int z = a.getBlockZ(); z <= b.getBlockZ(); z++){
											Block block = world.getBlockAt(x, y, z);
											
											//Make sure you are not grabbing air!
											if(block != null){
												ProtectedBlock pBlock = BlockProtection.getProtectedBlock(x, y, z);
												if(pBlock != null && accusedPlayer.equals(pBlock.UUID)){
													BlockProtection.updateProtection(player, false, block);
													block.setType(Material.AIR);		
													amountOfBlocksRemoved++;
												}
												
												/* TODO ProtectedBlock[] blocks = blockProtection.getProtectedBlock(player.getName(), x, y, z, 1);
											
												if(blocks.length > 0 && accusedPlayer.equals(blocks[blocks.length-1].UUID)){
													blockProtection.removeBlock(player.getName(), player.getUniqueId().toString(), true, block);
													block.setType(Material.AIR);		
													amountOfBlocksRemoved++;
												}*/
											}
										}
									}
								}
								
								player.sendMessage(ChatColor.GREEN + "Fjernet " + amountOfBlocksRemoved + " blokker!");
								break command;
							}else{
								if(worldEdit == null)player.sendMessage(ChatColor.RED + "WorldEdit er ikke installert p� denne serveren! Mener du dette er en feil kontakt staff!");
								if(accusedPlayer == null)player.sendMessage(ChatColor.RED + "Det er ingen spiller med dette navnet!");
							}
						}					
					}else{
						sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
						break command;
					}
				}else if(cmd.getName().equalsIgnoreCase("setowner")){
					// Checks to see if the player has the required permission
					if(user.has("xioco.protect")){
						// Make sure the player has written the comment correctly 
						if(args.length == 1){
							// Grabs the world edit instance
							WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
								
							// Makes sure the server uses world edit
							if(worldEdit != null){
								// Sets the default remover/placer to yourself
								Player nPlayer = player;
								
								// Sets the default place/remove value to place
								boolean placed = true;
								
								// If the argument equals 'null' then rather remove protection
								if(args[0].equalsIgnoreCase("null")) placed = false;
								
								// If not grab the player in question
								else nPlayer = Bukkit.getPlayer(args[0]);
								
								// Make sure the player exists
								if(nPlayer != null){
									// Grabs the player's selection
									Selection selection = worldEdit.getSelection(player);
										
									// Makes sure the player actually has selected something
									if(selection != null){
										// Grabs the world in question
										World world = selection.getWorld();
										
										// Grabs the points in question
										Location a = selection.getMinimumPoint();
										Location b = selection.getMaximumPoint();
										
										// Variable to describe amount of blocks removed/changed
										int amountOfBlocksRemoved = 0;
										
										// Loops through all the blocks
										for(int x = a.getBlockX(); x <= b.getBlockX(); x++){
											for(int y = a.getBlockY(); y <= b.getBlockY(); y++){
												for(int z = a.getBlockZ(); z <= b.getBlockZ(); z++){
													// Grabs the block at set x, y, z
													Block block = world.getBlockAt(x, y, z);
												
													// Makes sure its not air your setting
													if(!block.getType().equals(Material.AIR)){
														// Updates the protection based on player and placed/remove
														BlockProtection.updateProtection(nPlayer, placed, block);
														
														// Updates the amount of blocks changed
														amountOfBlocksRemoved++;
													}
												}
											}
										}
									
										// If you are removing protection
										// It lets the player know how many the command removed
										if(nPlayer == player) player.sendMessage(ChatColor.GREEN + "Fjernet " + amountOfBlocksRemoved + " blokker!");
										
										// If you are changing protection
										// It lets you know how many blocks you changed protection on
										else player.sendMessage(ChatColor.GREEN + "Ga " + amountOfBlocksRemoved + " blokker til " + nPlayer.getName() + "!");
										
										// Closes off the stuff
										break command;
									}else{
										// Let's the player know it needs to set points with world edit first
										player.sendMessage(ChatColor.RED + "Velg to punkter med WorldEdit f�rst.");
									}
								}else{
									// Let's the player know if the targeted player does not exist
									player.sendMessage(ChatColor.RED + "Denne spilleren eksisterer ikke");
								}
								/*
								Player online = getServer().getPlayer(args[0]);
								String accusedPlayer = null;
								
								if(online != null) accusedPlayer = online.getUniqueId().toString();
								
								if(accusedPlayer == null){
									for(OfflinePlayer offline : getServer().getOfflinePlayers()){
										if(offline.getName().equalsIgnoreCase(args[0])) accusedPlayer = offline.getUniqueId().toString();
									}
								}
								
								if(accusedPlayer != null){						
									Selection selection = worldEdit.getSelection(player);
									
									if(selection != null){
										World world = selection.getWorld();
										Location a = selection.getMinimumPoint();
										Location b = selection.getMaximumPoint();
										
										int amountOfBlocksRemoved = 0;
										
										for(int x = a.getBlockX(); x <= b.getBlockX(); x++){
											for(int y = a.getBlockY(); y <= b.getBlockY(); y++){
												for(int z = a.getBlockZ(); z <= b.getBlockZ(); z++){
													Block block = world.getBlockAt(x, y, z);
												
													if(!block.getType().equals(Material.AIR)){
														BlockProtection.updateProtection(player, true, block);
														//TODO blockProtection.protectBlock(accusedPlayer, block);		
														amountOfBlocksRemoved++;
													}
												}
											}
										}
										
										player.sendMessage(ChatColor.GREEN + "Ga " + amountOfBlocksRemoved + " blokker til " + args[0] + "!");
									}else{
										player.sendMessage(ChatColor.RED + "Velg to punkter med WorldEdit f�rst.");
									}
									
									break command;
								}*/
							}else{
								player.sendMessage(ChatColor.RED + "WorldEdit er ikke installert p� denne serveren! Mener du dette er en feil kontakt staff!");
								break command;
							}
						}else{
							player.sendMessage(ChatColor.RED + "Bruk /setowner NAVN");
							break command;
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
						break command;
					}
				}else if(cmd.getName().equalsIgnoreCase("m") || cmd.getName().equalsIgnoreCase("w")){				
					if(args.length >= 2){
						Player accusedPlayer = getServer().getPlayer(args[0]);
						if(accusedPlayer != null){		
							String text = "";
							
							if(args.length > 1){
								for(int i = 1; i < args.length; i++){
									text+=args[i];
									if(i != args.length-1) text+=" ";
								}
							}
							
							// Sends a message to the accused player
							message(player, accusedPlayer, text);
							
							// Sends a spy message to everyone who wants to read it
							spy(player, accusedPlayer, text);
							
							//Updates the SQL
							PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(accusedPlayer);
							playerInfo.message = player.getUniqueId().toString();
							playerInfo.writePlayerInfo();
							break command;
						}else{
							if(accusedPlayer == null)player.sendMessage(ChatColor.RED + args[0] + " er ikke online.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Bruk /m BRUKER TEKST | /w BRUKER TEKST");
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("r")){				
					if(args.length >= 1){
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						if(playerInfo.message != null){
							Player accusedPlayer = null;
							for(Player online : Bukkit.getOnlinePlayers()){
								if(online.getUniqueId().toString().equals(playerInfo.message)) accusedPlayer = online;
							}
							
							if(accusedPlayer != null){
								String text = "";
								
								
								for(int i = 0; i < args.length; i++){
									text+=args[i];
									if(i != args.length-1) text+=" ";
								}
								
								// Sends a message to the accused player
								message(player, accusedPlayer, text);
								
								// Sends a spy message to everyone who wants to read it
								spy(player, accusedPlayer, text);
								
								//Updates the SQL
								PlayerInfo accusedPlayerInfo = PlayerInfo.getPlayerInfo(accusedPlayer);
								accusedPlayerInfo.message = player.getUniqueId().toString();
								accusedPlayerInfo.writePlayerInfo();
							}else{
								player.sendMessage(ChatColor.RED + "Spiller ikke online.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Du har ingen meldinger � svare p�.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Bruk /r TEKST");
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("konto")){				
					player.sendMessage(ChatColor.GREEN + "Du har " + ChatColor.GOLD + PlayerInfo.getPlayerInfo(player).gull + " gull " + ChatColor.GREEN + "p� konto.");
					break command;
				}else if(cmd.getName().equalsIgnoreCase("inn")){	
					if(args.length == 1){
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						int antall = 0;
						
						try{
							antall = Integer.parseInt(args[0]);
						}catch(Exception e){
							player.sendMessage(ChatColor.RED + "Bruk /inn ANTALL");
							break command;
						}
						
						// TODO / HOTFIX: Don't accept values of zero or below.
						if(antall <= 0) {
							player.sendMessage(ChatColor.RED + "Du m� skrive et litt st�rre tall.");
							break command;
						}
						
						boolean enoughItems = player.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), antall);
						
						if(!enoughItems){
							player.sendMessage(ChatColor.RED + "Du har ikke nok gull-barer p� deg.");	
							break command;
						}
						
						playerInfo.gull+=antall;	
						player.getInventory().removeItem(new ItemStack[] {new ItemStack(Material.GOLD_INGOT, antall)});					
						
						//Updates the SQLe
						playerInfo.writePlayerInfo();
						player.sendMessage(ChatColor.GREEN + "Du har satt " + ChatColor.GOLD + antall + " gull " + ChatColor.GREEN + "p� konto, og har n� " + ChatColor.GOLD + PlayerInfo.getPlayerInfo(player).gull + " gull " + ChatColor.GREEN + " p� konto.");
					}else{
						player.sendMessage(ChatColor.RED + "Bruk /inn ANTALL");					
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("ut")){	
					if(args.length == 1){
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						int antall = 0;
						
						try{
							antall = Integer.parseInt(args[0]);
						}catch(Exception e){
							player.sendMessage(ChatColor.RED + "Bruk /ut ANTALL");
							break command;
						}
						
						// TODO / HOTFIX: Don't accept values of zero or below.
						if(antall <= 0) {
							player.sendMessage(ChatColor.RED + "Du m� skrive et litt st�rre tall.");
							break command;
						}
						
						if(antall > playerInfo.gull){
							player.sendMessage(ChatColor.RED + "Du har ikke nok gull p� konto.");	
							break command;
						}
						
						int spaceForGold = 0;
						for(ItemStack itemstack : player.getInventory().getContents()){
							if(itemstack != null && itemstack.getType().equals(Material.GOLD_INGOT)){
								spaceForGold += 64-itemstack.getAmount();
							}
							
							if(itemstack == null || itemstack.getType().equals(Material.AIR)) spaceForGold += 64;
						}
						
						
						
						if(spaceForGold < antall){
							player.sendMessage(ChatColor.RED + "Du har ikke nok plass i inventory.");	
							break command;
						}
						
						playerInfo.gull-=antall;	
						player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT, antall)});				
						
						//Updates the SQL
						playerInfo.writePlayerInfo();
						player.sendMessage(ChatColor.GREEN + "Du har tatt ut " + ChatColor.GOLD + antall + " gull " + ChatColor.GREEN + "fra konto.");
					}else{
						player.sendMessage(ChatColor.RED + "Bruk /ut ANTALL");					
					}
					
					break command;
				}/*else if(cmd.getName().equalsIgnoreCase("betal")){	
					if(args.length == 2){
						String UUID = null;
						if(Bukkit.getPlayer(args[0]) != null) UUID = Bukkit.getPlayer(args[0]).getUniqueId().toString();
						if(UUID == null) UUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
						
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						PlayerInfo accusedPlayerInfo = PlayerInfo.getPlayerInfo(UUID);
						int antall = 0;
						
						try{
							antall = Integer.parseInt(args[1]);
						}catch(Exception e){
							player.sendMessage(ChatColor.RED + "Bruk /betal SPILLER ANTALL");
							break command;
						}
						
						if(antall > playerInfo.gull){
							player.sendMessage(ChatColor.RED + "Du har ikke nok gull p� konto.");	
							break command;
						}
						
						playerInfo.gull-=antall;
						accusedPlayerInfo.gull+=antall;
	
						//Updates the SQL
						PlayerInfo.writePlayerInfo(playerInfo);
						PlayerInfo.writePlayerInfo(accusedPlayerInfo);
						player.sendMessage(ChatColor.GREEN + "Du har overf�rt " + ChatColor.GOLD + antall + " gull " + ChatColor.GREEN + "til " + args[0]);
						
						//If player is online
						if(Bukkit.getPlayer(args[0]) != null){
							Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GREEN + "Du har f�tt " + ChatColor.GOLD + antall + " gull " + ChatColor.GREEN + "fra " + player.getName());
						}
					}else{
						player.sendMessage(ChatColor.RED + "Bruk /betal SPILLER ANTALL");						
					}
					
					break command;
				}*/else if(cmd.getName().equalsIgnoreCase("socialspy")){	
					if(user.has("xioco.socialspy")){
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
						if(playerInfo.socialSpy.equals("true")) playerInfo.socialSpy = "false";
						else playerInfo.socialSpy = "true";
						
						//Updates the SQL
						playerInfo.writePlayerInfo();
						player.sendMessage(playerInfo.socialSpy.equals("true")?(ChatColor.GREEN+"Du er n� i SocialSpy!"):(ChatColor.RED+"Du er n� ute av SocialSpy!"));
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");			
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("setspawn")){	
					if(user.has("xioco.setspawn")){
						Location spawn = player.getLocation();
						player.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
						player.sendMessage(ChatColor.GREEN + "Nytt spawn satt i verden " + player.getWorld().getName() + "!");
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("tp")){	
					if(user.has("xioco.tp")){
						if(args.length > 0){
							Player target = getServer().getPlayer(args[0]);
							
							if(target != null){
								if(args.length == 2 && args[1].equalsIgnoreCase(player.getName())){
									target.teleport(player);
									player.sendMessage(ChatColor.GRAY + "Teleporting " + target.getDisplayName() + " to " + player.getDisplayName() + "!");
									target.sendMessage(ChatColor.GRAY + "Teleporting " + target.getDisplayName() + " to " + player.getDisplayName() + "!");
								}else{				
									player.teleport(target);
									player.sendMessage(ChatColor.GRAY + "Teleporting " + player.getDisplayName() + " to " + target.getDisplayName() + "!");
									target.sendMessage(ChatColor.GRAY + "Teleporting " + player.getDisplayName() + " to " + target.getDisplayName() + "!");
								}
							}else{
								sender.sendMessage(ChatColor.RED + "Spiller ikke funnet!");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Bruk /tp NAVN | /tp NAVN DITTNAVN");			
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("tpa")){
					if(user.has("xioco.tpa")){
						ThreadTPA.PlayerTPA tpa = ThreadTPA.getTPARequest(player);
						
						if(args.length == 1){
							Player target = getServer().getPlayer(args[0]);
							
							if(target != null){
								if(tpa == null){
									ThreadTPA.addTPAPlayer(player, target);
									player.sendMessage(ChatColor.GREEN + "Du har bedt om � teleportere til " + target.getDisplayName());
									target.sendMessage(ChatColor.GREEN + player.getDisplayName() + " �nsker � teleportere til deg. Skriv " + ChatColor.GOLD + "/tpa " + ChatColor.GREEN + "for � godta (innen 60 sekunder).");
								}else{
									player.sendMessage(ChatColor.RED + "Denne spilleren har allerede en foresp�rsel � svare p�. vent " + tpa.getTime() + " sekunder.");
								}
							}else{	
								sender.sendMessage(ChatColor.RED + "Spiller ikke funnet!");
							}
						}else if(args.length == 0){
							if(tpa != null){
								// TODO DEPRECATED RequestTPA tpa = ThreadTPA.threads.get(player);
								Player requester = tpa.requester;
								requester.teleport(player);
								ThreadTPA.remove(tpa);
								
								System.out.println(ThreadTPA.getSize());
								
								player.sendMessage(ChatColor.GREEN + "Du har blitt teleportert til " + requester.getDisplayName()+".");
							}else{
								player.sendMessage(ChatColor.RED + "Du har ingen foresp�rsler. Bruk /tpa NAVN");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Bruk /tp NAVN | /tp NAVN DITTNAVN");			
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("spawn")){	
					player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					player.sendMessage("Teleporting...");
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("sethome")){
					if(user.has("xioco.home")){
						if(args.length == 1){
							if(args[0].matches("[a-zA-Z0-9]+")){
								PlayerInfo info = PlayerInfo.getPlayerInfo(player);
								if(info.getHomeName(1) == null || info.getHomeName(1).equals("")){
									info.setHome(1, args[0], player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
									player.sendMessage(ChatColor.GREEN+"Hjem 1. "+args[0]+" er satt!");
								}else if(info.getHomeName(2) == null || info.getHomeName(2).equals("")){
									info.setHome(2, args[0], player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
									player.sendMessage(ChatColor.GREEN+"Hjem 2. "+args[0]+" er satt!");
								}else if(info.getHomeName(3) == null || info.getHomeName(3).equals("")){
									info.setHome(3, args[0], player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
									player.sendMessage(ChatColor.GREEN+"Hjem 3. "+args[0]+" er satt!");
								}else{
									player.sendMessage(ChatColor.RED + "Du har ikke flere ledige hjem.");
									player.sendMessage(ChatColor.RED + "Slett et hjem med /delhome NAVN eller /delhome 1-3 og pr�v igjen.");
								}
		
								info.writePlayerInfo();
							}else{
								player.sendMessage(ChatColor.RED + "Du kan kun bruke bokstaver og tall.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Bruk /sethome NAVN");	
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("home")){	
					if(user.has("xioco.home")){
						PlayerInfo info = PlayerInfo.getPlayerInfo(player);
						
						if(args.length == 1){
							Home home = null;
							String name = "";
							if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("3")){
								int i = Integer.parseInt(args[0]);
								home = info.getHome(i);
								name = info.getHomeName(i);
							}else{
								home = info.getHome(args[0]);
								name = info.getHomeName(args[0]);
							}
							
							if(name != null && !name.equals("")){
								player.sendMessage(ChatColor.GREEN + "Teleporterer til " + name+"!");
								player.teleport(home.getHome());
								
								WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
								worldEdit.wrapPlayer(player).findFreePosition();							
							}else{
								player.sendMessage("Finner ikke hjem.");
							}						
						}else{
							player.sendMessage(ChatColor.GOLD + "Dine hjem:");	
							if(info.home1 != null && !info.home1.equals("")) player.sendMessage(ChatColor.GREEN + "1. " + info.home1);	
							if(info.home2 != null && !info.home2.equals("")) player.sendMessage(ChatColor.GREEN + "2. " + info.home2);	
							if(info.home3 != null && !info.home3.equals("")) player.sendMessage(ChatColor.GREEN + "3. " + info.home3);	
							if((info.home1 == null || info.home1.equals("")) && (info.home2 == null || info.home2.equals("")) && (info.home3 == null || info.home3.equals(""))) player.sendMessage(ChatColor.RED + "Du har ingen hjem.");	
							player.sendMessage(ChatColor.GREEN + "Bruk "+ChatColor.GOLD+"/home NAVN"+ChatColor.GREEN + " eller "+ChatColor.GOLD+"/home 1-3"+ChatColor.GREEN + ", for � teleportere.");	
							player.sendMessage(ChatColor.GREEN + "Bruk "+ChatColor.GOLD+"/sethome NAVN"+ChatColor.GREEN + " for � sette et hjem.");	
							player.sendMessage(ChatColor.RED + "Bruk "+ChatColor.GOLD+"/delhome NAVN"+ChatColor.RED + " eller "+ChatColor.GOLD+"/delhome 1-3"+ChatColor.RED + ", for � fjerne hjem.");	
						}
	
						info.writePlayerInfo();
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("homes")){	
					if(user.has("xioco.home")){
						PlayerInfo info = PlayerInfo.getPlayerInfo(player);
						
						player.sendMessage(ChatColor.GOLD + "Dine hjem:");	
						if(info.home1 != null && !info.home1.equals("")) player.sendMessage(ChatColor.GREEN + "1. " + info.home1);	
						if(info.home2 != null && !info.home2.equals("")) player.sendMessage(ChatColor.GREEN + "2. " + info.home2);	
						if(info.home3 != null && !info.home3.equals("")) player.sendMessage(ChatColor.GREEN + "3. " + info.home3);	
						if((info.home1 == null || info.home1.equals("")) && (info.home2 == null || info.home2.equals("")) && (info.home3 == null || info.home3.equals(""))) player.sendMessage(ChatColor.RED + "Du har ingen hjem.");	
						player.sendMessage(ChatColor.GREEN + "Bruk "+ChatColor.GOLD+"/home NAVN"+ChatColor.GREEN + " eller "+ChatColor.GOLD+"/home 1-3"+ChatColor.GREEN + ", for � teleportere.");	
						player.sendMessage(ChatColor.RED + "Bruk "+ChatColor.GOLD+"/delhome NAVN"+ChatColor.RED + " eller "+ChatColor.GOLD+"/delhome 1-3"+ChatColor.RED + ", for � fjerne hjem.");	
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("delhome")){	
					if(user.has("xioco.home")){
						PlayerInfo info = PlayerInfo.getPlayerInfo(player);
						
						if(args.length == 1){
							if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("3")){
								int i = Integer.parseInt(args[0]);
								info.delHome(i);
								player.sendMessage(ChatColor.GREEN + "Hjem slettet.");
							}else{
								int i = info.getHomeNr(args[0]);
								if(i != 0){
									info.delHome(i);
									player.sendMessage(ChatColor.GREEN + "Hjem slettet.");
								}else player.sendMessage(ChatColor.RED + "Finner ikke hjem.");
							}					
						}
	
						info.writePlayerInfo();
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("debug")){	
					if(user.has("xioco.debug")){
						if(args.length == 0){
							if(Debug.update(player.getName())){
								player.sendMessage(ChatColor.RED + "Du er n� i debug mode.");								
							}else{
								player.sendMessage(ChatColor.RED + "Du er n� ute av debug mode.");		
							}
						}
						if(args.length == 2){
							if(args[0].equalsIgnoreCase("cap")){
								try{
									int cap = Integer.parseInt(args[1]);
									
									if(!Debug.update(player.getName(), cap)){
										player.sendMessage(ChatColor.RED + "Du er ikke i debug mode.");
									}
								}catch(NumberFormatException e){
									player.sendMessage(ChatColor.RED + "bruk /debug cap TALL");
								}
							}
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("tppos")){	
					if(user.has("xioco.tppos")){
						if(args.length == 3){
							try{
								int x = Integer.parseInt(args[0]);
								int y = Integer.parseInt(args[1]);
								int z = Integer.parseInt(args[2]);
								
								player.teleport(new Location(player.getWorld(), x, y, z));
								player.sendMessage(ChatColor.GRAY+"Teleporterer...");
							}catch(Exception e){
								player.sendMessage(ChatColor.RED+"Bruk /tppos X Y Z");
							}
						}else{
							player.sendMessage(ChatColor.RED+"Bruk /tppos X Y Z");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("gruppe")){	
					if(user.has("xioco.gruppe")){
						PlayerInfo info = PlayerInfo.getPlayerInfo(player);		
						if(args.length == 0){
							player.sendMessage(ChatColor.GREEN + "Lag en gruppe med "+ChatColor.GOLD+"/gruppe LAG NAVN");	
							player.sendMessage(ChatColor.GREEN + "Inviter en spiller til gruppen din med "+ChatColor.GOLD+"/gruppe INVITER NAVN");
							player.sendMessage(ChatColor.GREEN + "Godta en gruppe invitasjon med "+ChatColor.GOLD+"/gruppe GODTA");
							player.sendMessage(ChatColor.GREEN + "Forlat gruppen din med "+ChatColor.GOLD+"/gruppe FORLAT");		
							player.sendMessage(ChatColor.GREEN + "Kast en spiller ut av gruppen din med "+ChatColor.GOLD+"/gruppe KICK NAVN");		
						}else if(args.length == 1){
							if(args[0].equalsIgnoreCase("forlat")){
								if(!info.group.equals("")){
									player.sendMessage(ChatColor.GREEN + "Du er ikke lengre i gruppen " + info.group + ".");	
									info.group = "";
								}else{
									player.sendMessage(ChatColor.RED + "Du er ikke i en gruppe.");									
								}
							}else if(args[0].equalsIgnoreCase("godta")){
								if(info.group.equals("")){
									if(!info.groupInvite.equals("")){
										info.group = info.groupInvite;
										info.groupLeder = "false";
										player.sendMessage(ChatColor.GREEN + "Du godtok invitasjonen til gruppen " + info.group + ".");	
										info.groupInvite = "";
									}else{
										player.sendMessage(ChatColor.RED + "Du har ingen gruppe invitasjoner.");									
									}
								}else{
									player.sendMessage(ChatColor.RED + "Du er allerede i en gruppe.");
								}
							}						
						}else if(args.length == 2){
							if(args[0].equalsIgnoreCase("inviter")){
								if(!info.group.equals("")){
									if(info.groupLeder.equals("true")){
										OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
										if(offline != null){
											PlayerInfo offline_info = PlayerInfo.getPlayerInfo(offline.getUniqueId().toString());
											offline_info.groupInvite = info.group;
											
											offline_info.writePlayerInfo();
											player.sendMessage(ChatColor.GREEN+"Spilleren har mottatt en invitasjon");
										}else{
											player.sendMessage(ChatColor.RED + "Denne spilleren finnes ikke.");
										}
									}else{
										player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter. Kontakt gruppe leder.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "Du er ikke i noen gruppe.");
								}
							}else if(args[0].equalsIgnoreCase("kick")){
								if(!info.group.equals("")){
									if(info.groupLeder.equals("true")){
										OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
										if(offline != null){
											PlayerInfo offline_info = PlayerInfo.getPlayerInfo(offline.getUniqueId().toString());
											offline_info.group = "";
											
											offline_info.writePlayerInfo();
											player.sendMessage(ChatColor.GREEN+"Spilleren er kastet ut av gruppen din");
										}else{
											player.sendMessage(ChatColor.RED + "Denne spilleren finnes ikke.");
										}
									}else{
										player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter. Kontakt gruppe leder.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "Du er ikke i noen gruppe.");
								}
							}else if(args[0].equalsIgnoreCase("lag")){
								if(!Group.doesExist(args[1])){
									if(info.group.equals("")){
										if(Group.legalCharacters(args[1])){
											info.group = args[1];
											info.groupLeder = "true";
											
											Group.createGroup(args[1]);
											player.sendMessage(ChatColor.GREEN + "Du har laget en gruppe med navn " + args[1] + ".");	
										}else{
											player.sendMessage(ChatColor.RED+"Du kan bare bruke bokstaver n�r du lager grupper.");
										}
									}else{
										player.sendMessage(ChatColor.RED + "Du er allerede i en gruppe.");									
									}
								}else{
									player.sendMessage(ChatColor.RED + "Det finnes allerede en gruppe med dette navnet.");
								}
							}
						}
						
						info.writePlayerInfo();
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("hide")){	
					if(user.has("xioco.hide")){
						for(Player online : Bukkit.getOnlinePlayers()){
							online.hidePlayer(player);
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("god")){	
					if(user.has("xioco.god")){
						if(args.length == 1){
							try{
								int seconds = Integer.parseInt(args[0]);
								
								GodThread.GodPlayer god = GodThread.getGod(player);
								if(god == null){
									
									player.sendMessage(ChatColor.GREEN + "Du er n� i god (" + GodThread.addGod(player, seconds).getTime() + ")");
								}else{
									player.sendMessage(ChatColor.RED + "Du er allerede i god (" + god.getTime() + ")");
								}
							}catch(Exception e){
								player.sendMessage(ChatColor.RED + "Bruk /god SEKUNDER");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Bruk /god SEKUNDER");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("fixit")){	
					final Player player_ = player;
					if(user.has("xioco.fixit")){
						final int cap = 1000;
						final int pauseTime = 1000;
						WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
						
						if(worldEdit != null){						
							Selection selection = worldEdit.getSelection(player_);
							
							if(selection != null){
								World world = selection.getWorld();
								Location a = selection.getMinimumPoint();
								Location b = selection.getMaximumPoint();
								
								int amountOfBlocksRemoved = 0;	
								for(int x = a.getBlockX(); x <= b.getBlockX(); x++){
									for(int y = a.getBlockY(); y <= b.getBlockY(); y++){
										for(int z = a.getBlockZ(); z <= b.getBlockZ(); z++){
											Block block = world.getBlockAt(x, y, z);
											
											//Make sure you are grabbing air!
											//if(block.getType().equals(Material.AIR)){
											ProtectedBlock pBlock = BlockProtection.getProtectedBlock(x, y, z);

											amountOfBlocksRemoved++;
											if(amountOfBlocksRemoved >= cap){
												long time = System.currentTimeMillis();
												boolean pause = false;
												Bukkit.broadcastMessage(ChatColor.GREEN + "" + amountOfBlocksRemoved + " blocks fixed. pausing!");
												amountOfBlocksRemoved = 0;
												
												while(pause){
													pause = false;
													try {
														Thread.sleep(pauseTime);
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
												}

												Bukkit.broadcastMessage(ChatColor.RED + "Pause finished. Waited " + (System.currentTimeMillis()-time) + " milliseconds!");
											}
											
											// Make sure the block is protected
											if(pBlock != null){
												// Make sure the block has been placed
												if(pBlock.plasert.equals("true")){
													block.setType(Material.getMaterial(pBlock.blockName));
												}
											}
											//}
										}
									}
								}
								Bukkit.broadcastMessage(ChatColor.GOLD + "Finished fixing! " + amountOfBlocksRemoved + " blocks fixed!");
							}
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("shiftGenerate")){	
					final Player player_ = player;
					if(user.has("xioco.shiftGenerate")){
						try{
							final int amount = Integer.parseInt(args[0]);
							for(int i = 0; i < amount; i++){
								Bukkit.broadcastMessage(ChatColor.AQUA + "Round " + i + "!");
								Bukkit.dispatchCommand(player_, "/shift 2");	
								Bukkit.dispatchCommand(player_, "/regen");	
							}	
							Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "DONE");
						}catch(Exception e){
							player.sendMessage(ChatColor.RED + "Bruk /shiftGenerate ANTALL");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}else if(cmd.getName().equalsIgnoreCase("shiftFixit")){	
					final Player player_ = player;
					if(user.has("xioco.shiftFixit")){
						try{
							final int amount = Integer.parseInt(args[0]);
							for(int i = 0; i < amount; i++){
								Bukkit.broadcastMessage(ChatColor.AQUA + "Round " + i + "!");
								Bukkit.dispatchCommand(player_, "/shift 2");	
								Bukkit.dispatchCommand(player_, "fixit");	
							}	
							Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "DONE");
						}catch(Exception e){
							player.sendMessage(ChatColor.RED + "Bruk /shiftFixit ANTALL");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");	
					}
					
					break command;
				}/*else if(cmd.getName().equalsIgnoreCase("gny")){	
					PlayerInfo info = PlayerInfo.getPlayerInfo(player);
					if(info.group == null || info.group.equals("")){
						if(args.length == 1){
							if(args[0].length() <= 16){
								String[] badWords = chatListener.badWords;
								boolean containsFaultyWord = false;
								for(String word : badWords){
									if(args[0].contains(word)) containsFaultyWord = true;
								}
								
								if(!containsFaultyWord){
									if(PlayerInfo.Group.getGroup(args[0]) == null){
										info.group = args[0];
										info.groupLeder = true;
										PlayerInfo.Group.createGroup(args[0]);
										player.sendMessage(ChatColor.GREEN+"Gruppen " + args[0] + " har blitt opprettet. " + ChatColor.RED + "Du deler n� kister og blokker med alle i gruppen.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "Gruppen ble ikke opprettet. Ugyldig navn.");
								}
							}else{
								player.sendMessage(ChatColor.RED + "Gruppen ble ikke opprettet. Ugyldig navn.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Gruppen ble ikke opprettet. Ugyldig navn.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Gruppen ble ikke opprettet. Du er allerede i en gruppe.");
					}
					
					PlayerInfo.writePlayerInfo(info);
					break command;
				}else if(cmd.getName().equalsIgnoreCase("ginv")){	
					PlayerInfo info = PlayerInfo.getPlayerInfo(player);
					if(info.group != null && !info.group.equals("")){
						if(args.length == 1){
							if(info.groupLeder){
								Player acPlayer = Bukkit.getPlayer(args[0]);
								if(acPlayer != null){
									PlayerInfo accusedPlayer = PlayerInfo.getPlayerInfo(acPlayer);
									if(accusedPlayer != null){
										accusedPlayer.groupInvite = info.group;
										player.sendMessage(ChatColor.GREEN + "Du har blitt invitert til gruppen " + info.group + ". Skriv " + ChatColor.WHITE + "/ggodta " + ChatColor.GREEN + "for � bli med.");
									}else{
										player.sendMessage(ChatColor.RED + "Denne spilleren eksisterer ikke.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "Denne spilleren eksisterer ikke.");
								}
							}else{
								player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter. Ta kontakt med en gruppe leder");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Bruk /ginv NAVN");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du er ikke i en gruppe.");
					}
					
					PlayerInfo.writePlayerInfo(info);
					break command;
				}else if(cmd.getName().equalsIgnoreCase("ggodta")){	
					PlayerInfo info = PlayerInfo.getPlayerInfo(player);
					if(info.groupInvite != null && !info.groupInvite.equals("")){
						if(info.group == null || info.group.equals("")){	
							PlayerInfo.Group newgroup = PlayerInfo.Group.getGroup(info.groupInvite);
							if(newgroup != null){
								info.group = info.groupInvite;
								
								player.sendMessage(ChatColor.GREEN + "Du er n� i gruppen " + info.groupInvite+".");
								info.groupInvite = "";
							}else{
								player.sendMessage(ChatColor.RED + "Denne gruppen eksisterer ikke lengre.");
								info.groupInvite = "";
							}
						}else{
							player.sendMessage(ChatColor.RED + "Du er allerede i en gruppe.");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du er ikke invitert til noen gruppe.");
					}
					
					PlayerInfo.writePlayerInfo(info);
					break command;
				}else if(cmd.getName().equalsIgnoreCase("gop")){	
					PlayerInfo info = PlayerInfo.getPlayerInfo(player);
					if(info.group != null && !info.group.equals("")){
						if(info.groupLeder){
							if(args.length == 1){
								Player acPlayer = Bukkit.getPlayer(args[0]);
								if(acPlayer != null){
									PlayerInfo acInfo = PlayerInfo.getPlayerInfo(acPlayer);
									if(acInfo != null){
										acInfo.groupLeder = true;
									}else{
										player.sendMessage(ChatColor.RED + "Denne spilleren eksisterer ikke.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "Denne spilleren eksisterer ikke.");
								}
							}else{
								player.sendMessage(ChatColor.RED + "Bruk /gop NAVN");							
							}
							
						}else{
							player.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter. Ta kontakt med en gruppe leder");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Du er ikke i en gruppe.");
					}
					
					PlayerInfo.writePlayerInfo(info);
					break command;
				}*/
			}
			
			
			//NOT NEEDED TO BE DONE BY A PLAYER!
			if(cmd.getName().equalsIgnoreCase("unban")){
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.unban");
				}else{
					permission = true;
				}
				
				if(permission){
					if(args.length == 1){
						String UUID = null;
						for(OfflinePlayer player : getServer().getOfflinePlayers()){
							if(player.getName().equalsIgnoreCase(args[0])) UUID = player.getUniqueId().toString();
						}
						
						if(UUID != null){
							BannedPlayer bannedPlayer = BannedPlayer.getBannedPlayer(UUID);
							
							Reason reason = bannedPlayer.getStatus().reason;
							
							Clock clock = Clock.getClock();
							Date date = Clock.getDate();
							String creationDate = date.day+"."+date.month+"."+date.year+" - " + clock.minute+":"+clock.hour;
							
							reason.editAuthor = BannedPlayer.getAuthor(sender);
							reason.editDate = creationDate;
							bannedPlayer.writeUnban(reason);
							
							for(Player player : Bukkit.getOnlinePlayers()){
								if(PermissionsEx.getUser(player).has("xioco.unban")) player.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.GREEN + " ubannet " + ChatColor.GOLD + args[0] + ChatColor.GREEN + "!");
							}
							break command;
						}else{
							sender.sendMessage(ChatColor.RED + "Spiller ikke funnet!");
							break command;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Bruk /unban NAVN");
					break command;
				}else{
					sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
					break command;
				}			
			}else if(cmd.getName().equalsIgnoreCase("ban")){
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.ban");
				}else{
					permission = true;
				}
				
				if(permission){
					if(args.length > 1){
						String reason = "";
						String UUID = null;
						
						Player player = getServer().getPlayer(args[0]);
						
						if(player == null){
							for(OfflinePlayer offline : getServer().getOfflinePlayers()){
								if(offline.getName().equalsIgnoreCase(args[0])) UUID = offline.getUniqueId().toString();
							}
						}else{
							UUID = player.getUniqueId().toString();
						}
						
						if(UUID != null){
							for(int i = 1; i < args.length; i++){
								reason+=args[i]+" ";
							}
							
							PlayerInfo.BannedPlayer bannedPlayer = PlayerInfo.BannedPlayer.getBannedPlayer(UUID);						
							if(bannedPlayer.getStatus().allowed){
								Reason b_reason = new PlayerInfo.BannedPlayer.Reason(UUID, PlayerInfo.BannedPlayer.Reason.Type.BAN, reason, reason, BannedPlayer.getAuthor(sender), BannedPlayer.getTime(), "", "");
								
								for(Player online : Bukkit.getOnlinePlayers()){
									if(PermissionsEx.getUser(online).has("xioco.ban")) online.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.GREEN + " bannet " + ChatColor.GOLD + args[0] + ChatColor.GREEN + " for " + reason);
								}
								
								bannedPlayer.writeBan(b_reason);
								
								if(player != null) player.kickPlayer(reason);						
								break command;
							}else{
								sender.sendMessage(ChatColor.RED + "Spiller allerede bannet!");
								break command;
							}
						}else{
							sender.sendMessage(ChatColor.RED + "Spiller ikke funnet!");
							break command;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Bruk /ban NAVN GRUNN");
					break command;
				}else{
					sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
					break command;
				}
			}else if(cmd.getName().equalsIgnoreCase("kick")){
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.kick");
				}else{
					permission = true;
				}
				
				if(permission){
					if(args.length > 1){
						String reason = "";
						
						for(int i = 1; i < args.length; i++){
							reason+=args[i]+" ";
						}
						
						if(args[0].equalsIgnoreCase("all")){
							for(Player player : Bukkit.getOnlinePlayers()){
								player.kickPlayer(reason);
							}
						}else{
							Player player = getServer().getPlayer(args[0]);
							
							if(player != null){
								PlayerInfo.BannedPlayer.getBannedPlayer(player).writeBan(new Reason(player.getUniqueId().toString(), PlayerInfo.BannedPlayer.Reason.Type.KICK, reason, reason, BannedPlayer.getAuthor(sender), BannedPlayer.getTime(), "", ""));
								
								for(Player online : Bukkit.getOnlinePlayers()){
									if(PermissionsEx.getUser(online).has("xioco.kick")) online.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.GREEN + " kicket " + ChatColor.GOLD + player.getDisplayName() + ChatColor.GREEN + " for " + reason);
								}
								
								player.kickPlayer(reason);
								
								break command;
							}
							
							sender.sendMessage(ChatColor.RED + "Finner ikke spiller!");
							break command;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Bruk /kick NAVN GRUNN");
					break command;
				}else{
					sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
					break command;
				}
			}else if(cmd.getName().equalsIgnoreCase("tempban") || cmd.getName().equalsIgnoreCase("temp")){
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.tempban");
				}else{
					permission = true;
				}
				
				if(permission){
					if(args.length > 2){
						String reason = "";
						String UUID = null;
						String time = args[1];
						String name = "";
						
						Player player = getServer().getPlayer(args[0]);
						
						if(player == null){
							for(OfflinePlayer offline : getServer().getOfflinePlayers()){
								if(offline.getName().equalsIgnoreCase(args[0])){
									UUID = offline.getUniqueId().toString();
									name = offline.getName();
								}
							}
						}else{
							UUID = player.getUniqueId().toString();
							name = player.getName();
						}
						
						if(UUID != null){
							for(int i = 2; i < args.length; i++){
								reason+=args[i]+" ";
							}
							
							BannedPlayer bannedPlayer = BannedPlayer.getBannedPlayer(UUID);
							if(bannedPlayer.getStatus().allowed){
								String date = "";
								String value = "";
								
								int minutes = 0;
								int days = 0;
								int weeks = 0;
								
								for(int i = 0; i < time.length(); i++){
									String point = String.valueOf(time.charAt(i));
									if(point.equals("0") || point.equals("1") || point.equals("2") || point.equals("3") || point.equals("4") || point.equals("5") || point.equals("6") || point.equals("7") || point.equals("8") || point.equals("9")) value+=point;
									else if(point.equals("m") && !value.equals("")) {
										minutes += Integer.parseInt(value);
										value="";
									}
									else if(point.equals("d") && !value.equals("")) {
										days += Integer.parseInt(value);
										value="";
									}
									else if(point.equals("u") && !value.equals("")) { 
										weeks += Integer.parseInt(value);
										value="";
									}
									else if(point.equals("t") && !value.equals("")) { 
										minutes += Integer.parseInt(value)*60;
										value="";
									}else { sender.sendMessage(ChatColor.RED + "Bruk /tempban NAVN TID(5m4t2d1u) GRUNN | /temp NAVN TID(5m4t2d1u) GRUNN "); break command; }
								}
								
								Calendar currDate = Calendar.getInstance();
								int year = currDate.get(Calendar.YEAR);
								int month = (currDate.get(Calendar.MONTH)+1); //STARTS AT 0
								int day = currDate.get(Calendar.DATE)+days+(weeks*7);
								int hour = currDate.get(Calendar.HOUR_OF_DAY);
								int minute = currDate.get(Calendar.MINUTE)+minutes;
								int second = currDate.get(Calendar.SECOND);
								
								while(second >= 60){
									second -= 60;
									minute++;
								}
								
								while(minute >= 60){
									minute -= 60;
									hour++;
								}
								
								while(hour >= 24){
									hour -= 24;
									day++;
								}
								
								while(day > currDate.getActualMaximum(Calendar.DAY_OF_MONTH)){
									day -= currDate.getActualMaximum(Calendar.DAY_OF_MONTH);
									month++;
									
									//Needs to be inside the day for loop. What if the month is 12, and you add another? The date can't be set to 13 bellow? Can it?
									//Makes sure you are not setting a invalid value.
									while(month >= 12){
										month-=12;
										year++;
									}
									
									//Updates the month so that the getActualMaximum retrieves correct data
									currDate.set(Calendar.MONTH, month);
								}
								
								Clock _clock = new Clock(""+hour, ""+minute, ""+second);
								Date _date = new Clock.Date(""+day, ""+month, ""+year);
								
								for(Player online : Bukkit.getOnlinePlayers()){
									if(PermissionsEx.getUser(online).has("xioco.tempban")){
										online.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.GREEN + " tempbannet " + ChatColor.GOLD + name + ChatColor.GREEN + " for " + reason);
										online.sendMessage(ChatColor.GREEN+""+name+" bannet til "+ BannedPlayer.getTime(_clock, _date) +"!");
									}
								}
								
								bannedPlayer.writeBan(new Reason(UUID, BannedPlayer.Reason.Type.TEMPBAN, reason, reason, BannedPlayer.getAuthor(sender), BannedPlayer.getTime(), BannedPlayer.getTime(_clock, _date), ""));
								//Configs.writeToFile(Configs.bannedPlayersFile, UUID+":"+date);
								
								if(player != null) player.kickPlayer(reason+"\nYou are banned until " + BannedPlayer.getTime(_clock, _date));
								break command;
							}else{
								sender.sendMessage(ChatColor.RED + "Spiller allerede bannet!");
								break command;
							}
						}else{
							sender.sendMessage(ChatColor.RED + "Spiller ikke funnet!");
							break command;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Bruk /tempban NAVN TID(5m4t2d1u) GRUNN | /temp NAVN TID(5m4t2d1u) GRUNN ");
					break command;
				}else{
					sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
					break command;
				}
			}else if(cmd.getName().equalsIgnoreCase("bank")){	
				sender.sendMessage(ChatColor.WHITE + "/konto " + ChatColor.GREEN + "- Se hvor mye du har p� konto.");
				sender.sendMessage(ChatColor.WHITE + "/inn ANTALL " + ChatColor.GREEN + "- Sett gull inn p� konto.");
				sender.sendMessage(ChatColor.WHITE + "/ut ANTALL " + ChatColor.GREEN + "- Ta ut gull fra konto.");
				sender.sendMessage(ChatColor.WHITE + "/banktopp " + ChatColor.GREEN + "- Se hvem som er rikest.");
				break command;
			}else if(cmd.getName().equalsIgnoreCase("gull")){	
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.gull");
				}else{
					permission = true;
				}
				
				if(permission){
					if(args.length == 2){
						Player online = Bukkit.getPlayer(args[0]);
						OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
						
						String UUID = null;
						
						if(online != null) UUID = online.getUniqueId().toString();
						if(UUID == null) UUID = offline.getUniqueId().toString();
						
						if(UUID == null){
							sender.sendMessage(ChatColor.RED + "Spiller ikke funnet!");
							break command;
						}else{
							int antall = 0;
							
							try{
								antall = Integer.parseInt(args[1]);
							}catch(Exception e){
								sender.sendMessage(ChatColor.RED + "Bruk /gull BRUKER ANTALL");
								break command;
							}
							
							PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(UUID);
							playerInfo.gull = antall;
							playerInfo.writePlayerInfo();
							
							sender.sendMessage(ChatColor.GREEN + args[0] + " konto forandret til " + ChatColor.GOLD + antall + " gull.");
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Bruk /gull BRUKER ANTALL");
					break command;
				}else{
					sender.sendMessage(ChatColor.RED + "Du har ikke n�dvendige rettigheter!");
					break command;
				}
			}else if(cmd.getName().equalsIgnoreCase("cleartable")){
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.cleartable");
				}else{
					permission = true;
				}
				
				if(permission && args.length == 1){
					SQLConnection.openConnection(false);
					
					try {
						Statement statement = SQLConnection.connection.createStatement();
						statement.executeUpdate("TRUNCATE " + args[0]);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}else if(cmd.getName().equalsIgnoreCase("updateprotection")){
				boolean permission = false;
				
				if(sender instanceof Player){
					PermissionUser user = PermissionsEx.getUser((Player)sender);
					permission = user.has("xioco.cleartable");
				}else{
					permission = true;
				}
				
				if(permission && false){
					Thread thread = new Thread(){
						public void run(){
							this.setPriority(MIN_PRIORITY);
							
							SQLConnection.openConnection(false);
							Debug debug = Debug.start();
							
							try {
								Statement stmt = SQLConnection.connection.createStatement();
								ResultSet result = stmt.executeQuery("SELECT * FROM `" + BlockProtection.tableName + "` WHERE plasert='true' ORDER BY id DESC");
								
								List<PointXYZ> points = new ArrayList<PointXYZ>();
								
								while(result.next()){
									ProtectedBlock pBlock = new ProtectedBlock(result.getInt("id"), result.getString("uuid"), result.getInt("x"), result.getInt("y"), result.getInt("z"), result.getString("plasert"), result.getString("block_name"));
									PointXYZ point = new PointXYZ(pBlock, pBlock.x, pBlock.y, pBlock.z);
									if(!points.contains(point)){
										points.add(point);
									}
								}
								
								PreparedStatement stmt2 = SQLConnection.connection.prepareStatement("INSERT INTO `" + BlockProtection.newTableName + "` values(?,?,?,?,?,?,?);");
								
								for(PointXYZ point : points){
									stmt2.setInt(1, 0);
									stmt2.setString(2, point.pBlock.UUID);
									stmt2.setInt(3, point.pBlock.x);
									stmt2.setInt(4, point.pBlock.y);
									stmt2.setInt(5, point.pBlock.z);
									stmt2.setString(6, point.pBlock.plasert);
									stmt2.setString(7, point.pBlock.blockName);
									stmt2.execute();
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							debug.print(ChatColor.AQUA+"ReadDatabase: ");
						}
					};
					
					thread.start();
				}
			}
		}
		
		debug.print(ChatColor.BLUE+"XioCoCommands: ");
		
		return true;
	}
	
	public void message(Player accuser, Player accused, String text){
		accuser.sendMessage(ChatColor.GREEN + accuser.getName() + " til " + accused.getName() + ": " + ChatColor.WHITE + text);
		accused.sendMessage(ChatColor.GREEN + accuser.getName() + " til " + accused.getName() + ": " + ChatColor.WHITE + text);
	}
	
	public void spy(Player accuser, Player accused, String text){
		PlayerInfo[] online = PlayerInfo.getPlayersInfo(Bukkit.getOnlinePlayers());	
		for(PlayerInfo info : online){
			if(info.socialSpy.equals("true")){
				info.getPlayer().sendMessage(ChatColor.GRAY + "Spy: " + accuser.getName() + " til " + accused.getName() + ": " + text);
			}
		}
	}
	
	public static boolean isGuest(Player player){
		return PermissionsEx.getUser(player).has("xioco.gjest");
	}
	
}
