package action.world.map;

import action.utility.enums.Direction;
import action.utility.geom.Rectangle;
import action.utility.geom.Shape;


/**
 * An utility to manage non smooth transition between maps
 *
 * @author Andrea
 */
public class Transition {
	
	private int x;
	private int y;
    private int offsetX = 0;
    private int offsetY = 0;
    /** ID of the map this transition points to */
    private int dstMap;
    private int dstX;
    private int dstY;
    /** Hitbox for the transition */
    private Rectangle rect;

    /**
     * Create an interactable transition
     */
    public Transition(com.badlogic.gdx.math.Rectangle rectangle, int dstMap, int dstX, int dstY) {
    	this.x = (int) rectangle.x;
    	this.y = (int) rectangle.y;
        this.dstMap = dstMap;
        this.dstX = dstX;
        this.dstY = dstY;
        rect = new Rectangle(x, y, rectangle.width, rectangle.height);
	}
    
    /**
     * Create a simple transition
     */
    public Transition(int dstMap, int dstX, int dstY) {
        this.dstMap = dstMap;
        this.dstX = dstX;
        this.dstY = dstY;
    }

	public int getDstMap() {
        return dstMap;
    }
    
    public int getDstX() {
        return dstX;
    }
    
    public int getDstY() {
        return dstY;
    }
    
    public Shape getShape() {
    	rect.setX(x + offsetX);
    	rect.setY(y + offsetY);
        return rect;
    }
    
    public void applyOffset(Direction dir) {
    	offsetX = dir.getMapOffsetXEff();
    	offsetY = dir.getMapOffsetYEff();
    }
}
