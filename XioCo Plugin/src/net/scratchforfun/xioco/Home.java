package net.scratchforfun.xioco;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Home {

	public String name;
	public String world;
	public int x;
	public int y;
	public int z;
	
	public Home(String name, String world, int x, int y, int z){
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location getHome(){
		return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
	}
	
}
