package action.combat;
/**
 * @author Andrea
 */
public enum BlockLevel {

	ALL(0),
	WEAPON(1),
	SHIELD(2),
	ARMOR(3),
	BARRIER(4),
	IMPOSSIBLE(5);
	
	private final int value;
	
	BlockLevel(int value) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
}