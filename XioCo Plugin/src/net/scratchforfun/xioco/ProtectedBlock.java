package net.scratchforfun.xioco;

public class ProtectedBlock {

	public int x;
	public int y;
	public int z;
	
	public int id;
	
	public String UUID;//The players unique id
	public String date;//dd.mm.yyyy
	public String clock;//hh:mm
	public String plasert;//true/false  <-- Not sure why this is not a boolean :/ SQL stuff
	public String blockName;//The block name
	
	public ProtectedBlock(int id, String UUID, int x, int y, int z, String plasert, String blockName){
		this.id = id;
		this.UUID = UUID;
		this.plasert = plasert;
		this.blockName = blockName;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public ProtectedBlock(int id, String UUID, int x, int y, int z, String date, String clock, String plasert, String blockName){
		this(id, UUID, x, y, z, plasert, blockName);
		this.date = date;
		this.clock = clock;
	}
	
}
