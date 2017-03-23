package action.entity.being.creature.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.animation.AttackMotion;
import action.combat.Damage;
import action.combat.Damage.DamageLoader;
import action.combat.HitLevel;
import action.combat.Impact;
import action.combat.Knockback;
import action.combat.effect.ChargeTrigger;
import action.combat.effect.Effect;
import action.combat.effect.Effect.EffectLoader;
import action.core.Game;
import action.entity.Entity;
import action.entity.Entity.DistanceComparator;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.enums.AttackState;
import action.entity.enums.AttackType;
import action.entity.enums.BusyLevel;
import action.entity.enums.MagicSkill;
import action.hitbox.DangerZone;
import action.hitbox.IndexedHitBox;
import action.hitbox.StaticHitBox;
import action.item.weapon.Attacker;
import action.utility.timer.Timer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Class to manage the Attack action
 *
 * @author Andrea
 */
public class Melee extends Action {
	
	protected Creature owner;
    private List<Entity> targets;
    protected Attacker item;
    protected IndexedHitBox hitBox;
    private float durationSetUp;
    private float durationReady;
    private float durationSetOff;
    protected AttackState attackState;
    /** Timer for duration of states */
    protected Timer timer = new Timer(1000);
    /** The speed of the attack */
    private float speed;
    protected boolean done;
    
    /** The maximum distance to check entities hitted */
    private static final int HIT_CHECK_DISTANCE = 1000;
    /** Special gained by hitting something */
    private static final float SPECIAL_GAIN_MODIFIER = 0f;
	
	public Melee(Creature owner, Graphics gfx, Logic logic, Stats stats, AttackType attackType, List<Entity> targets, Attacker item) {
		super(owner, ActionType.MELEE);
		logic.setBusy(BusyLevel.SOFT_BUSY);
		this.owner = owner;
		this.targets = targets;
		this.item = item;
		this.item.setCurrentAttack(attackType);
		hitBox = new IndexedHitBox(owner.getArmReachH(), owner.getArmReachV(), item.getAttackHitBoxWidth(), item.getAttackHitBoxLength());
		initDurations(gfx.getAttackMotion(item.getAttackStyle()));
		setState(gfx, logic, AttackState.SET_UP);
        checkSpeed(gfx, logic, stats);
        setDangerZone(logic);
	}
	
	private void setDangerZone(Logic logic) {
		/* Get hitbox shape and stretch it */
		IndexedHitBox hitBox = new IndexedHitBox(owner.getArmReachH(), owner.getArmReachV(), item.getAttackHitBoxWidth(), 
				(int) (item.getAttackHitBoxLength() + item.getDashLength()));
        logic.setDangerZone(new DangerZone(new StaticHitBox(hitBox.getShape(owner.getCenterX(), owner.getCenterY(), 
        		logic.getDir()), logic.getDir().getVector(), item.getAttackHitBoxLength() + item.getDashLength()), item.getBlockNeeded(),
        		owner.getFaction()));
	}

	private void initDurations(AttackMotion attackMotion) {
	    durationSetUp = attackMotion.getDuration(AttackState.SET_UP);
	    durationReady = attackMotion.getDuration(AttackState.READY);
	    durationSetOff = attackMotion.getDuration(AttackState.SET_OFF);
	}
	
	@Override
	public void maintenance(Stats stats, float delta) {
		recoverFocus(stats, FOCUS_REGEN_BASEMOD, delta);
	}
	
	@Override
	public final void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		checkSpeed(gfx, logic, stats);
		timer.update(delta * speed);
		
