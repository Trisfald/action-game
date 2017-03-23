package action.utility.timer;

/**
 * Class to make timers easy to use
 * 
 * @author Andrea
 */
public class Timer {

	protected float counter;
	/** Target value. (-1 for infinite timer) */
	protected float length;
	/** True if the timer restarts itself after reaching the target length */
	private boolean cyclic;
	
	/**
	 * Standard timer
	 */
	public Timer(float length) {
		this(0, length, false);
	}
	
	/**
	 * Timer with a different start from 0
	 */
	public Timer(float start, float length, boolean cyclic) {
		this.counter = start;
		this.length = length;
		this.cyclic = cyclic;
	}
	
	/**
	 * Timer with a fixed start at 0
	 */
	public Timer(float length, boolean cyclic) {
		this(0, length, true);
	}
	
	/**
	 * Endless timer
	 */
	public Timer() {
		this(0, -1, false);
	}
	
	public void restart() {
		counter = 0;
	}
	
	public boolean isEndless() {
		return length < 0;
	}
	
	/**
	 * Restart from a given value
	 */
	public void restart(float value) {
		counter = value;
	}
	
	public boolean expired() {
		return expired(length);
	}
	
	public boolean expired(float value) {
		return (counter >= value && !isEndless() && !cyclic);
	}
	
	public void update(float delta) {
		/* Don't update if the timer has already reached the limit. But an endless timer is always updated */
		if (expired() && !isEndless())
			return;
		counter += delta;
		
		/* Endless timers don't need extra checks */
		if (isEndless())
			return;
		
		/* Check for counter floategrity (must be less or equal than the length) */
		if (counter >= length)
			if (cyclic)
				counter -= length;
			else
				counter = length;
	}
	
	/**
	 * Extend the length of the timer
	 */
	public void extend(float value) {
		length += value;
	}
	
	/**
	 * Set the length of the timer to the new value
	 */
	public void setLength(float value) {
		length = value;
	}

	/**
	 * @return The progress (from 0 to 1) of the timer
	 */
	public float getProgress() {
		if (length == 0)
			return 1;
		
		return (float)counter / (float)length;
	}

	public float getLength() {
		return length;
	}
	
	public float getCounter() {
		return counter;
	}
	
	/**
	 * Set the value of the counter of this timer
	 */
	public void setCounter(float value) {
		counter = value;
	}
	
	@Override
	public String toString() {
		if (isEndless())
			return "-";
		
		return counter + " / " + length;
	}
	
}
