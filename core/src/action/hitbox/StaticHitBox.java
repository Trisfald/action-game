package action.hitbox;

import action.utility.geom.Shape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Simple class for hitboxes that only contains informations and cannot be manipulated
 * 
 * @author Andrea
 */
public class StaticHitBox {

	private Shape shape;
	private Vector2 tip;
	private Vector2 start;
	
	/**
	 * Create a static hitbox for an area of effect (no vectors for edges)
	 * 
	 * @param shape
	 */
	public StaticHitBox(Shape shape) {
		this(shape, null, null);
	}
	
	/**
	 * Construct a static hitbox starting from some parameters
	 * 
	 * @param shape
	 * @param dirV
	 * @param length
	 */
	public StaticHitBox(Shape shape, Vector2 dirV, float length) {
		this.shape = shape;
		this.tip = computeTipPosition(dirV, length);
		this.start = computeStartPosition(dirV, length);
	}
	
	/**
	 * Standard constructor
	 * 
	 * @param shape
	 * @param tip
	 * @param start
	 */
	public StaticHitBox(Shape shape, Vector2 tip, Vector2 start) {
		this.shape = shape;
		this.tip = tip;
		this.start = start;
	}

    public Vector2 computeTipPosition(Vector2 dirV, float length) {
    	float x = shape.getCenterX() + MathUtils.cos(dirV.angleRad()) * length / 2;
    	float y = shape.getCenterY() + MathUtils.sin(dirV.angleRad()) * length / 2;
    	return new Vector2(x, y);
    }
    
    public Vector2 computeStartPosition(Vector2 dirV, float length) {
    	float x = shape.getCenterX() + MathUtils.cos(MathUtils.PI + dirV.angleRad()) * length / 2;
    	float y = shape.getCenterY() + MathUtils.sin(MathUtils.PI + dirV.angleRad()) * length / 2;
    	return new Vector2(x, y);
    }
	
	public Shape getShape() {
		return shape;
	}

	public Vector2 getTip() {
		return tip;
	}

	public Vector2 getStart() {
		return start;
	}
	
	public Vector2 getCenter() {
		return new Vector2(shape.getCenterX(), shape.getCenterY());
	}
	
}
