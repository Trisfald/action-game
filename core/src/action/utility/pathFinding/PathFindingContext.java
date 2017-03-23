package action.utility.pathFinding;

import action.entity.Entity;

/**
 * The context describing the current path finding state
 * 
 * @author kevin, Andrea
 */
public interface PathFindingContext {
	/**
	 * Get the entity being moved along the path if any
	 * 
	 * @return The entity being moved along the path
	 */
	public Entity getMover();
	
	/**
	 * Get the x coordinate of the source location
	 * 
	 * @return The x coordinate of the source location
	 */
	public int getSourceX();

	/**
	 * Get the y coordinate of the source location
	 * 
	 * @return The y coordinate of the source location
	 */
	public int getSourceY();
	
	/**
	 * Get the distance that has been searched to reach this point
	 * 
	 * @return The distance that has been search to reach this point
	 */
	public int getSearchDistance();
	
}
