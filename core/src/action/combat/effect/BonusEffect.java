package action.combat.effect;

import action.combat.Alterable.AlterationTarget;
import action.utility.TextureRRef;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class BonusEffect extends OverTimeEffect {

	private EffectLoader bonus;
	
	public BonusEffect(float duration, int charges, ChargeTrigger trigger, EffectStacking stackable, float propagation,
			float sustain, TextureRRef icon, EffectLoader bonus) {
		super(duration, charges, trigger, stackable, propagation, sustain, icon);
		this.bonus = bonus;
	}
	
	public BonusEffect(BonusEffectLoader loader) {
		this(loader.superInfo.duration, loader.superInfo.charges, loader.superInfo.trigger, loader.superInfo.stackable, 
				loader.superInfo.propagation, loader.superInfo.sustain, loader.superInfo.icon, loader.bonus);
	}
	
	public EffectLoader getBonus() {
		return bonus;
	}
	
	@Override
	public Effect copy() {
		return new BonusEffect(duration.getLength(), charges, trigger, stackable, propagation, sustain, iconID, bonus);
	}

	@Override
	public EffectType getEffectType() {
		return EffectType.BONUS_EFFECT;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BonusEffect) {
			BonusEffect o = (BonusEffect) other;
			return (o.bonus.type == bonus.type);
		}
		return false;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class BonusEffectLoader extends EffectLoader {
		
		public final OverTimeInfo superInfo;
		public final EffectLoader bonus;
		
		public BonusEffectLoader(EffectType effectType, float probability, AlterationTarget target, 
				OverTimeInfo superInfo, EffectLoader bonus) {
			super(effectType, probability, target);
			this.superInfo = superInfo;
			this.bonus = bonus;
		}

		@Override
		public Effect load() {
			return new BonusEffect(this);
		}
		
		public static EffectLoader loadFromXML(Element element) {
		    return new BonusEffectLoader(
		    		EffectType.BONUS_EFFECT,
		    		element.getFloatAttribute("probability", 1),
		    		AlterationTarget.valueOf(element.getAttribute("target", AlterationTarget.BODY.name())),
		    		OverTimeInfo.loadFromXML(element),
		    		EffectLoader.loadFromXML(element.getChildByName("effect"))
		    		);
		}
		
	}
}
