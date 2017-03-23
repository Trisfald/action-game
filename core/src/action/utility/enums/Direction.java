package action.utility.enums;

import action.utility.Vector2i;
import action.world.World;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Enumeration of all possible directions for orienting an entity
 *
 * @author Andrea
 */
public enum Direction {
	
	UPPER_LEFT(0),
	UP(1),
	UPPER_RIGHT(2),
	RIGHT(3),
	DOWN_RIGHT(4),
	DOWN(5),
	DOWN_LEFT(6),
	LEFT(7),
	CENTER(0);
	
	private final int value;
	
	Direction(int value) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
	
	/**
	 * @return Not normalized vector
	 */
	public Vector2 getVectorOfOnes() {
		float x = 0, y = 0;
		
		switch (this) {
			case UPPER_LEFT:
				x = -1;
				y = 1;
				break;
			case UP:
				x = 0;
				y = 1;
				break;
			case UPPER_RIGHT:
				x = 1;
				y = 1;
				break;
			case RIGHT:
				x = 1;
				y = 0;
				break;
			case DOWN_RIGHT:
				x = 1;
				y = -1;
				break;
			case DOWN:
				x = 0;
				y = -1;
				break;
			case DOWN_LEFT:
				x = -1;
				y = -1;
				break;
			case LEFT:
				x = -1;
				y = 0;
				break;
			default:
				break;
		}
		
		return new Vector2(x, y);
	}
	
	/**
	 * @return The unit vector
	 */
	public Vector2 getVector() {
		return getVectorOfOnes().nor();
	}
	
	public static Direction unitVectorToDir(Vector2 v) {
		return unitVectorToDir(v.x, v.y);
	}
	
	public static Direction unitVectorToDir(float x, float y) {
		if (x > 0) {
			if (y < 0) 
				return DOWN_RIGHT;
			else if (y > 0)
				return UPPER_RIGHT;
			else
				return RIGHT;
		}
		else if (x < 0) {
			if (y < 0) 
				return DOWN_LEFT;
			else if (y > 0)
				return UPPER_LEFT;
			else
				return LEFT;	
		}
		else {
			if (y < 0)
				return DOWN;
			else if (y > 0)
				return UP;
			else 
				return CENTER;
		}	
	}
	
	public static Direction unitVectorToDir(Vector2i v) {
		return unitVectorToDir(v.x, v.y);
	}
	
	public static Direction vectorToDir(float x, float y) {
		Vector2 v = new Vector2(x, y).nor();
		return unitVectorToDir(Math.round(v.x), Math.round(v.y));
	}
	
	public static Direction vectorToDir(Vector2 v) {
		return vectorToDir(v.x, v.y);
	}
	
	/**
	 * Convert a direction (8 choises) to a small index (4 choises), interpreting diagonal directions as laterals
	 */
	public int fourCoordValue() {
		switch (this) {
			case UP:
				return 0;
			case UPPER_RIGHT: case RIGHT: case DOWN_RIGHT:
				return 3;
			case DOWN:
				return 2;	
			default:
				return 1;
		}
	}
	
	public boolean isDiagonal() {
		switch (this) {
			case UPPER_LEFT: case UPPER_RIGHT: case DOWN_RIGHT: case DOWN_LEFT:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Compute the angle corresponding to this direction. The 0° angle is used for direction DOWN. 
	 * The angle is counted clockwise.
	 * 
	 * @return The angle of the direction (in radiants)
	 */
	public float getShapeAngle() {
		return -((float)value - 5) / 4 * MathUtils.PI;
	}

	/**
	 * @return The x offset (in pixel) to apply to everything belonging the map in this direction (relative to the map matrix)
	 */
	public int getMapOffsetXEff() {
		return getMapOffsetX() * World.MAP_SIZE_EFFECTIVE;
	}
	
	/**
	 * @return The y offset (in pixel) to apply to everything belonging the map in this direction (relative to the map matrix)
	 */
	public int getMapOffsetYEff() {
		return getMapOffsetY() * World.MAP_SIZE_EFFECTIVE;
	}

	
	/**
	 * @return The x offset (in map index) to apply to everything belonging the map in this direction (relative to the map matrix)
	 */
	public int getMapOffsetX() {
		return (int) (getVectorOfOnes().x + 1);
	}
	
	/**
	 * @return The y offset (in map index) to apply to everything belonging the map in this direction (relative to the map matrix)
	 */
	public int getMapOffsetY() {
		return (int) (getVectorOfOnes().y + 1);
	}
	
	/**
	 * @return The opposite direction
	 */
	public Direction getOpposite() {
		switch(this) {
			case UPPER_LEFT:
				return DOWN_RIGHT;
			case UP:
				return DOWN;
			case UPPER_RIGHT:
				return DOWN_LEFT;
			case RIGHT:
				return LEFT;
			case DOWN_RIGHT:
				return UPPER_LEFT;
			case DOWN:
				return UP;
			case DOWN_LEFT:
				return UPPER_RIGHT;
			case LEFT:
				return RIGHT;
			default:
				return CENTER;
		}
	}
	
	/**
	 * @return The direction of the end vector in respect to the starting one
	 */
	public static Direction getDirTowards(Vector2 start, Vector2 end) {
		return vectorToDir(end.x - start.x, end.y - start.y);
	}

}
