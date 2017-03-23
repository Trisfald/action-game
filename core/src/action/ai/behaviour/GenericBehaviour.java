package action.ai.behaviour;

import action.ai.Ai;
import action.entity.being.creature.Creature;
import action.world.World;

/**
 * @author Andrea
 */
public abstract class GenericBehaviour implements Behaviour {

	protected BehaviourState state;
	protected Ai ai;
	protected World world;
	protected Creature owner;
	
	public GenericBehaviour(Ai ai) {
		this.ai = ai;
		this.owner = ai.getOwner();
		this.world = owner.getWorld();
		setState(BehaviourState.PROGRESS);
	}

	@Override
	public BehaviourState getState() {
		return state;
	}

	@Override
	public void setState(BehaviourState state) {
		this.state = state;
	}
	
	@Override
	public boolean isFinished() {
		return state == BehaviourState.COMPLETED || state == BehaviourState.FAILED;
	}
	
	@Override
	public final void update(float delta) {
		preAct(delta);
		act(delta);
	}
	
	/**
	 * Do operations before acting
	 */
	protected void preAct(float delta) {
		
	}

	private final void act(float delta) {
		switch (state) {
			case PROGRESS:
				doProgress(delta);
				break;
			case PAUSED:
				doPaused(delta);
				break;
			case FAILED:
				doFailed(delta);
				break;
			case COMPLETED:
				doCompleted(delta);
				break;
		}
	}
	
	protected void doProgress(float delta) {
		
	}
	
	protected void doPaused(float delta) {
		
	}
	
	protected void doFailed(float delta) {
		
	}
	
	protected void doCompleted(float delta) {
		
	}
	
	@Override
	public void shiftPosition(float dx, float dy) {
		
	}
	
}
