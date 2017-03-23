package action.spawner;


/**
 * Interface for classes able to spawn entities into the game world
 * 
 * @author Andrea
 */
public interface Spawner {
	
	public void update(float delta);
	
	/**
	 * @return True if the spawner has completed his duty
	 */
	public boolean isDone();
	
	
	
	/**
	 * @author Andrea
	 */
	public enum SpawnerType {
		
		BEAM, 
		PROJECTILE;
		
	}
}
