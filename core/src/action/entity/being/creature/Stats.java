package action.entity.being.creature;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import action.combat.Damage;
import action.combat.Impact;
import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;
import action.combat.effect.EffectManager;
import action.combat.effect.Status.StatusType;
import action.core.Game;
import action.entity.being.creature.Creature.CreatureInfo;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.AttackType;
import action.entity.enums.BlockType;
import action.entity.enums.BusyLevel;
import action.entity.enums.MagicSkill;
import action.interfaces.GlobalVar;
import action.interfaces.StatsMemory;
import action.utility.Body;
import action.utility.Body.BodyStatType;
import action.utility.Body.DefenceType;
import action.utility.Statistic;
import action.utility.Statistic.StatisticInfo;
import action.utility.flag.Flag;
import action.utility.timer.Timer;


/**
 * An utility to manage creature's statistics
 *
 * @author Andrea
 */
public class Stats implements StatsMemory {
    
	private Creature owner;
	private Map<CreatureStatType, Statistic> stats = new EnumMap<CreatureStatType, Statistic>(CreatureStatType.class);
	private Map<StatusType, Flag> flags = new EnumMap<StatusType, Flag>(StatusType.class);
	private EffectManager effectManager;
	private Body body;
	/** The Creature magical abilities */
	private MagicSkill magicSkill;
	/** True if quick cast is active */
	private boolean quickCastActive;
	/** Timer to see if the creature is in combat */
	private Timer combatTimer = new Timer(GlobalVar.COMBAT_TIMER_LENGTH);
	/** All the effective damage (real dmg * dmgtype mod) accumulated since the past update cycle */
	private float effectiveDmg = 0;
	private float chargeProgress = -1;
    
    public Stats(Creature owner, CreatureInfo info) {
    	this.owner = owner;
    	this.body = new Body(info.body);
    	for (Map.Entry<CreatureStatType, StatisticInfo> x : info.stats.entrySet())
    		stats.put(x.getKey(), new Statistic(x.getValue()));   
    	for (StatusType x : StatusType.values())
    		flags.put(x, new Flag());
    	effectManager = new StatsEffectManager(this.owner);
    	this.magicSkill = info.magicSkill;
    }
    
    @Override
	public Statistic getStat(CreatureStatType type) {
    	return stats.get(type);
    }
    
    public Statistic getStat(BodyStatType type) {
    	return body.getStat(type);
    }

	@Override
	public Statistic getStat(DefenceType type) {
    	return body.getStat(type);
	}
    
    public Flag getFlag(StatusType type) {
    	return flags.get(type);
    }
    
    public boolean hasAttack(AttackType type) {
    	return (owner.getGfx().hasAttackMotion(owner.getInv().canAttackStyle(type)));
    }
    
    public boolean hasBlock() {
    	return (owner.getInv().getBlockItem().canBlock(BlockType.STANDARD));
    }
    
    public boolean canAttack(AttackType type) {
    	/* Take needed style and check it together with the other conditions */
    	return ((isAlive()) &&
    			(!flags.get(StatusType.PARALYZE).active()) &&
    			(hasAttack(type)) && 
    			(owner.getLogic().isBusyLevel(BusyLevel.FREE)));
    }
    
    public boolean canEmpoweredAttack(AttackType type) {
    	return ((canAttack(type)) && (owner.getBook().hasSpell(type)));
    }
    
    public boolean canQuickCast() {
    	return ((isAlive()) &&
    			(!flags.get(StatusType.PARALYZE).active()) &&
    			(owner.getLogic().isBusyLevel(BusyLevel.FREE)) &&
    			(owner.getBook().hasQuickSpell()));
    }
    
    public boolean canBlock() {
    	return ((isAlive()) &&
    			(!flags.get(StatusType.PARALYZE).active()) &&
    			(hasBlock()) &&
    			(owner.getLogic().isBusyLevel(BusyLevel.FREE)));
    }
    
    public boolean canMove() {
    	return isAlive() && !(flags.get(StatusType.PARALYZE).active());
    }
    
