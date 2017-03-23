package action.combat.effect;

import action.combat.Alterable.AlterationTarget;
import action.utility.Statistic;
import action.utility.TextureRRef;

import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * 
 * @author Andrea
 *
 */
public class Status extends OverTimeEffect {

	private StatusType type;
	private float force;
	
	/**
	 * Standard status
	 */
	public Status(float duration, int charges, ChargeTrigger trigger, EffectStacking stackable, float propagation, float sustain, TextureRRef icon, 
			StatusType type, float force, float variation) {
		super(duration, charges, trigger, stackable, propagation, sustain, icon);
		this.type = type;
		this.force = Statistic.randomizer(force, variation);
	}
	
	
	public Status(StatusLoader loader) {
		this(loader.superInfo.duration, loader.superInfo.charges, loader.superInfo.trigger, loader.superInfo.stackable,
				loader.superInfo.propagation, loader.superInfo.sustain, loader.superInfo.icon, loader.type, loader.force, loader.variation);
	}

	public StatusType getType() {
		return type;
	}

	public float getForce() {
		return force;
	}
	
	@Override
	public EffectType getEffectType() {
		return EffectType.STATUS;
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Status) {
			Status o = (Status) other;
			return (type == o.type);
		}
		return false;
	}
	
	/**
	 * @return A copy of the object
	 */
	@Override
	public Status copy() {
		return new Status(duration.getLength(), charges, trigger, stackable, propagation, sustain, iconID, type, force, 0);
	}


	/**
	 * @author Andrea
	 */
	public static class StatusLoader extends EffectLoader {
		
		public final OverTimeInfo superInfo;
		public final StatusType type;
		public final float force;
		public final float variation;
		
		public StatusLoader(EffectType effectType, float probability, AlterationTarget target, OverTimeInfo superInfo, 
				StatusType type, float force, float variation) {
			super(effectType, probability, target);
			this.superInfo = superInfo;
			this.type = type;
			this.force = force;
			this.variation = variation;
		}

		@Override
		public Effect load() {
			return new Status(this);
		}
		
		public static EffectLoader loadFromXML(Element element) {
		    return new StatusLoader(
		    		EffectType.STATUS,
		    		element.getFloatAttribute("probability", 1),
		    		AlterationTarget.valueOf(element.getAttribute("target", AlterationTarget.BODY.name())),
		    		OverTimeInfo.loadFromXML(element),
		    		StatusType.valueOf(element.getAttribute("type")),
	    			element.getFloatAttribute("force", 1),
	    			element.getFloatAttribute("variation", 0)
		    		);
		}
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum StatusType {
		
		PARALYZE;
		
	}
	
}
