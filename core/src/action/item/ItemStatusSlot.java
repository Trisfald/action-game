package action.item;

import action.item.Protection.ProtectionSlot;


/**
 * Slots for items that have a 'status'
 * 
 * @author Andrea
 */
public enum ItemStatusSlot {
	
	WEAPON,
	SHIELD,
	TORSO,
	LEGS,
	ARMS,
	HEAD;
	
	/**
	 * @return The protection slot corresponding to this object. Or null for item that are not protections
	 */
	public ProtectionSlot getProtection() {
		switch (this) {
			case TORSO:
				return ProtectionSlot.TORSO;
			case LEGS:
				return ProtectionSlot.LEGS;
			case ARMS:
				return ProtectionSlot.ARMS;
			case HEAD:
				return ProtectionSlot.HEAD;
			default:
				return null;
		}
	}
			
}