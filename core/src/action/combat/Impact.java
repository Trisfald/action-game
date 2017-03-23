package action.combat;

import java.util.ArrayList;
import java.util.List;

import action.combat.effect.Effect;
import action.hitbox.StaticHitBox;


/**
 * Class that defines an impact force that can hit and alter entities
 *
 * @author andrea
 */
public class Impact {

	private StaticHitBox staticHitBox;
	private BlockLevel level;
	private List<Damage> damages;
	private List<Effect> effects;
	private Float crushPower;
	private Knockback knockback;
	/** True if the impact hits only part of the armor, false if it hits all */
	private boolean selective;
	
	public Impact(List<Damage> damages, List<Effect> effects, Knockback knockback, StaticHitBox staticHitBox,
			BlockLevel level, Float crushPower, boolean selective) {
		this.damages = damages;
		this.effects = effects;
		this.knockback = knockback;
		this.staticHitBox = staticHitBox;
		this.level = level;
		this.crushPower = crushPower;
		this.selective = selective;
	}
	
	public StaticHitBox getHitBox() {
		return staticHitBox;
	}
	
	public BlockLevel getBlockLevel() {
		return level;
	}
	
	public float getCrushPower() {
		return crushPower;
	}
	
	/**
	 * @return True if the impact hits only part of the armor, false if it hits all
	 */
	public boolean isSelective() {
		return selective;
	}
	
	/**
	 * @return False if the impact does nothing
	 */
	public boolean exists() {
		if ((effects.isEmpty()) && (damages.isEmpty()) && (knockback == null))
			return false;
		return true;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public List<Damage> getDamages() {
		return damages;
	}
	
	public Knockback getKnockback() {
		return knockback;
	}
	
	public void removeKnockback() {
		knockback = null;
	}
	
	/**
	 * @return A copy of the impact
	 */
	public Impact copy() {
		return copy(this.staticHitBox);
	}

	/**
	 * @param staticHitBox The new hitbox
	 * @return A copy of the impact but with a new hitbox
	 */
	public Impact copy(StaticHitBox staticHitBox) {
		List<Damage> dmg = new ArrayList<Damage>();
		for (Damage x : damages)
			dmg.add(x.copy());
		
		List<Effect> eff = new ArrayList<Effect>();
		for (Effect x : effects)
			eff.add(x.copy());
		
		Knockback kb = null;
		if (knockback != null)
			kb = knockback.copy();
		
		return new Impact(dmg, eff, kb, staticHitBox, level, crushPower, selective);
	}
}
