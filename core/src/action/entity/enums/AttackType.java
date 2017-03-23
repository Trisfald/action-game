package action.entity.enums;

import action.spell.Spell.SpellCategory;



/**
 * Enumeration of all possible attack types
 *
 * @author Andrea
 */
public enum AttackType {
	
	WEAK(0),
	STRONG(1);
	
	private final int id;
	
	AttackType(int id) {
		this.id = id;
	}
	
	/**
	 * @return The unique id of the attack type
	 */
	public int id() {
		return id;
	}
	
	/**
	 * @return The corresponding empowered spell to this attack
	 */
	public SpellCategory getSpellCategory() {
		switch (this) {
			case WEAK:
				return SpellCategory.EMPOWER_WEAK;
			case STRONG:
				return SpellCategory.EMPOWER_STRONG;
			default:
				return null;
		}
	}
	
	/**
	 * @return An indication of how much the attack is strong
	 */
	public float getPowerMod() {
		switch (this) {
			case STRONG:
				return 2;
			default:
				return 1;
		}
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum AttackMotionType {
		
		MELEE,
		RANGED;
		
	}
	
}