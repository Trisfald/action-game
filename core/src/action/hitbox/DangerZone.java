package action.hitbox;

import action.combat.BlockLevel;
import action.entity.Entity;
import action.utility.enums.Direction;
import action.world.Faction;

import com.badlogic.gdx.math.Vector2;

/**
 * Class used to manage a dangerous zone of the map
 * 
 * @author Andrea
 */
public class DangerZone {

	private StaticHitBox hitBox;
	private boolean active;
	private BlockLevel blockLevel;
	/** The faction creating this dangerzone or null if it's a danger for everyone */
	private Faction faction;

	/**
	 * Create an active danger zone
	 */
	public DangerZone(StaticHitBox hitBox, BlockLevel level, Faction faction) {
		this(hitBox, level, faction, true);
	}
	
	/**
	 * Standard constructor
	 */
	public DangerZone(StaticHitBox hitBox, BlockLevel level, Faction faction, boolean active) {
		this.hitBox = hitBox;
		this.blockLevel = level;
		this.faction = faction;
		this.active = active;
	}

	public StaticHitBox getHitBox() {
		return hitBox;
	}
	
	/**
	 * @return True if this dangerzone is dangerous for the given entity
	 */
	public boolean isMenacing(Entity entity) {
		if (faction == null)
			return true;
		
		return faction.isAggresiveTo(entity.getFaction());
	}
	
	public void setHitBox(StaticHitBox hitBox) {
		this.hitBox = hitBox;
	}
	
	public BlockLevel getBlockLevel() {
		return blockLevel;
	}

	/**
	 * @param pos Starting center position of the entity who wants to block
	 * @return The direction to block from this danger
	 */
	public Direction getBlockingDir(Vector2 pos) {
		if (hitBox.getStart() != null)
			return Direction.getDirTowards(pos, hitBox.getStart());
		return Direction.getDirTowards(pos, hitBox.getCenter());
	}
	
	/**
	 * @param x Center x
	 * @param y Center y
	 * @return The direction to block from this danger
	 */
	public Direction getBlockingDir(float x, float y) {
		return getBlockingDir(new Vector2(x, y));
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
