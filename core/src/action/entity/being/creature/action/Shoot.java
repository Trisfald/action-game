package action.entity.being.creature.action;

import action.animation.AttackMotion;
import action.core.Game;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.AttackState;
import action.entity.enums.AttackType;
import action.entity.enums.BusyLevel;
import action.hitbox.IndexedHitBox;
import action.item.weapon.Attacker;
import action.utility.enums.Direction;
import action.utility.timer.Timer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Shoot extends Action {
	
	private Attacker item;
	private AttackState attackState;
	private Timer timer = new Timer(1000);
	private float speed;
    protected IndexedHitBox hitBox;
	private float durationSetUp;
	private float durationSetOff;
	private float durationReady;
	private boolean active = true;
	private boolean chargeSuccess = false;

	public Shoot(Creature owner, Graphics gfx, Logic logic, Stats stats, AttackType attackType, Attacker item) {
		super(owner, ActionType.SHOOT);
		logic.setBusy(BusyLevel.SOFT_BUSY);
		this.owner = owner;
		this.item = item;
		this.item.setCurrentAttack(attackType);
		hitBox = new IndexedHitBox(owner.getArmReachH(), owner.getArmReachV(), item.getAttackHitBoxWidth(), item.getAttackHitBoxLength());
		initDurations(gfx.getAttackMotion(item.getAttackStyle()));
		checkSpeed(gfx, logic, stats);
		setState(gfx, logic, AttackState.SET_UP);
	}

	@Override
	public void initialize() {
		active = false;
	}

	@Override
	public void maintenance(Stats stats, float delta) {
		recoverFocus(stats, FOCUS_REGEN_BASEMOD, delta);
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		gfx.resetAttack(item.getAttackStyle());
		item.resetAttack(logic.getDir());
	}
	
	@Override
	public final void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		timer.update(delta);
		
		checkSpeed(gfx, logic, stats);
		
		/* Update behaviour changes depending on the state we are in */
		switch (attackState) {
			case SET_UP:
				if (timer.expired(getRealDuration(attackState)))
					setState(gfx, logic, AttackState.READY);
				break;
			case READY:
				/* Set the global charge meter for the UI */
				stats.setChargeProgress(timer.getCounter() / durationReady);			
				if (timer.expired(getRealDuration(attackState)))
					chargeSuccess = true;
				if (!active) {
					if (chargeSuccess)
						fire(gfx, logic, stats);
					setState(gfx, logic, AttackState.SET_OFF);
				}
				break;
			case SET_OFF:
				if (timer.expired(getRealDuration(attackState)))
					logic.terminateAction(true);
				break;
			default:
				break;
		}	
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		gfx.updateAttack(item.getAttackStyle(), delta * speed);
	}
	
	@Override
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		gfx.drawAttack(batch, item.getAttackStyle(), x, y);
	}
	
	@Override
	public void updateRoute(Logic logic, Graphics gfx, Vector2 dir, float speed) {
		if (attackState != AttackState.READY)
			return;
		
		logic.setDir(Direction.vectorToDir(dir));
		gfx.setAttackState(item.getAttackStyle(), attackState);
	}
	
	private void initDurations(AttackMotion attackMotion) {
	    durationSetUp = attackMotion.getDuration(AttackState.SET_UP);
	    durationReady = item.getChargeTime();
	    durationSetOff = attackMotion.getDuration(AttackState.SET_OFF);
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
	
	/**
	 * Check if the speed is the same. If it's not, updates everything
	 */
	protected void checkSpeed(Graphics gfx, Logic logic, Stats stats) {
		float newSpeed = computeRealSpeed(stats);
		
		if (newSpeed != speed)
			setSpeed(gfx, logic, newSpeed);
	}
	
	/**
	 * @return The effective speed of the attack
	 */
	private float computeRealSpeed(Stats stats) {
		return stats.getEffectiveAttackSpeed() * item.getAttackSpeed();
	}
	
    private void setSpeed(Graphics gfx, Logic logic, float speed) {
    	/* Adjust timer */
    	timer.setCounter((int) (timer.getCounter() * this.speed / speed));	
    	this.speed = speed;
    }
    
	public void signalActivity() {
		active = true;
	}
	
	private void fire(Graphics gfx, Logic logic, Stats stats) {
		hitBox.getShape(owner.getCenterX(), owner.getCenterY(), logic.getDir());
		owner.getWorld().spawnSpawner(item.getSpawner().spawn(hitBox.getTipPosition().x, hitBox.getTipPosition().y, 
				logic.getDir().getVector(), owner.getWorld(), owner, stats));
		gfx.getSound(item.getAttackStyle()).play(Game.sounds.getSfxVolume() * 
				owner.getWorld().getSoundIntensity(owner.getCenterX(), owner.getCenterY()));
	}
	
	public boolean isCharged() {
		return chargeSuccess;
	}

}
