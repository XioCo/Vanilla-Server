package net.scratchforfun.xioco;

public class PointXYZ {
	
	ProtectedBlock pBlock;
	
	int x;
	int y;
	int z;
	
	public PointXYZ(ProtectedBlock pBlock, int x, int y, int z){
		this.pBlock = pBlock;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object object){
		if(object == null) return false;
		
		if(object instanceof PointXYZ){
			PointXYZ point = (PointXYZ) object;
			if(this.x == point.x)
				if(this.y == point.y)
					if(this.z == point.z) return true;
		}
		
		return false;
	}

}
