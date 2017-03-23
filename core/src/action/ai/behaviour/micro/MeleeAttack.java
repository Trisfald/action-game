package action.ai.behaviour.micro;

import action.ai.Ai;
import action.ai.behaviour.Behaviour;
import action.ai.behaviour.GenericBehaviour;
import action.ai.behaviour.micro.Movement.MovementMode;
import action.entity.Entity;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.AttackType;
import action.utility.enums.Direction;
import action.utility.timer.Timer;

/**
 * Behaviour for a melee attack
 * 
 * @author Andrea
 */
public class MeleeAttack extends GenericBehaviour {
	
	private Entity target;
	private AttackType attackType;
	private Behaviour movement;
	private Timer timer = new Timer(ATTACK_MAX_TRYTIME);
	
	private static final float ATTACK_RANGE_MOD = 1f;
	private static final float ATTACK_MAX_TRYTIME = 1;

	public MeleeAttack(Ai ai, Entity target, AttackType attackType) {
		super(ai);
		this.target = target;
		this.attackType = attackType;
		movement = new Movement(ai, target, MovementMode.FOLLOW, ai.getMirror().getAttackRange(attackType) * ATTACK_RANGE_MOD, false);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.MELEE_ATTACK;
	}
	
	@Override
	protected void doProgress(float delta) {
		/* Update the movement */
		movement.update(delta);

		/* If it's at range try the attack */
		if (movement.getState() == BehaviourState.COMPLETED) {
			executeAttack(delta);
		}
		/* Cannot reach */
		else if (movement.getState() == BehaviourState.FAILED) {
			setState(BehaviourState.FAILED);
		}
		/* Keep timer to 0 */
		else
			timer.restart();
	}
	
	@Override
	protected void doPaused(float delta) {
		/* Wait for the completion of the animation and then change state */
		if (ai.getMirror().currentAction() != ActionType.MELEE)
			setState(BehaviourState.COMPLETED);
	}
	
	private void executeAttack(float delta) {
		/* Fix the orientation of the creature and then attack */
		owner.setDir(Direction.getDirTowards(owner.getPos(), target.getPos()));
		owner.tryAttack(attackType);
		
		/* Put on pause only if the attack starts */
		if (ai.getMirror().currentAction() == ActionType.MELEE) 
			setState(BehaviourState.PAUSED);
		
		/* Check if it's waiting to much time for the attack to start and if so put on failed */
		timer.update(delta);
		if (timer.expired())
			setState(BehaviourState.FAILED);
	}
	

}