    public boolean canDodge() {
    	return ((isAlive()) &&
    			(!flags.get(StatusType.PARALYZE).active()) &&
    			(owner.getLogic().isBusyLevel(BusyLevel.FREE)));
    }
    
    public boolean canRun() {
    	return ((isAlive()) &&
    			(!flags.get(StatusType.PARALYZE).active()) &&
    			(owner.getLogic().isActionEqual(ActionType.WALK)));
    }
    
    public boolean canInteraction() {
    	return ((isAlive()) &&
    			(!flags.get(StatusType.PARALYZE).active()) &&
    			(owner.getLogic().isBusyLevel(BusyLevel.FREE)));
    }
    
    public void takeImpact(Impact impact) {
    	for (Damage x : impact.getDamages())
    		takeDamage(x);
    	for (Effect x : impact.getEffects())
    		takeEffect(x);
    }
    
	public boolean takeDamage(Damage damage) {
		damage.reduce(body.getResistanceValue(damage.getDmgType()), 
				body.getReductionValue(damage.getDmgType()), true);
		/* Decrease HP and add this contribution to the effective damage taken */
    	effectiveDmg += body.getStat(BodyStatType.HP).decreaseValue(damage.getPower());
    	
    	/* Impact won't go any further so return true even if it's not assorbed */
    	return true;
	}
      
    public void update(float delta) {
    	effectManager.update(delta);
    	checkHold();
    	/* Timers */
    	combatTimer.update(delta);
    }
    
    public void takeEffect(Effect effect) {
    	if (!body.isImmune(effect))
    		effectManager.add(effect);
    }
    
	public void apply(Effect effect) {
		effectManager.apply(effect);
	}

	public void remove(Effect effect) {
		effectManager.remove(effect);
	}
    
    public boolean isAlive() {
        return !(body.getStat(BodyStatType.HP).value() <= 0);
    }
    
    public MagicSkill getMagicSkill() {
    	return magicSkill;
    }

    /**
     * Request an usage of an amount of stamina
     * @return True if the stamina was enough and has been used
     */
    public boolean requestStaminaUse(float amount) {
    	return stats.get(CreatureStatType.STAMINA).decreaseValueIfEnough(amount);
    }
    
    /**
     * @return True if there's enough stamina
     */
    public boolean hasStamina(float amount) {
    	return stats.get(CreatureStatType.STAMINA).value() >= amount;
    }
    
    /**
     * Decreases stamina. Use only after having verified that there's a sufficient
     * amount of stamina
     */
    public void useStamina(float amount) {
    	stats.get(CreatureStatType.STAMINA).decreaseValue(amount);
    }
    
    /**
     * Request an usage of an amount of special
     * @return True if the special was enough and has been used
     */
    public boolean requestSpecialUse(float amount) {
    	return stats.get(CreatureStatType.SPECIAL).decreaseValueIfEnough(amount);
    }
    
    /**
     * @return True if there's enough special
     */
    public boolean hasSpecial(float amount) {
    	return stats.get(CreatureStatType.SPECIAL).value() >= amount;
    }
    
    /**
     * Decreases special. Use only after having verified that there's a sufficient
     * amount of special
     */
    public void useSpecial(float amount) {
    	stats.get(CreatureStatType.SPECIAL).decreaseValue(amount);
    }
    
	public void consumeCharge(ChargeTrigger trigger) {
		effectManager.consumeCharge(trigger);
	}
	
	public Timer getCombatTimer() {
		return combatTimer;
	}

	/**
	 * @return Body mass factor
	 */
	public float getMassFactor() {
		return body.getStat(BodyStatType.MASS).factor();
	}
	
	/**
	 * @return Knockback resistance due to constitution
	 */
	public float getKnockbackResist() {
		return stats.get(CreatureStatType.CONSTITUTION).factor();
	}
	
	public boolean sustainEffect(float cost) {
		return requestSpecialUse(cost);
	}

	public void removeToggleEffects() {
		effectManager.removeToggleEffects();
	}
	
    public boolean hasActiveToggleEffects() {
    	return effectManager.hasActiveToggleEffects();
    }
	
