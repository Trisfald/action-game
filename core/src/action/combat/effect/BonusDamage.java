package action.combat.effect;

import action.combat.Alterable.AlterationTarget;
import action.combat.Damage.DamageLoader;
import action.utility.TextureRRef;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class BonusDamage extends OverTimeEffect {

	private DamageLoader bonus;
	
	public BonusDamage(float duration, int charges, ChargeTrigger trigger, EffectStacking stackable, 
			float propagation, float sustain, TextureRRef icon, DamageLoader bonus) {
		super(duration, charges, trigger, stackable, propagation, sustain, icon);
		this.bonus = bonus;
	}
	
	public BonusDamage(BonusDamageLoader loader) {
		this(loader.superInfo.duration, loader.superInfo.charges, loader.superInfo.trigger, loader.superInfo.stackable, 
				loader.superInfo.propagation, loader.superInfo.sustain, loader.superInfo.icon, loader.bonus);
	}
	
	@Override
	public Effect copy() {
		return new BonusDamage(duration.getLength(), charges, trigger, stackable, propagation, sustain, iconID, bonus);
	}

	@Override
	public EffectType getEffectType() {
		return EffectType.BONUS_DMG;
	}
	
	public DamageLoader getBonus() {
		return bonus;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BonusDamage) {
			BonusDamage o = (BonusDamage) other;
			return (bonus.dmgType == o.bonus.dmgType);
		}
		return false;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class BonusDamageLoader extends EffectLoader {
		
		public final OverTimeInfo superInfo;
		public final DamageLoader bonus;
		
		public BonusDamageLoader(EffectType effectType, float probability, AlterationTarget target, 
				OverTimeInfo superInfo, DamageLoader bonus) {
			super(effectType, probability, target);
			this.superInfo = superInfo;
			this.bonus = bonus;
		}

		@Override
		public Effect load() {
			return new BonusDamage(this);
		}
		
		public static EffectLoader loadFromXML(Element element) {
		    return new BonusDamageLoader(
		    		EffectType.BONUS_DMG,
		    		element.getFloatAttribute("probability", 1),
		    		AlterationTarget.valueOf(element.getAttribute("target", AlterationTarget.BODY.name())),
		    		OverTimeInfo.loadFromXML(element),
		    		DamageLoader.loadFromXML(element)
		    		);
		}
		
	}

}
