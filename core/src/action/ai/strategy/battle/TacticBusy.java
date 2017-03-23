package action.ai.strategy.battle;

import java.util.ArrayList;
import java.util.List;

import action.ai.behaviour.Behaviour.BehaviourType;
import action.ai.behaviour.micro.MeleeAttack;
import action.ai.behaviour.micro.Shield;
import action.ai.behaviour.micro.Shoot;
import action.ai.personality.Personality.AiStatType;
import action.ai.strategy.BattleStrategy;
import action.ai.strategy.Tactic;
import action.combat.BlockLevel;
import action.entity.Entity;
import action.entity.being.creature.Creature;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.enums.AttackType;
import action.entity.enums.BlockType;
import action.hitbox.DangerZone;
import action.interfaces.GlobalVar;
import action.utility.Statistic;
import action.world.Collision;

/**
 * This class represents all reaction to external world's events while a creature is performing a precise action
 * 
 * @author Andrea
 *
 */
public class TacticBusy implements Tactic {
	
	protected BattleStrategy strategy;
	
	/** Variation in the stating score of an attack */
	private static final float ATTACK_START_SCORE_VARIATION = 0.15f;
	/** Percentuage of the maximum stamina needed to block */
	private static final float BLOCK_MIN_STAMINA = 0.1f;
	/** How much actions are important in attack choice */
	private static final float ACTION_BASE_INFLUENCE = 7;
	/** Starting bonus to attack score */
	private static final float ATTACK_STARTING_BONUS = -30;
	/** Bonus to attack score per second passed without attacking (will be used into a formula) */
	private static final float ATTACK_BONUS_SECOND = 4000;
	/** Distance from spot range and maximum shooting range */
	private static final float SPOT_TO_RANGED = 50;
	
	public TacticBusy(BattleStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FIGHT;
	}
	
	@Override
	public void decide(Entity enemy) {

	}
	
	@Override
	public void conclude() {
		
	}
	
	/**
	 * @return All attack types available
	 */
	protected List<AttackEval> getPossibleAttacks(boolean ranged) {
		List<AttackEval> list = new ArrayList<AttackEval>();
		/* Try for each type of attack if it's enabled */
		for (AttackType x : AttackType.values())
			if ((strategy.getAi().getMirror().hasAttack(x)) && 
					(strategy.getAi().getMirror().isAttackRanged(x) == ranged))
				list.add(new AttackEval(x, getAttackStartScore(), ATTACK_START_SCORE_VARIATION));
	
		return list;
	}
	
	private float getAttackStartScore() {
		return (float) Math.sqrt(strategy.getAttackTimer().getCounter() * ATTACK_BONUS_SECOND * 
				strategy.getAi().getPersonality().getStat(AiStatType.AGGRESSIVITY).factor()) + ATTACK_STARTING_BONUS;
	}
	
	/**
	 * Rate total attack convenience
	 */
	private void rateAttack(AttackEval attack, Entity enemy) {
		attack.score += rateAttackCost(attack);
		attack.score += rateAttackByLife(attack, enemy);
		attack.score += rateAttackByAction(attack, enemy);
	}	
	
	/**
	 * Rate attack cost convenience
	 */
	private float rateAttackCost(AttackEval attack) {
		float afterStamina = strategy.getAi().getMirror().getStat(CreatureStatType.STAMINA).value() - 
				strategy.getAi().getMirror().getAttackCost(attack.type);
		if (afterStamina < 0)
			/* Insufficient stamina, put an huge penality */
			return -10000;
	
		/* Stamina is sufficient but how does the cost compare with creature's aggressivity? */
		float ratio = afterStamina / strategy.getAi().getMirror().getStat(CreatureStatType.STAMINA).maximum();
		
		/* Formula: -100 * (1-ratioAfterSTA) / AGG.factor */
		return -100 * (1-ratio) / strategy.getAi().getPersonality().getStat(AiStatType.AGGRESSIVITY).factor();
	}
	
	/**
	 * Rate attack importance in relation to the enemy life
	 */
	private float rateAttackByLife(AttackEval attack, Entity enemy) {
		/* Formula: 100 * (1-ratioHP) * INT.factor */
		return 100 * (1 - enemy.getAppearance().ratioHP()) * 
				strategy.getAi().getPersonality().getStat(AiStatType.INTELLIGENCE).factor();
	}
	
	/**
	 * Rate attack importance in relation to the enemy current action
	 */
	private float rateAttackByAction(AttackEval attack, Entity enemy) {	
		/* Intelligence factor */
		float factor = strategy.getAi().getPersonality().getStat(AiStatType.INTELLIGENCE).factor();
		
		switch (enemy.getAppearance().getActionType()) {
			case BLOCK:
				/* Malus, but less for strong attacks */
				return -2 * ACTION_BASE_INFLUENCE / (attack.type.getPowerMod() * factor);
			case MELEE: case EMPOWERED_MELEE:
				if (enemy.getAppearance().isRecovering())
					/* Bonus, enemy is recovering from the attack */
					return ACTION_BASE_INFLUENCE * attack.type.getPowerMod() * factor;
				else
					/* Malus, better not be vulnerable while the enemy is attacking */
					return -2 * ACTION_BASE_INFLUENCE * factor;
			case HOLD:
				/* Bonus, enemy can't do anything */
				return ACTION_BASE_INFLUENCE * attack.type.getPowerMod() * factor;
			case DODGE:
				if (enemy.getAppearance().isRecovering())
					/* Bonus, take advantage of the recovering window */ 
					return ACTION_BASE_INFLUENCE * attack.type.getPowerMod() * factor;
				else
					/* Malus, enemy will probably dodge */
					return -2 * ACTION_BASE_INFLUENCE * factor;
			default:
				return 0;
		}
	}
	
