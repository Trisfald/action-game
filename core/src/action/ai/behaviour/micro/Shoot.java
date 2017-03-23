package action.ai.behaviour.micro;

import action.ai.Ai;
import action.ai.behaviour.Behaviour;
import action.ai.behaviour.GenericBehaviour;
import action.ai.behaviour.micro.Movement.MovementMode;
import action.entity.Entity;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.AttackType;
import action.utility.enums.Direction;
import action.utility.geom.Line;
import action.utility.geom.Shape;
import action.world.Collision;

import com.badlogic.gdx.math.Vector2;

/**
 * Behaviour for a ranged attack
 * 
 * @author Andrea
 */
public class Shoot extends GenericBehaviour {

	private Entity target;
	private AttackType attackType;
	private Behaviour movement;
	/** True if the AI has decided to shoot */
	private boolean shooting;
	
	private static final float ATTACK_RANGE_MOD = 0.9f;
	
	public Shoot(Ai ai, Entity target, AttackType attackType) {
		super(ai);
		this.target = target;
		this.attackType = attackType;
		movement = new Movement(ai, target, MovementMode.FOLLOW, ai.getMirror().getAttackRange(attackType) * ATTACK_RANGE_MOD, false);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.SHOOT;
	}
	
	@Override
	protected void doProgress(float delta) {
		if (shooting)
			shoot(delta);
		else
			prepare(delta);
	}
	
	@Override
	protected void doPaused(float delta) {
		/* Wait for the completion of the animation and then change state */
		if (ai.getMirror().currentAction() != ActionType.SHOOT)
			setState(BehaviourState.COMPLETED);
	}
	
	private void shoot(float delta) {
		owner.tryAttack(attackType);

		/* If the attack doesn't start, it's a failure */
		if (ai.getMirror().currentAction() != ActionType.SHOOT) 
			setState(BehaviourState.FAILED);
		
		/* Adjiust creature's direction */
		owner.tryMovement(Direction.getDirTowards(owner.getPos(), target.getPos()).getVector());
		
		/* When charging is completed and the aim is correct, release the action and put in pause */
		if (ai.getMirror().isReadyToShoot() && aim())
			setState(BehaviourState.PAUSED);
	}
	
	private void prepare(float delta) {
		/* Update the movement */
		movement.update(delta);
		
		/* Check if the enemy is now inside the range */
		if (movement.getState() == BehaviourState.COMPLETED)
			shooting = true;
		/* Cannot reach */
		else if (movement.getState() == BehaviourState.FAILED)
			setState(BehaviourState.FAILED);
	}
	
	/**
	 * Check the aim
	 */
	private boolean aim() {
		/* Create the imaginary line of fire */
		Vector2 displacement = owner.getDir().getVector().scl(ai.getMirror().getAttackRange(attackType));
		Shape line = new Line(owner.getCenterX(), owner.getCenterY(), displacement.x, displacement.y ,false);		
		/* See if the line collides with the enemy */
		return Collision.collide(line, target.getShape());
	}
	
}
