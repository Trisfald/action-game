package action.combat.effect;

import action.combat.Alterable;
import action.combat.Alterable.AlterationTarget;
import action.combat.Damage;
import action.combat.Damage.DamageType;
import action.utility.Statistic;
import action.utility.TextureRRef;

import com.badlogic.gdx.utils.XmlReader.Element;



/**
 * @author Andrea
 */
public class DoT extends OverTimeEffect {

	private float force;
	/** How many seconds pass between two ticks of damage */
	private float interval;
	private DamageType dmgType;
	/** Fraction at which the power increase each second */
	private float aging;
	/** Timer for intervals */
	private float timer = 0;
	
	public DoT(DoTLoader loader) {
		this(loader.superInfo.duration, loader.superInfo.charges, loader.superInfo.trigger, loader.superInfo.stackable,
				loader.superInfo.propagation, loader.superInfo.sustain, loader.superInfo.icon, loader.dmgType, 
				Statistic.randomizer(loader.force, loader.variation), loader.interval, loader.aging);
	}
	
	public DoT(float duration, int charges, ChargeTrigger trigger, EffectStacking stackable, float propagation, float sustain, TextureRRef icon,
			DamageType dmgType, float force, float interval, float aging) {
		super(duration, charges, trigger, stackable, propagation, sustain, icon);
		this.force = force;
		this.interval = interval;
		this.dmgType = dmgType;
		this.aging = aging;
	}
	
	@Override
	public boolean update(Alterable owner, float delta) {
		/* Remove if the duration expired */
		if (!maintain(owner, delta)) {
			return false;
		}
		
		age(delta);
		apply(owner, delta);
		
		return true;
	}
	
	public void apply(Alterable owner, float delta) {
		timer += delta;		
		for (; timer >= interval; timer -= interval)
			owner.takeDamage(new Damage(dmgType, force, 0));
	}
	
	public void age(float delta) {
		if (aging != 0)
			force += (force * (aging)) * (delta);
	}
	
	@Override
	public EffectType getEffectType() {
		return EffectType.DOT;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof DoT) {
			DoT o = (DoT) other;
			return (dmgType == o.dmgType);
		}
		return false;
	}

	/**
	 * @return A copy of the object
	 */
	@Override
	public DoT copy() {
		return new DoT(duration.getLength(), charges, trigger, stackable, propagation, sustain, iconID, dmgType, force, interval, aging);
	}
	
	

	/**
	 * @author Andrea
	 */
	public static class DoTLoader extends EffectLoader {
		
		public final OverTimeInfo superInfo;
		public final DamageType dmgType;
		public final float force;
		public final float variation;
		public final float interval;
		public final float aging;
		
		public DoTLoader(EffectType effectType, float probability, AlterationTarget target, OverTimeInfo superInfo, 
				DamageType dmgType, float force, float variation, float interval, float aging) {
			super(effectType, probability, target);
			this.superInfo = superInfo;
			this.dmgType = dmgType;
			this.force = force;
			this.variation = variation;
			this.interval = interval;
			this.aging = aging;
		}

		@Override
		public Effect load() {
			return new DoT(this);
		}
		
		public static EffectLoader loadFromXML(Element element) {
		    return new DoTLoader(
		    		EffectType.DOT,
		    		element.getFloatAttribute("probability", 1),
		    		AlterationTarget.valueOf(element.getAttribute("target", AlterationTarget.BODY.name())),
		    		OverTimeInfo.loadFromXML(element),
		    		DamageType.valueOf(element.getAttribute("type")),
	    			element.getFloatAttribute("force"),
	    			element.getFloatAttribute("variation", 0),
	    			element.getFloatAttribute("interval"),
	    			element.getFloatAttribute("aging", 0)
		    		);
		}
			
	}

}
