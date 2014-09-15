package net.scratchforfun.xioco.permission;

public class Permission {

	public static Permission XIOCO_PROTECT = new Permission("xioco.protect");
	public static Permission XIOCO_ADMINSHOP = new Permission("xioco.adminshop");
	public static Permission XIOCO_OPENCHEST = new Permission("xioco.openchest");
	public static Permission XIOCO_GUEST = new Permission("xioco.gjest");
	
	public String permission;
	
	private Permission(String permission){
		this.permission = permission;
	}
	
}
