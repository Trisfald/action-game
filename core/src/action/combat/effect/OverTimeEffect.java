package action.combat.effect;

import action.combat.Alterable;
import action.core.Game;
import action.interfaces.GlobalVar;
import action.utility.TextureRRef;
import action.utility.timer.Timer;

import com.badlogic.gdx.utils.XmlReader.Element;



/**
 * Class that defines an effect affecting one creature or item for a fixed duration
 * 
 * @author andrea
 */
public abstract class OverTimeEffect implements Effect {
	
	protected Timer duration;
	/** Number of charges (negative values indicate an infinite amount of charges) */
	protected int charges;
	/** Type of event that consumes a charge */
	protected ChargeTrigger trigger;
	/** Describe what happens when copies of the same effect overlaps */
	protected EffectStacking stackable;
	/** Probability to propagate to other alterable things */
	protected float propagation;
	/** True if the effect has been applied */
	protected boolean applied;
	/** Cost (per second) to keep the effect going */
	protected float sustain;
	/* ID for the icon */
	protected TextureRRef iconID;

	public OverTimeEffect(float duration, int charges, ChargeTrigger trigger, EffectStacking stackable, float propagation,
			float sustain, TextureRRef icon) {
		this.duration = new Timer(duration);
		this.charges = charges;
		this.stackable = stackable;
		this.propagation = propagation;
		this.sustain = sustain;
		this.iconID = icon;
	}
	
	public OverTimeEffect(OverTimeInfo info) {
		this(info.duration, info.charges, info.trigger, info.stackable, info.propagation, info.sustain, info.icon);
	}
	
	@Override
	public boolean update(Alterable owner, float delta) {
		/* Remove if maintenance fails */
		if (!maintain(owner, delta))
			return false;

		if (!applied)
			apply(owner);
		
		return true;
	}
	
	private void apply(Alterable owner) {
		owner.apply(this);
		applied = true;
	}

	/**
	 * @return False if the effect can't maintain itself anymore
	 */
	public boolean maintain(Alterable owner, float delta) {
		/* Charges check */
		if (charges == 0)
			return false;
		
		/* Sustain check */
		if (!owner.sustainEffect(sustain * delta / 1000))
			return false;	
		
		/* Duration check */
		duration.update(delta);
		return !duration.expired();
	}
	
	@Override
	public boolean consumeCharge(ChargeTrigger trigger) {
		/* Always success if the charges are infinite or not of subject of this trigger */
		if (charges < 0 || this.trigger != trigger)
			return true;
		
		if (charges > 0) {
			charges--;
			return true;
		}
		return false;
	}
	
	@Override
	public EffectStacking getStacking() {
		return stackable;
	}

	@Override
	public float getDuration() {
		return duration.getLength();
	}

	@Override
	public int getCharges() {
		return charges;
	}
	
	@Override
	public void renew(float duration, int charges) {
		/* Recharge both duration and charges only if they are not infinite and less than the new amount */
		if (!this.duration.isEndless()) {
			this.duration.update(-duration); 
			if (this.duration.getCounter() < 0)
				this.duration.setCounter(0);
		}
		if (this.charges >= 0 && this.charges < charges)
			this.charges = charges; 
	}
	
	@Override
	public boolean propagates() {
		return (Game.master().random() < propagation);
	}
	
	@Override
	public float getProgress() {
		return duration.getProgress();
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class OverTimeInfo {
		
		public final float duration;
		public final int charges;
		public final ChargeTrigger trigger;
		public final EffectStacking stackable;
		public final float propagation;
		public final float sustain;
		public final TextureRRef icon;
		
		public OverTimeInfo(float duration, int charges, ChargeTrigger trigger, EffectStacking stackable, float propagation,
				float sustain, TextureRRef icon) {
			this.duration = duration;
			this.charges = charges;
			this.trigger = trigger;
			this.stackable = stackable;
			this.propagation = propagation;
			this.sustain = sustain;
			this.icon = icon;
		}
		
		public static OverTimeInfo loadFromXML(Element element) {
			return new OverTimeInfo(
					element.getFloatAttribute("duration", -1),
					element.getIntAttribute("charges", -1),
					ChargeTrigger.valueOf(element.getAttribute("trigger", ChargeTrigger.NONE.name())),
					EffectStacking.valueOf(element.getAttribute("stackable")),
	    			element.getFloatAttribute("propagation", 0),
	    			element.getFloatAttribute("sustain", 0),
	    			new TextureRRef(element.getAttribute("icon", GlobalVar.DEFAULT_EFFECT_ICON))
					);
		}
	
	}
	
}
