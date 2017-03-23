package action.item.shield;

import action.combat.Alterable;
import action.combat.BlockLevel;
import action.entity.enums.BlockType;
import action.entity.enums.BlockType.BlockMotionType;
import action.item.Item.ItemType;
import action.utility.enums.Direction;

/**
 * An item capable of blocking
 * 
 * @author Andrea
 */
public interface Blocker extends Alterable {

	public void setCurrentBlock(BlockType block);

	/**
	 * @return The block style of the current block
	 */
	public BlockMotionType getBlockStyle();
	
	/**
	 * Tell the level of blocking provided
	 */
	public BlockLevel getBlockCapability();

	public int getBlockHitBoxWidth();

	public int getBlockHitBoxLength();

	public float getBlockSpeed();
	
	public float getCrushResist();

	public boolean isAlive();
	
	public void resetBlock(Direction dir);
	
	public float getBlockCost(BlockType type);
	
	/**
	 * @return The block style if present, otherwise null
	 */
	public BlockMotionType getBlockStyle(BlockType type);
	
	public boolean canBlock(BlockType type);
	
	public ItemType getItemType();
	
}
