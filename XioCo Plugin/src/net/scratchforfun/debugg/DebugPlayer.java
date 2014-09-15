package net.scratchforfun.debugg;

public class DebugPlayer {

	public String player;
	public int cap;
	
	public DebugPlayer(String player){
		this.player = player;
	}
	
	public DebugPlayer(String player, int cap){
		this(player);
		this.cap = cap;
	}
	
}