	/**
	 * Choose the best attack available
	 * @param enemy
	 * @param ranged True if the attack must be ranged, false if the attack must be melee
	 * @return The attack type
	 */
	private AttackType chooseAttack(Entity enemy, boolean ranged) {
		/* Create a fake attack eval so that to be chosen, one real attack must have score positive */
		AttackEval chosen = new AttackEval(null);
		List<AttackEval> list = getPossibleAttacks(ranged);
		
		for (AttackEval x : list) {
			rateAttack(x, enemy);
			/* Score must be superior to the previous choice */
			if (x.score > chosen.score)
				chosen = x;
		}

		return chosen.type;
	}
	
	/**
	 * @return The minimum combat distance wanted by this creature
	 */
	protected float getCombatMinDistance() {
		return -1;
	}
	
	/**
	 * @return The maximum combat distance wanted by this creature
	 */
	protected float getCombatMaxDistance() {
		switch (strategy.getTacticPosition()) {
			case MELEE:
				return GlobalVar.BASE_COMBAT_MAX_DISTANCE / strategy.getAi().getPersonality().getStat(AiStatType.AGGRESSIVITY).factor();
			case RANGED:
				return strategy.getAi().getPersonality().getSpotDistance() - SPOT_TO_RANGED;
			default:
				return Float.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * @return The distance at which the creature starts moving with the guard high
	 */
	protected float getGuardDistance() {
		return getCombatMaxDistance() * 1.5f;
	}
	
	/**
	 * @return True if an attack behaviour has been decided
	 */
	protected boolean decideAttack(Entity enemy) {
		if (strategy.getTacticPosition() == TacticPosition.MELEE)
			return decideMelee(enemy);
		else
			return decideShoot(enemy);
	}
	
	/**
	 * @return True if a melee attack behaviour has been decided
	 */
	protected boolean decideMelee(Entity enemy) {
		/* Choose an attack */
		AttackType attack = chooseAttack(enemy, false);
		/* If attack is valid we found the behaviour */
		if (attack != null) {
			strategy.setBehaviour(new MeleeAttack(strategy.getAi(), enemy, attack));
			return true;
		}
		return false;
	}
	
	/**
	 * @return True if a ranged attack behaviour has been decided
	 */
	protected boolean decideShoot(Entity enemy) {
		/* Choose an attack */
		AttackType attack = chooseAttack(enemy, true);
		/* If attack is valid we found the behaviour */
		if (attack != null) {
			strategy.setBehaviour(new Shoot(strategy.getAi(), enemy, attack));
			return true;
		}
		return false;
	}
	
	/**
	 * @return True if a defensive behaviour has been decided
	 */
	protected boolean decideBlock() {
		/* Must be able to block */
		if (!strategy.getAi().getMirror().canBlock())
			return false;
		
		/* Check timer */
		if (!strategy.getDangerTimer().expired())
			return false;
		strategy.getDangerTimer().restart();
		
		/* Must have enough stamina */
		if (strategy.getAi().getMirror().getStat(CreatureStatType.STAMINA).value() < getMinBlockStamina())
			return false;
		
		Creature owner = strategy.getAi().getOwner();
		for (DangerZone x : owner.getWorld().getDangers(owner, strategy.getAi().getPersonality().getSpotDistance()))
			if (x.isMenacing(owner) && 
					x.getBlockLevel().value() <= BlockLevel.SHIELD.value() && 
					Collision.collide(owner.getShape(), x.getHitBox().getShape())) {
				/* Check if the creature is able to react to the danger */
				if (Math.random() < GlobalVar.BASE_BLOCK_PROBABILITY * strategy.getAi().getPersonality().getStat(AiStatType.DEFENCE).factor()) {
					strategy.setBehaviour(new Shield(strategy.getAi(), x));
					return true;
				}
			}
		return false;
	}
	
	/**
	 * @return The minimum amount of stamina required to try a block
	 */
	private float getMinBlockStamina() {
		/* Base cost */
		float cost = strategy.getAi().getMirror().getStat(CreatureStatType.STAMINA).maximum() *
				BLOCK_MIN_STAMINA;
		/* Already perfoming a block, so don't need to pay the starting cost */
		if (strategy.getAi().getMirror().hasStartedBlock())
			return cost;
		
		/* Add starting block cost */
		return cost + strategy.getAi().getMirror().getBlockCost(BlockType.STANDARD);
	}
	
	/**
	 * @return True if the creature must keep his guard high
	 */
	protected boolean decideGuarded() {	
		/* Calculate the stamina after the possible block */
		float afterStamina = strategy.getAi().getMirror().getStat(CreatureStatType.STAMINA).value();
		/* No need to take into account block cost if already started */
		if (!strategy.getAi().getMirror().hasStartedBlock())
				afterStamina -= strategy.getAi().getMirror().getBlockCost(BlockType.STANDARD);
		
		/* Get a percentuage value */
		afterStamina /= strategy.getAi().getMirror().getStat(CreatureStatType.STAMINA).maximum();
		
		return afterStamina >= GlobalVar.BASE_GUARDED_STAMINA / strategy.getAi().getPersonality().getStat(AiStatType.DEFENCE).factor();
	}
	
	
	
	/**
	 * Class to evaluate the goodness of an attack
	 * 
	 * @author Andrea
	 */
	private static class AttackEval {
		
		public AttackType type;
		public float score;
		
		public AttackEval(AttackType type, float score) {
			this.type = type;
			this.score = score;
		}
		
		public AttackEval(AttackType type, float score, float variation) {
			this(type, Statistic.randomizer(score, variation));
		}
		
		public AttackEval(AttackType type) {
			this(type, 0);
		}
		
	}
	
}


