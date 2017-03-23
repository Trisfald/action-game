package action.entity.enums;

/**
 * Enumeration of all possible busy levels for a creature
 *
 * @author Andrea
 */
public enum BusyLevel {
	
	FREE(0),
	SOFT_BUSY(1),
	HARD_BUSY(2);
	
	private final int value;
	
	BusyLevel(int value) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
}
