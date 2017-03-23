package action.entity;

import java.util.Comparator;

import action.combat.Alterable;
import action.combat.HitLevel;
import action.combat.Knockback;
import action.entity.appearance.Appearance;
import action.hitbox.DangerZone;
import action.utility.geom.Shape;
import action.world.Faction;
import action.world.World;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * Interface that defines the objects in the game world
 *
 * @author Andrea
 */
public interface Entity extends Alterable, Interactable, Disposable {
	
    public float getX();
    
    public float getY();
    
    /**
     * @return The top point Y
     */
    public float getTopY();
    
    public Vector2 getPos();
    
    public float getWidth();
    
    public float getHeight();
    
    public float getPerspectiveHeight();
    
    public float getCenterX();
    
    public float getCenterY();
    
    public float getPerspectiveCenterY();
    
    public float getMassFactor();
    
    public float getKnockbackResist();
    
    public Faction getFaction();
    
    /**
     * 
     * @return The world this entity belongs to
     */
    public World getWorld();
    
    public void setPos(float x, float y);
    
    public void updatePos(float dx, float dy);

    /**
     * @return The distance between the two perspective centers
     */
    public float computeDistance(Entity target);
    
    /**
     * @return The distance between the perspective center and the center of the shape
     */
    public float computeDistance(Shape shape);
    
    /**
     * @return The distance from border to border
     */
    public float computeBorderDistance(Entity target);
    
    /**
     * @return The radius of the shape (approximated)
     */
    public float getAverageRadius();
    
    /**
     * @return The perspective shape of the entity at his current locations plus a displacement
     */
    public Shape getPerspectiveShape(float dx, float dy);
    
    /**
     * @return The perspective shape of the entity at his current locations
     */
    public Shape getPerspectiveShape();
    
    /**
     * @return The shape of the entity at his current locations plus a displacement
     */
    public Shape getShape(float dx, float dy);
    
    /**
     * @return The shape of the entity at his current locations
     */
    public Shape getShape();
    
    public boolean isAggressiveTo(Entity target);
    
    public void initialize();
    
    public void update(float delta);
    
	public void draw(SpriteBatch batch);
      
    public boolean isAlive();
    
	/**
	 * @return True if this entity exists in some way, either dead or alive
	 */
	public boolean exist();
    
    public float getPerspectiveRatio();
    
    public boolean isBlockingMov();
    
    public boolean isHittable(HitLevel level);
    
    public void takeKnockback(Knockback knockback);

    /**
     * @return The handle to the entity's appearance
     */
    public Appearance getAppearance();
    
    /**
     * Shifts everything related to the position of this entity. 
     * It's a change of coordinates not a movement
     */
    public void shiftPosition(float dx , float dy); 
    
    /**
     * Starts all the operation needed after the death of this entity
     */
    public void die();
    
    public DangerZone getDangerZone();
    
    /**
     * @return True if at least a piece of this entity is on the given camera
     */
    public boolean isOnCamera(Camera camera);
    
    
    /**
     * Utility to compare which entity is 'nearer' to the screen
     *
     * @author Andrea
     */
    public class ZorderComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity obj1, Entity obj2) {
            if (obj1 != null && obj2 != null){
            	/** Comparison involves onlty the vertical coordinate */
                if (obj1.getTopY() < obj2.getTopY())
                    return 1;
                else
                    if (obj1.getTopY() == obj2.getTopY())
                        return 0;
            }
            return -1;
        }
    }
    
    
    /**
     * Utility to compare which entity is 'nearer' to another one
     *
     * @author Andrea
     */
    public class DistanceComparator implements Comparator<Entity> {

    	private Entity source;
    	
    	public DistanceComparator(Entity source) {
    		this.source = source;
    	}
    	
        @Override
        public int compare(Entity obj1, Entity obj2) {
            if (obj1 != null && obj2 != null){
                if (obj1.computeBorderDistance(source) > obj2.computeBorderDistance(source))
                    return 1;
                else
                    if (obj1.computeBorderDistance(source) == obj2.computeBorderDistance(source))
                        return 0;
            }
            return -1;
        }
    }


}
