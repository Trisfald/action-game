package action.combat.effect;

import action.combat.Alterable;
import action.combat.Alterable.AlterationTarget;
import action.combat.effect.BonusDamage.BonusDamageLoader;
import action.combat.effect.BonusEffect.BonusEffectLoader;
import action.combat.effect.DoT.DoTLoader;
import action.combat.effect.Status.StatusLoader;
import action.utility.flag.StatusFlag.StatusGiver;

import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * @author Andrea
 */
public interface Effect extends StatusGiver {
	
	/**
	 * Update the effect's actions
	 * 
	 * @return False if the effect has ended
	 */
	public boolean update(Alterable owner, float delta);
	
	public float getDuration();
	
	public float getProgress();
	
	public int getCharges();
	
	/**
	 * @return True if the effect propagates through the defence
	 */
	public boolean propagates();
	
	/**
	 * @return False if was impossible to consume a charge
	 */
	public boolean consumeCharge(ChargeTrigger trigger);
	
	/**
	 * @return How this interact with other effects of the same kind
	 */
	public EffectStacking getStacking();
	
	/**
	 * Prolong the duration / charges of the effects up to the specified values
	 */
	public void renew(float duration, int charges);

	/**
	 * @return A copy of the object
	 */
	public Effect copy();
	
	public EffectType getEffectType();
	
	
	/**
	 * @author Andrea
	 */
	public enum EffectType {
		
		STATUS,
		DOT,
		BONUS_DMG,
		BONUS_EFFECT;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum EffectStacking {
		
		YES,
		NO,
		TOGGLE;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public abstract static class EffectLoader {
		
		public final EffectType type;
		/** Probability that the effect will take place */
		public final float probability;
		/** Which part the effect will target */
		public final AlterationTarget target;

		public EffectLoader(EffectType type, float probability, AlterationTarget target) {
			this.type = type;
			this.probability = probability;
			this.target = target;
		}
		
		/**
		 * Instantiate and return an effect
		 */
		public abstract Effect load();
		
		public static EffectLoader loadFromXML(Element element) {
	    	switch (EffectType.valueOf(element.getAttribute("effectType"))) {
	    		case STATUS:
	    			return StatusLoader.loadFromXML(element);
	    		case DOT:
	    			return DoTLoader.loadFromXML(element);
	    		case BONUS_DMG:
	    			return BonusDamageLoader.loadFromXML(element);
	    		case BONUS_EFFECT:
	    			return BonusEffectLoader.loadFromXML(element);
				default:
	    			return null;
	    	}
		}
	}

	
}
