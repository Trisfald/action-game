package action.ai.behaviour;

import action.ai.Ai;
import action.ai.behaviour.macro.Fight;
import action.ai.behaviour.macro.Wander;
import action.entity.Entity;
import action.utility.PoolableArray;
import action.utility.timer.Timer;

/**
 * Root behaviour for AI
 * 
 * @author Andrea
 */
public class Master extends GenericBehaviour {
	
	private Behaviour servant = new Wander(ai);
	/** Timer to check around looking for enemies */
	private Timer timer;

	public Master(Ai ai) {
		super(ai);
		timer = new Timer(ai.getPersonality().getReflexTime());
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.MASTER;
	}
	
	@Override
	protected void preAct(float delta) {
		timer.setLength(ai.getPersonality().getReflexTime());
	}
	
	@Override
	protected void doProgress(float delta) {
		/* Update the filler-servant (in this case it can only be a wander) while continuing the progress phase */
		servant.update(delta);
		timer.update(delta);
		if (timer.expired()) {
			checkEnemies();
			timer.restart();
		}
	}
	
	@Override
	protected void doPaused(float delta) {
		servant.update(delta);
		if (servant.isFinished()) {
			servant = new Wander(ai);
			setState(BehaviourState.PROGRESS);
		}
	}
	
	/**
	 * See if there's any enemy at range and fights him
	 */
	private void checkEnemies() {
		/* Check for enemies near the creature */
		PoolableArray<Entity> entities = ai.getOwner().getWorld().getEntities(owner, ai.getPersonality().getSpotDistance());
		for (Entity x : entities) {
			/* The target must be an enemy and must be not covered by walls */
			if (ai.getOwner().isAggressiveTo(x) && world.clearLine(
					ai.getOwner().getCenterX(), ai.getOwner().getCenterY(), x.getCenterX(), x.getCenterY())) {
				servant = new Fight(ai, x);
				setState(BehaviourState.PAUSED);
				return;
			}
		}
		entities.free();
	}

}
