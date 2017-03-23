package action.item.shield;

import action.entity.enums.BlockType;
import action.entity.enums.BlockType.BlockMotionType;

/**
 * Manages the block feature
 *
 * @author Andrea
 */
public class ShieldBlock {
	
    private BlockType type;
    private BlockMotionType style;
    private float crushResist;
    private float speed;
    private int hitBoxWidth;
    private int hitBoxLength;
    private float cost;
    
    public ShieldBlock(ShieldBlockInfo info) {
        this.type = info.type;
        style = info.style;
        crushResist = info.crushResist;
        speed = info.speed;
        hitBoxWidth = info.hitBoxWidth;
        hitBoxLength = info.hitBoxLength;
        cost = info.cost;
    }

    public BlockType getType() {
        return type;
    }
    
    public BlockMotionType getStyle() {
        return style;
    }
 
    public float getCost() {
    	return cost;
    }
    
    public float getSpeed() {
    	return speed;
    }
    
    public float getCrushResist() {
    	return crushResist;
    }
    
	public int getHitBoxWidth() {
		return hitBoxWidth;
	}

	public int getHitBoxLength() {
		return hitBoxLength;
	}
	
    
	/**
	 * @author Andrea
	 */
    public static class ShieldBlockInfo {

    	public final BlockType type;
    	public final BlockMotionType style;
    	public final float crushResist;
    	public final float speed;
        public final int hitBoxWidth;
        public final int hitBoxLength;
        public final float cost;
        
		public ShieldBlockInfo(BlockType type, BlockMotionType style, float crushResist, float speed, int hitBoxWidth,
				int hitBoxLength, float cost) {
			this.type = type;
			this.style = style;
			this.crushResist = crushResist;
			this.speed = speed;
			this.hitBoxWidth = hitBoxWidth;
			this.hitBoxLength = hitBoxLength;
			this.cost = cost;
		}
         
    }
}
