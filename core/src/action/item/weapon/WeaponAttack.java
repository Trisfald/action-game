package action.item.weapon;

import java.util.List;

import action.combat.BlockLevel;
import action.combat.Damage.DamageLoader;
import action.combat.Knockback.KnockbackLoader;
import action.combat.effect.Effect.EffectLoader;
import action.entity.enums.AttackType;
import action.entity.enums.AttackType.AttackMotionType;
import action.spawner.SpawnerInfo;

/**
 * Manages the attack feature
 *
 * @author Andrea
 */
public class WeaponAttack {

    private AttackType type;
    private AttackMotionType style;
    private BlockLevel level;
    private List<EffectLoader> effects;
    private List<DamageLoader> damages;
    private KnockbackLoader knockback;
    private float crushPower;
    private float dashLength;
    private float speed;
    private float chargeTime;
    private int hitBoxWidth;
    private int hitBoxLength;
    private int targets;
    private float cost;
    /** Spawner for ranged attacks */
   	private SpawnerInfo spawner;
    
    public WeaponAttack(WeaponAttackInfo info) {
        type = info.type;
        style = info.style;
        level = info.level;
        effects = info.effects;
        damages = info.damages;
        knockback = info.knockback;
        crushPower = info.crushPower;
        dashLength = info.dashLength;
        speed = info.speed;
        chargeTime = info.chargeTime;
        hitBoxWidth = info.hitBoxWidth;
        hitBoxLength = info.hitBoxLength;
        targets = info.targets;
        cost = info.cost;
        spawner = info.spawner;
    }

    public AttackType getType() {
        return type;
    }
    
    public AttackMotionType getStyle() {
        return style;
    }
    
    public BlockLevel getBlockLevel() {
    	return level;
    }

	public float getCrushPower() {
		return crushPower;
	}
	
	public float getDashLength() {
		return dashLength;
	}
	
	public float getSpeed() {
		return speed;
	}
	
    public float getCost() {
    	return cost;
    }
    
    public KnockbackLoader getKnockback() {
    	return knockback;
    }

	public int getHitBoxWidth() {
		return hitBoxWidth;
	}

	public int getHitBoxLength() {
		return hitBoxLength;
	}
	
	public List<EffectLoader> getAlters() {
		return effects;
	}
	
	public List<DamageLoader> getDamages() {
		return damages;
	}
	
	public boolean isRanged() {
		return spawner != null;
	}
	
	public SpawnerInfo getSpawner() {
		return spawner;
	}
	
	public float getRange() {
		if (isRanged())
			return spawner.getRange();
		return hitBoxLength + dashLength;
	}
	
	public float getChargeTime() {
		return chargeTime;
	}

	public int getAttackTargets() {
		return targets;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class WeaponAttackInfo {

	    public final AttackType type;
	    public final AttackMotionType style;
	    public final BlockLevel level;
	    public final List<DamageLoader> damages;
	    public final List<EffectLoader> effects;
	    public final KnockbackLoader knockback;
	    public final float crushPower;
	    public final float dashLength;
	    public final float speed;
	    public final float chargeTime;
	    public final int hitBoxWidth;
	    public final int hitBoxLength;
	    public final int targets;
	    public final float cost;
	    public final SpawnerInfo spawner;
	    
		public WeaponAttackInfo(AttackType type, AttackMotionType style, BlockLevel level, List<DamageLoader> damages,
				List<EffectLoader> effects, KnockbackLoader knockback, float crushPower, float dashLength, float speed,
				float chargeTime, int hitBoxWidth, int hitBoxLength, int targets, float cost, SpawnerInfo spawner) {
			this.type = type;
			this.style = style;
			this.level = level;
			this.damages = damages;
			this.effects = effects;
			this.knockback = knockback;
			this.crushPower = crushPower;
			this.dashLength = dashLength;
			this.speed = speed;
			this.chargeTime = chargeTime;
			this.hitBoxWidth = hitBoxWidth;
			this.hitBoxLength = hitBoxLength;
			this.targets = targets;
			this.cost = cost;
			this.spawner = spawner;
		}
	    
	}

}
