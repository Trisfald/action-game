package action.entity.enums;

/**
 * Enumeration of all possible simple motions
 *
 * @author Andrea
 */
public enum SimpleMotionType {
	
	/** Doing nothing */
	IDLE,
	/** Walking normally */
	WALK,
	/** Blocking standing still */
	BLOCK_IDLE,
	/** Walking while blocking */
	BLOCK_WALK,
	/** Taking Damage */
	HURT,
	/** Dodging with a leap */
	DODGE;
	
}
