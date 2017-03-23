package action.item.weapon;

import java.util.List;

import action.combat.Alterable;
import action.combat.BlockLevel;
import action.combat.Damage.DamageLoader;
import action.combat.Knockback.KnockbackLoader;
import action.combat.effect.Effect.EffectLoader;
import action.entity.enums.AttackType;
import action.entity.enums.AttackType.AttackMotionType;
import action.spawner.SpawnerInfo;
import action.utility.enums.Direction;

/**
 * An item capable of attacking
 * 
 * @author Andrea
 */
public interface Attacker extends Alterable {

	/**
	 * @return The attack style of the current attack
	 */
	public AttackMotionType getAttackStyle();
	
	/**
	 * @return The attack style if present, otherwise null
	 */
	public AttackMotionType getAttackStyle(AttackType type);
	
    public void resetAttack(Direction dir);

    public void setCurrentAttack(AttackType attack);
    
    public int getAttackHitBoxWidth();
    
    public int getAttackHitBoxLength();
    
	public float getCrushPower();
	
	public float getDashLength();
	
	public float getAttackSpeed();
	
	/**
	 * Tell the minimum level of blocking required to stop the impact
	 */
	public BlockLevel getBlockNeeded();
    
	public boolean isAlive();
	
	public float getAttackCost(AttackType type);
	
	public KnockbackLoader getKnockback();
	
    public List<EffectLoader> getAttackEffects();
    
    public List<DamageLoader> getDamages();

	public float getDashLength(AttackType type);

	public int getAttackHitBoxLength(AttackType type);
	
	public float getRange(AttackType type);

	public WeaponAttack getAttack(AttackType type);

	/**
	 * @return The spawner info
	 */
	public SpawnerInfo getSpawner();
	
	/**
	 * @return True if it has at least one ranged attack
	 */
	public boolean hasRanged();
	
	/**
	 * @return True if it has at least one melee attack
	 */
	public boolean hasMelee();

	/**
	 * @return The time needed (seconds) for charging this attack. Useful only for ranged attacks
	 */
	public float getChargeTime();
	
	/**
	 * @return The number of max targets hitted by this attack
	 */
	public int getAttackTargets();
	
}
