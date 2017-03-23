package action.utility.timer;

/**
 * Class to make timers easy to use
 * 
 * @author Andrea
 */
public class TimerInt {

	protected int counter;
	/** Target value. (-1 for infinite timer) */
	protected int length;
	/** True if the timer restarts itself after reaching the target length */
	private boolean cyclic;
	
	/**
	 * Standard timer
	 */
	public TimerInt(int length) {
		this(0, length, false);
	}
	
	/**
	 * Timer with a different start from 0
	 */
	public TimerInt(int start, int length, boolean cyclic) {
		this.counter = start;
		this.length = length;
		this.cyclic = cyclic;
	}
	
	/**
	 * Timer with a fixed start at 0
	 */
	public TimerInt(int length, boolean cyclic) {
		this(0, length, true);
	}
	
	/**
	 * Endless timer
	 */
	public TimerInt() {
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
	public void restart(int value) {
		counter = value;
	}
	
	public boolean expired() {
		return expired(length);
	}
	
	public boolean expired(int value) {
		return (counter >= value && !isEndless() && !cyclic);
	}
	
	public void update(int delta) {
		/* Don't update if the timer has already reached the limit. But an endless timer is always updated */
		if (expired() && !isEndless())
			return;
		counter += delta;
		
		/* Endless timers don't need extra checks */
		if (isEndless())
			return;
		
		/* Check for counter integrity (must be less or equal than the length) */
		if (counter >= length)
			if (cyclic)
				counter -= length;
			else
				counter = length;
	}
	
	/**
	 * Extend the length of the timer
	 */
	public void extend(int value) {
		length += value;
	}
	
	/**
	 * Set the length of the timer to the new value
	 */
	public void setLength(int value) {
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

	public int getLength() {
		return length;
	}
	
	public int getCounter() {
		return counter;
	}
	
	/**
	 * Set the value of the counter of this timer
	 */
	public void setCounter(int value) {
		counter = value;
	}
	
	@Override
	public String toString() {
		if (isEndless())
			return "-";
		
		return counter + " / " + length;
	}
	
}
