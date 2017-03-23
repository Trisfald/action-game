package action.utility.timer;

/**
 * A cyclic timer going up and down between the 0 and the length
 * 
 * @author Andrea
 */
public class TimerUpDown extends Timer {

	/** True if the timer is counting up, false if it's counting down */
	private boolean up = true;
	
	public TimerUpDown(float length) {
		super(length);
	}
	
	@Override
	public void restart() {
		super.restart();
		up = true;
	}
	
	@Override
	public void update(float delta) {
		if (up)
			counter += delta;
		else
			counter -= delta;
		
		/* Endless timers don't need extra checks */
		if (isEndless())
			return;
		
		if (up) {
			if (counter > length) {
				counter = length;
				up = false;
			}
		}
		else {
			if (counter < 0) {
				counter = 0;
				up = true;
			}
		}
		
	}
	
}
