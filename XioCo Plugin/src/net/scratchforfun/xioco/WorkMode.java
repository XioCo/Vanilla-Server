package net.scratchforfun.xioco;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WorkMode {
	
	//ADMINSTICK
	private static Material stickMaterial = Material.BLAZE_ROD;
	private static String stickName = ChatColor.DARK_AQUA + "Adminstick";
	private static List<String> stickLore = Arrays.asList("THE STICK OF ADMINS!");//The more strings the more lines

	//TELEPORTER
	private static Material compassMaterial = Material.COMPASS;
	private static String compassName = ChatColor.DARK_AQUA + "Teleporter";
	private static List<String> compassLore = Arrays.asList("THE INCREDIBLE TELEPORTER!");

	//WOODENAXE
	private static Material woodenaxeMaterial = Material.WOOD_AXE;
	private static String woodenaxeName = ChatColor.DARK_AQUA + "Wooden Axe";
	private static List<String> woodenaxeLore = Arrays.asList("THE AXE OF DESTRUCTION!");

	//TOOL
	private static Material woodenswordMaterial = Material.WOOD_SWORD;
	private static String woodenswordName = ChatColor.DARK_AQUA + "Tool";
	private static List<String> woodenswordLore = Arrays.asList("THE SWORD OF MIGHT!");

	//WATER
	private static Material waterMaterial = Material.WATER;
	private static String waterName = ChatColor.DARK_AQUA + "Water";
	private static List<String> waterLore = Arrays.asList("THE WATER OF REFRESHMENT!?");
	
	//LAVA
	private static Material lavaMaterial = Material.LAVA;
	private static String lavaName = ChatColor.DARK_AQUA + "Lava";
	private static List<String> lavaLore = Arrays.asList("Lava...");

	//FIRE
	private static Material fireMaterial = Material.FIRE;
	private static String fireName = ChatColor.DARK_AQUA + "Fire";
	private static List<String> fireLore = Arrays.asList("THE FIRE OF SUBDUCTION!?");
	
	//BEDROCK
	private static Material bedrockMaterial = Material.BEDROCK;
	private static String bedrockName = ChatColor.DARK_AQUA + "Bedrock";
	private static List<String> bedrockLore = Arrays.asList("THE STONY HARD STONE OF STONE!?", "Totaly out of ideas :/");
	
	//MIDLERTIDIG
	private static Material signMaterial = Material.STICK;
	private static String signName = ChatColor.DARK_AQUA + "Midlertidig";
	private static List<String> signLore = Arrays.asList("WHACK A SIGN!");//The more strings the more lines

	public static final ItemStack ADMINSTICK = XioCo.createItem(new ItemStack(stickMaterial), stickName, stickLore);
	public static final ItemStack TELEPORTER = XioCo.createItem(new ItemStack(compassMaterial), compassName, compassLore);
	public static final ItemStack WOODENAXE = XioCo.createItem(new ItemStack(woodenaxeMaterial), woodenaxeName, woodenaxeLore);
	public static final ItemStack TOOL = XioCo.createItem(new ItemStack(woodenswordMaterial), woodenswordName, woodenswordLore);
	public static final ItemStack WATER = XioCo.createItem(new ItemStack(waterMaterial), waterName, waterLore);
	public static final ItemStack LAVA = XioCo.createItem(new ItemStack(lavaMaterial), lavaName, lavaLore);
	public static final ItemStack FIRE = XioCo.createItem(new ItemStack(fireMaterial), fireName, fireLore);
	public static final ItemStack BEDROCK = XioCo.createItem(new ItemStack(bedrockMaterial), bedrockName, bedrockLore);
	public static final ItemStack MIDLERTIDIG = XioCo.createItem(new ItemStack(signMaterial), signName, signLore);
	
}
