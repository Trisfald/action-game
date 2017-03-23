package action.ai.strategy;

import action.ai.Ai;
import action.ai.personality.Personality.AiStatType;
import action.ai.strategy.Tactic.TacticPosition;
import action.ai.strategy.battle.TacticAttack.TacticMelee;
import action.ai.strategy.battle.TacticAttack.TacticShoot;
import action.ai.strategy.battle.TacticBusy;
import action.ai.strategy.battle.TacticIdle;
import action.ai.strategy.battle.TacticMovement;
import action.entity.Entity;
import action.interfaces.GlobalVar;
import action.utility.timer.Timer;

/**
 * @author Andrea
 */
public class BattleStrategy extends Strategy {

	private Entity enemy;
	/** Time passed from the conclusion of the last attack done */
	private Timer attackTimer = new Timer();
	/** Timer used to check dangers at regular intervals */
	private Timer dangerTimer = new Timer();
	/** Position tactic in use */
	private TacticPosition position;
	/** Pool with available tactics ready to be used */
	private TacticPool pool;
			
	public BattleStrategy(Ai ai, Entity enemy) {
		super(ai);
		this.enemy = enemy;
		attackTimer.setCounter(computeStartingAttackTimer());
		pool = new TacticPool(this);
		/* Put first tactic */
		tactic = pool.idle;
	}
	
	@Override
	protected void initTactic() {
		if (getRunningType() == tactic.getType())
			return;

		/* Type is different, must change tactic */
		tactic.conclude();
		switch(getRunningType()) {
			case IDLE: case FACE:
				tactic = pool.idle;
				break;
			case MOVEMENT:
				tactic = pool.movement;
				break;
			case MELEE_ATTACK:
				tactic = pool.melee;
				break;
			case SHOOT:
				tactic = pool.shoot;
				break;
			default:
				tactic = pool.busy;
				break;
		}
	}

	@Override
	protected void init() {
		super.init();
		dangerTimer.setLength(ai.getPersonality().getReflexTime());
	}
	
	@Override
	public void decide() {
		init();
		initTactic();
		decidePosition();
		tactic.decide(enemy);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		attackTimer.update(delta);
		dangerTimer.update(delta);
	}
	
	public Timer getAttackTimer() {
		return attackTimer;
	}
	
	public Timer getDangerTimer() {
		return dangerTimer;
	}

	public TacticPosition getTacticPosition() {
		return position;
	}

	public void setTacticPosition(TacticPosition position) {
		this.position = position;
	}

	/**
	 * Decides the current position tactic
	 */
	private void decidePosition() {
		/* Check distance from enemy */
		if (ai.getOwner().computeDistance(enemy) < GlobalVar.MELEE_RANGE_DISTANCE)
			position = (ai.getMirror().isWeaponMelee()) ? TacticPosition.MELEE : TacticPosition.RANGED;
		else
			position = (ai.getMirror().isWeaponRanged()) ? TacticPosition.RANGED : TacticPosition.MELEE;
	}
	
	/**
	 * Compute the starting value of the attack timer 
	 */
	private float computeStartingAttackTimer() {
		return ai.getPersonality().getStat(AiStatType.AGGRESSIVITY).factor();
	}
	
	
	/**
	 * A fixed pool with necessary tactics already instantiated
	 * 
	 * @author Andrea
	 */
	private class TacticPool {
		
		Tactic idle;
		Tactic movement;
		Tactic melee;
		Tactic shoot;
		Tactic busy;
		
		private TacticPool(BattleStrategy strategy) {
			idle = new TacticIdle(strategy);
			movement = new TacticMovement(strategy);
			melee =  new TacticMelee(strategy);
			shoot = new TacticShoot(strategy);
			busy = new TacticBusy(strategy);
		}

	}
	
}
