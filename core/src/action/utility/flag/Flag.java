package action.utility.flag;

/**
 * An utility to store a boolean information accessible by many
 *
 * @author Andrea
 */
public class Flag {

	protected int counter = 0;
	
	public boolean active() {
		return (counter > 0);
	}
	
	public void hold() {
		counter++;
	}
	
	public void release() {
		if (counter >= 1)
			counter--;
	}

	/**
	 * @return The current number of holds
	 */
	public int count() {
		return counter;
	}
	
	public void reset() {
		counter = 0;
	}
	
}
