package action.input;

/**
 * @author Andrea
 */
public class KeyController {
	
	private int code;
	private float timer = 0;
	private static final float DOWN_TRIGGER_TIME = 250;
	private boolean active = false;
	/** Lock to prevent past input interference */
	private boolean lock = true;
	
	public KeyController(int code) {
		this.code = code;
	}
	
	public int getKeyCode() {
		return code;
	}
	
	/**
	 * Clear the state of this controller
	 */
	public void clear() {
		active = false;
		timer = 0;
	}
	
	/**
	 * Update the status of the key, call once per cycle
	 * @return The status of the key
	 */
	public KeyStatus keyStatus(boolean pressed, float delta) {

		/* Check lock */
		if (lock)
			if (!pressed)
				lock = false;
			else
				return KeyStatus.UP;
		
		/* Key not yet pressed */
		if (!active) {
			if (pressed) {
				active = true;
			}
			return KeyStatus.UP;
		}

		/* Key already pressed */
		timer += delta;
		if (!pressed) {
			active = false;
			if (timer < DOWN_TRIGGER_TIME) {
				timer = 0;
				return KeyStatus.PRESSED;
			}
			timer = 0;
			return KeyStatus.DOWN;
		}
		
		/* Key still pressed */
		if (timer < DOWN_TRIGGER_TIME)
			return KeyStatus.UP;
		
		return KeyStatus.DOWN;
	}
	
	public enum KeyStatus {
		
		PRESSED,
		DOWN,
		UP;
		
	}
	
}