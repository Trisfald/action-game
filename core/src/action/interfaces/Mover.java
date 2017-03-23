package action.interfaces;

import action.world.World;

import com.badlogic.gdx.math.Vector2;

/**
 * Interface for entities capable of movement into the game world
 * 
 * @author Andrea
 */
public interface Mover {

	public Vector2 getMovV();
	
    public void setMovV(float x, float y);
	
	/**
	 * @param smooth If true the creature will try to slide around the blocked tile
	 * @return True if the sub movement has been successfully completed
	 */
	public boolean move(World world, float dx, float dy, boolean smooth);
	
}
