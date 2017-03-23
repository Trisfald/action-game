package action.hitbox;

import action.interfaces.GlobalVar;
import action.utility.enums.Direction;
import action.utility.geom.Rectangle;
import action.utility.geom.Shape;
import action.utility.geom.Transform;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * An utility to manage an hitbox working for more than one direction
 *
 * @author Andrea
 */
public class IndexedHitBox {
    
	/** Length of the hitbox */
	private int length;
	/** Rectangle for behind the scenes operations */
	private Rectangle rectangle;
	/** The actual geometric shape for the collision */
    private Shape shape;
    /** Distance between the start of the hitbox and the pivot (horizontal) */
    private float radiusH;
    /** Distance between the start of the hitbox and the pivot (vertical) */
    private float radiusV;
    /** The actual direction for which the hitbox is calibrated */
    private Direction cachedDir = null;
    
    private float borderW;
    private float borderH;
    
    /**
     * Create a normal hitbox
     * @param radiusH Distance start the center of the hitbox and the pivot (horizontal) 
     * @param radiusV Distance start the center of the hitbox and the pivot (vertical) 
     * @param width Width of the hitbox
     * @param length Height of the hitbox
     */
    public IndexedHitBox(float radiusH, float radiusV, int width, int length) {
        this.radiusH = radiusH;
        this.radiusV = radiusV;
        this.length = length;
        /* Set the shape to a default Rectangle */
        rectangle = new Rectangle(0, 0, width, length);
        shape = new Rectangle(0, 0, width, length);
        borderW = shape.getWidth()/2;
        borderH = shape.getHeight()/2;
    }
    
    /**
     * Create an hitbox with the default radius
     * @param width Width of the hitbox
     * @param height Height of the hitbox
     */
    public IndexedHitBox(int width, int height) {
    	this(GlobalVar.HITBOX_RADIUS, GlobalVar.HITBOX_RADIUS, width, height);
    }

    /**
     * @param x The pivot X around which the hitbox rotates
     * @param y The pivot Y around which the hitbox rotates
     *
     * @return Updates the hitbox and return the shape for collisions
     */
    public Shape getShape(float x, float y, Direction dir) {
        rotate(dir);
        shape.setLocation(x - borderW + dir.getVector().x * (radiusH + borderH),
        		y - borderH + dir.getVector().y * (radiusV + borderH));
        return shape;
    }
    
    /**
     * @return The shape with the last set up used
     */
    public Shape getShape() {
    	return shape;
    }
    
    /** 
     * Set up the hitbox for the desired direction 
     */
    public void rotate(Direction dir) {
    	if (dir == cachedDir)
    		return;
    	shape = rectangle.transform(Transform.createRotateTransform(dir.getShapeAngle(), rectangle.getCenterX(), rectangle.getCenterY()));
    	cachedDir = dir;
    }

    public boolean collide(float x, float y, Direction dir, Shape target) {
        shape = getShape(x, y, dir);
        return shapeCollide(shape, target);
    }
    
    public static boolean shapeCollide(Shape shape1, Shape shape2) {
    	if (shape1 == null || shape2 == null)
    		return false;
        return (shape1.intersects(shape2) || shape1.contains(shape2) || shape2.contains(shape1));
    }
    
    /**
     * @return A vector containing the position of the central point of hitbox's starting edge
     */
    public Vector2 getTipPosition() {
    	float x = shape.getCenterX() + MathUtils.cos(cachedDir.getVector().angleRad()) * length / 2;
    	float y = shape.getCenterY() + MathUtils.sin(cachedDir.getVector().angleRad()) * length / 2;
    	return new Vector2(x, y);
    }
    
    /**
     * @return A vector containing the position of the central point of hitbox's ending edge
     */
    public Vector2 getStartPosition() {
    	float x = shape.getCenterX() + MathUtils.cos(MathUtils.PI + cachedDir.getVector().angleRad()) * length / 2;
    	float y = shape.getCenterY() + MathUtils.sin(MathUtils.PI + cachedDir.getVector().angleRad()) * length / 2;
    	return new Vector2(x, y);
    }
    
    /**
     * @return A vector containing the position of the central point of hitbox
     */
	public Vector2 getCentralPosition() {
		return new Vector2(shape.getCenterX(), shape.getCenterY());
	}
    
    public StaticHitBox getStatic() {
    	return new StaticHitBox(getShape(), getTipPosition(), getStartPosition());
    }

}
