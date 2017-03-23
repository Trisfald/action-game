package action.combat.effect;

/**
 * All the possible triggers for consuming an effect's charge
 * @author Andrea
 */
public enum ChargeTrigger {

	DO_ATTACK,
	TAKE_IMPACT,
	/** Effects with this trigger will never consume a charge */
	NONE;
	
}
