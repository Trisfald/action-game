package action.combat;

/**
 * @author Andrea
 */
public enum HitLevel {
	
	ALL(0),
	WEAPON(1),
	PROJECTILE(2),
	ZONE(3),
	IMPOSSIBLE(4);
	
	private final int value;
	
	HitLevel(int value) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
	
}