	/**
	 * @return True if at least one quick cast spell is still active
	 */
	public boolean isQuickCastActive() {
		return quickCastActive;
	}
	
	public void setQuickCast(boolean value) {
		quickCastActive = value;
	}
	
	/**
	 * Control if the entity took enough damage and went into the hold action
	 */
	private void checkHold() {
		float duration = getHoldTime(effectiveDmg);
		/* If the hold is enough long, hold the entity */
		if (duration >= GlobalVar.HOLD_MIN_DURATION)
			owner.getLogic().tryHold(duration);
		/* Reset the damage taken */
		effectiveDmg = 0;
	}
    
	/**
	 * @return How much time this entity is stunned by taking the specified damage
	 */
	private float getHoldTime(float damage) {
		if (damage <= 0)
			return 0;
		
		return (float) (Math.sqrt(damage) / (20 * stats.get(CreatureStatType.CONSTITUTION).factor()));
	}
	
	/**
	 * @return The move speed after all bonus and malus
	 */
	public float getEffectiveMoveSpeed() {
		return stats.get(CreatureStatType.MOVEMENT).factor() * getStaminaMultiplicator() / owner.getInv().getBurdenFactor();
	}
	
	/**
	 * @return The block speed after all bonus and malus
	 */
	public float getEffectiveBlockSpeed() {
		return stats.get(CreatureStatType.QUICKNESS).factor();
	}
	
	/**
	 * @return The attack speed after all bonus and malus
	 */
	public float getEffectiveAttackSpeed() {
		return stats.get(CreatureStatType.QUICKNESS).factor();
	}
	
	/**
	 * @return A multiplicator depending on the current stamina. For low stamina is less than 1.
	 */
	private float getStaminaMultiplicator() {
		float ratio = getStat(CreatureStatType.STAMINA).ratio();
		if (ratio < GlobalVar.STAMINA_MALUS_START)
			return 1 - (GlobalVar.STAMINA_MALUS_MAX * (1 - ratio / GlobalVar.STAMINA_MALUS_START));
		return 1;
	}
	
	/**
	 * @return Stamina cost for dodge
	 */
	public float getDodgeCost() {
		return GlobalVar.DODGE_COST;
	}
	
	public float getDodgeSpeed() {
		return getEffectiveMoveSpeed();
	}
	
	public float getDodgeDistance() {
		return GlobalVar.DODGE_DISTANCE;
	}
	
	/**
	 * @return The time (in seconds) needed to recover after a dodge
	 */
	public float getDodgeRecover() {
		return GlobalVar.DODGE_RECOVER_DURATION / stats.get(CreatureStatType.QUICKNESS).factor();
	}
	
	/**
	 * @return The progress of the current charging (if there's any) or -1 if no charging is active
	 */
	public float getChargeProgress() {
		return chargeProgress;
	}
	
	public void setChargeProgress(float value) {
		chargeProgress = value;
	}
	
	public List<Effect> getActiveEffects() {
		return effectManager.getEffects();
	}
    
    
	/**
	 * @author Andrea
	 */
	private class StatsEffectManager extends EffectManager {
	
		private StatsEffectManager(Creature owner) {
			super(owner);
		}
		
	    @Override
	    public void applyParalyze() {
	    	flags.get(StatusType.PARALYZE).hold();
	    }
	    
	    @Override
		public void removeParalyze() {
	    	flags.get(StatusType.PARALYZE).release();
	    }
		
	}
    
    
    /**
     * Enumeration of all possible creature statistics
     *
     * @author Andrea
     */
    public enum CreatureStatType {

    	SPECIAL(22),
    	SPECIAL_REGEN(23),
    	
    	QUICKNESS(24),
    	MOVEMENT(30),
    	
    	STAMINA(25),
    	STAMINA_REGEN(26), 
    	
    	STRENGTH(27),
    	MIND(28),
    	CONSTITUTION(29);
    	
		private String name;
		
		CreatureStatType(int nameId) {
			this.name = Game.assets.getDialog(nameId);
		}
		
		@Override
		public String toString() {
			return name;
		}

    }

}
