package action.combat.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import action.combat.Alterable;
import action.combat.effect.Effect.EffectStacking;

/**
 * 
 * @author Andrea
 *
 */
public abstract class EffectManager {

	protected List<Effect> effects = new ArrayList<Effect>();
	private Alterable owner;
	/** Number of toggling effects active */
	private int toggleCount = 0;
	
	public EffectManager(Alterable owner) {
		this.owner = owner;
	}
	
	public void add(Effect effect) {
		switch (effect.getStacking()) {
			case YES:
				/* If stackable add in any case */
				effects.add(effect);
				break;
			case NO:
				/* Check for the presence of the same kind of effect */
				if (effects.contains(effect))
					effects.get(effects.indexOf(effect)).renew(effect.getDuration(), effect.getCharges());
				else
					effects.add(effect);
				break;
			case TOGGLE:
				/* Add only if not present */
				if (!effects.contains(effect)) {
					effects.add(effect);
					toggleCount++;
				}
				break;
		}
	}
	
    public void update(float delta) {
        for (Iterator<Effect> iter = effects.iterator(); iter.hasNext();) {
        	Effect effect = iter.next();
        	if (!effect.update(owner, delta)) {
        		/* Update counter */
        		if (effect.getStacking() == EffectStacking.TOGGLE)
        			toggleCount--;
        		/* Remove the effect */
        		owner.remove(effect);
        		iter.remove();
        	}
        }
    }
    
    /**
     * @return All current effects
     */
    public List<Effect> getEffects() {
    	return effects;
    }
    
    /**
     * @return True if there are active toggle effects
     */
    public boolean hasActiveToggleEffects() {
    	return toggleCount > 0;
    }
    
    /**
     * Removes all toggling effects
     */
    public void removeToggleEffects() {
        for (Iterator<Effect> iter = effects.iterator(); iter.hasNext();) {
        	Effect effect = iter.next();
    		if (effect.getStacking() == EffectStacking.TOGGLE) {
        		/* Remove the effect */
    			owner.remove(effect);
    			iter.remove();
    		}
        }
        toggleCount = 0;
    }
    
    public final void apply(Effect effect) {
    	switch(effect.getEffectType()) {
    		case STATUS:
    			applyStatus((Status) effect);
    			break;
    		case BONUS_DMG:
    			applyBonusDmg((BonusDamage) effect);
    			break;
    		case BONUS_EFFECT:
    			applyBonusEffect((BonusEffect) effect);
    			break;
			default:
				break;
    	}
    }
    
    public final void remove(Effect effect) {
    	switch(effect.getEffectType()) {
    		case STATUS:
    			removeStatus((Status) effect);
    			break;
    		case BONUS_DMG:
    			removeBonusDmg((BonusDamage) effect);
    			break;
    		case BONUS_EFFECT:
    			removeBonusEffect((BonusEffect) effect);
    			break;
			default:
				break;
    	}
    }
    
	public void consumeCharge(ChargeTrigger trigger) {
		for (Effect x : effects)
			x.consumeCharge(trigger);
	}
	
    protected final void applyStatus(Status status) {
		switch(status.getType()) {
			case PARALYZE:
				applyParalyze();
				break;
		}
	}

    protected final void removeStatus(Status status) {
		switch(status.getType()) {
			case PARALYZE:
				removeParalyze();
				break;
		}	
	}
	
    protected void applyBonusDmg(BonusDamage bonus) {
		
	}
	
    protected void removeBonusDmg(BonusDamage bonus) {
		
	}
	
    protected void applyBonusEffect(BonusEffect bonus) {
		
	}
	
    protected void removeBonusEffect(BonusEffect bonus) {
		
	}

    protected void applyParalyze() {
    	
	}
	
	protected void removeParalyze() {

	}
    
}
