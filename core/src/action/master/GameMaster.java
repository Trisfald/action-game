package action.master;

import java.util.Random;

/**
 * Game master interfacing the game with the network
 * 
 * @author Andrea
 */
public interface GameMaster {

	/**
	 * Dismiss this master
	 */
	public void dismiss();
	
	/**
	 * @return True if this is the default master
	 */
	public boolean isDefault();

	/**
	 * @see Math#random()
	 */
	public float random();

	/**
	 * @return A random instance
	 */
	public Random getRandom();
	
	public void update();

}