		/* Update behaviour changes depending on the state we are in */
		switch (attackState) {
			case CHARGE:
				updateStateCharge(gfx, logic, stats);
				break;
			case SET_UP:
				updateStateSetUp(gfx, logic, stats);
				break;
			case READY:
				updateStateReady(gfx, logic, stats);
				break;
			case SET_OFF:
				updateStateSetOff(gfx, logic, stats);
				break;
		}		
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		gfx.updateAttack(item.getAttackStyle(), delta * speed);
	}
	
	protected void updateStateCharge(Graphics gfx, Logic logic, Stats stats) {
		
	}
	
	private void updateStateSetUp(Graphics gfx, Logic logic, Stats stats) {
		/* Update the mov vector in logic to perform the dash */
		logic.updateMovV(logic.getDir().getVector().scl(getDashSpeed()));
		
		if (timer.expired(getRealDuration())) {
			setState(gfx, logic, AttackState.READY); 
			gfx.getSound(item.getAttackStyle()).play(Game.sounds.getSfxVolume() * 
					owner.getWorld().getSoundIntensity(owner.getCenterX(), owner.getCenterY()));
		}
	}
	
	private void updateStateReady(Graphics gfx, Logic logic, Stats stats) {
		/* Give the hit at half the duration of the state */
		if (!done && timer.expired(getRealDuration()/2))
			executeHit(logic, stats);
		if (timer.expired(getRealDuration())) {
			setState(gfx, logic, AttackState.SET_OFF);
		}
	}
	
	private void updateStateSetOff(Graphics gfx, Logic logic, Stats stats) {
		if (timer.expired(getRealDuration()))
			logic.terminateAction(true);
	}
	
	private float getDashSpeed() {
		return item.getDashLength() / getRealDuration(AttackState.SET_UP);
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		gfx.resetAttack(item.getAttackStyle());
		item.resetAttack(logic.getDir());
		logic.setDangerZone(null);
	}
	
	@Override
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		gfx.drawAttack(batch, item.getAttackStyle(), x, y);
	}
	
	/**
	 * Check if the speed is different and update it
	 */
	protected void checkSpeed(Graphics gfx, Logic logic, Stats stats) {
		this.speed = computeRealSpeed(stats);
	}
	
	/**
	 * For debugging
	 */
	public IndexedHitBox getHitBox(Logic logic) {
		hitBox.getShape(owner.getCenterX(), owner.getCenterY(), logic.getDir());
		return hitBox;
	}
    
	/**
	 * @return The effective speed of the attack
	 */
	private float computeRealSpeed(Stats stats) {
		return stats.getEffectiveAttackSpeed() * item.getAttackSpeed();
	}
    
    /**
     * @return The effective duration of the current state
     */
    private float getRealDuration() {
    	return getRealDuration(attackState);
    }
    
    /**
     * @return The effective duration of a given state
     */
    private float getRealDuration(AttackState state) {
    	switch (state) {
    		case SET_UP:
    			return (durationSetUp / speed);
    		case READY:
    			return (durationReady / speed);
    		case SET_OFF:
    			return (durationSetOff / speed);
    		default:
    			return 0;
    	}
    }
    
	protected void setState(Graphics gfx, Logic logic, AttackState state) {
		timer.restart();
		attackState = state;
		gfx.setAttackState(item.getAttackStyle(), state);
	}

    protected void executeHit(Logic logic, Stats stats) {
        int i;
        Entity target;
        List<Entity> hitted = new ArrayList<Entity>();
        /** To see if something has been hitted at least one time */
        boolean hit = false;
        
        for (i = 0; i < targets.size(); i++) {
            target = targets.get(i);
            if ((!target.equals(owner)) && 
            		(owner.isAggressiveTo(target)) &&
            		(target.isHittable(HitLevel.WEAPON)) && 
            		(target.computeDistance(owner) <= HIT_CHECK_DISTANCE)) {
                if (hitBox.collide(owner.getCenterX(), owner.getCenterY(), logic.getDir(), target.getShape())) {
                	/* Add target hit to list */
                	hitted.add(target);
                	hit = true;
                }
            }
        }

        /* Give the hit to all hitted targets and gain focus*/
        if (hit) {
            giveHit(logic, stats, hitted);
            if (stats.getMagicSkill() == MagicSkill.SPECIAL)
            	focusGain(stats);
        }

        /* Consume the charges for attacking */
    	item.consumeCharge(ChargeTrigger.DO_ATTACK);
    	stats.consumeCharge(ChargeTrigger.DO_ATTACK);
        done = true;
        
        /* Nullify danger zone */
        logic.setDangerZone(null);
    }
    
    /*
     * Give an impact from the weapon to all hitted entities
     */
    private void giveHit(Logic logic, Stats stats, List<Entity> hitted) {
    	/* Sort to get the nearest target */
    	Collections.sort(hitted, new DistanceComparator(owner));
    	
    	/* Compute damages */
    	List<Damage> damages = new ArrayList<Damage>();
    	float crush = computeHitDamages(damages, stats);
    	
    	for (int i = 0; i < item.getAttackTargets() && i < hitted.size(); i++)
	        hitted.get(i).takeImpact(new Impact(damages, computeHitEffects(), computeKnockback(logic),
	        		hitBox.getStatic(), item.getBlockNeeded(), computeCrushPower(crush), true));
    }
    
    public float computeCrushPower(float base) {
    	return item.getCrushPower() * base;
    }
    
    /**
     * Load damage in the given list and return the block crush caused by those damages
     */
    public float computeHitDamages(List<Damage> damages, Stats stats) {
    	float crush = 0;
    	for (DamageLoader x : item.getDamages()) {	
    		Damage dmg = Damage.computeDamage(stats, x);
    		damages.add(dmg);
    		crush += dmg.getPower();
    	}
    	return crush;
    }
    
    public List<Effect> computeHitEffects() {
    	List<Effect> effects = new ArrayList<Effect>();
    	
        for (EffectLoader x : item.getAttackEffects()) {
        	if (Math.random() < x.probability)
        		effects.add(x.load());
        }
        return effects;
    }
    
    public Knockback computeKnockback(Logic logic) {
    	if (item.getKnockback() == null)
    		return null;
    	return new Knockback(item.getKnockback(), logic.getDir().getVector());	
    }
    
    public boolean isBlockable() {
        return (attackState == AttackState.SET_UP);
    }
    
    /**
     * Gives focus and restart the timer
     */
    protected void focusGain(Stats stats) {
    	stats.getStat(CreatureStatType.SPECIAL).increaseValue(
    			stats.getStat(CreatureStatType.SPECIAL).value() * SPECIAL_GAIN_MODIFIER);	
    	stats.getCombatTimer().restart();
    }
    
    @Override
	public boolean isRecovering() {
    	return done;
    }

}
