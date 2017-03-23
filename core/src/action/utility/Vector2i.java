package action.utility;

import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;

/**
 * An object representation of an integer 2 dimensional vector
 *
 * @author Andrea
 */

public class Vector2i {
	
    public int x;
    public int y;
    
    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * 0.0 vector constructor
     */
    public Vector2i() {
    	this(0, 0);
    }
    
    /**
     * Copy constructor
     */
    public Vector2i(Vector2i v) {
    	this(v.x, v.y);
	}
    
    public void setX(int x) {
    	this.x = x;
    }
    
    public void setY(int y) {
    	this.y = y;
    }
    
    @Override
	public String toString() {
    	return ("(" + x + " ; " + y + ")");
    }
    
	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector2i) {
			Vector2i o = ((Vector2i) other);
			return (o.x == x) && (o.y == y);
		}
		
		return false;
	}
	
	public boolean equals(int x, int y) {
		if (this.x != x) return false;
		if (this.y != y) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
        return 997 * (x) ^ 991 * (y);
	}
	
	public float getDistance(Vector2i other) {
		return getDistance(other.x, other.y);
	}
	
	public float getDistance(int x, int y) {
		return (float) (Math.sqrt(Math.pow(this.x-x, 2) + Math.pow(this.y-y, 2)));
	}
	
	public float getDistanceSquared(Vector2i other) {
		return getDistanceSquared(other.x, other.y);
	}
	
	public float getDistanceSquared(int x, int y) {
		return (float) (Math.pow(this.x-x, 2) + Math.pow(this.y-y, 2));
	}
	
	/**
	 * @return A vector2f copy of this vector
	 */
	public Vector2 getVector2() {
		return new Vector2(x, y);
	}
	
	public boolean isZero() {
		return x == 0 && y == 0;
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return The vector resulting from the subtraction of the two given vectors
	 */
	public static Vector2i computeSubtraction(Vector2i v1, Vector2i v2) {
		return new Vector2i(v1.x - v2.x, v1.y - v2.y);
	}
	
	public static Vector2i convertVector2f(Vector2 v) {
		return new Vector2i((int) v.x, (int) v.y);
	}
	
	public static float dst(int x, int y, int tx, int ty) {
		return (float) (Math.sqrt(Math.pow(tx-x, 2) + Math.pow(ty-y, 2)));
	}
	
	
    /**
     * Comparator to sort vectors depending on their distance from a fixed point
     *
     * @author Andrea
     */
    public static class DistanceComparator implements Comparator<Vector2i> {

    	private Vector2i source;
    	
    	public DistanceComparator(Vector2i source) {
    		this.source = source;
    	}
    	
        @Override
        public int compare(Vector2i obj1, Vector2i obj2) {
            if (obj1 != null && obj2 != null){
                if (obj1.getDistanceSquared(source) > obj2.getDistanceSquared(source))
                    return 1;
                else
                    if (obj1.getDistanceSquared(source) == obj2.getDistanceSquared(source))
                        return 0;
            }
            return -1;
        }
    }

}