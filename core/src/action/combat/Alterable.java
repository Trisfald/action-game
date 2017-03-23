package action.combat;

import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;


/**
 * Can be altered by an effect and an impact
 * 
 * @author Andrea
 *
 */
public interface Alterable {

	public void takeImpact(Impact impact);
	
	public void takeEffect(Effect effect);
	
	public void takeEffect(Effect effect, AlterationTarget target);
	
	/**
	 * @return True if the damage has been completely assorbed
	 */
	public boolean takeDamage(Damage damage);
	
	public void apply(Effect effect);
	
	public void remove(Effect effect);
	
	public void consumeCharge(ChargeTrigger trigger);
	
	/**
	 * @return True if maintaining cost has been paid
	 */
	public boolean sustainEffect(float cost);
	
	public void removeToggleEffects();
	
    public boolean hasActiveToggleEffects();
	
	
	/**
	 * The type of target that will be affected by an alteration
	 * 
	 * @author Andrea
	 */
	public enum AlterationTarget {
		
		BODY,
		WEAPON,
		SHIELD;
		
	}
}
