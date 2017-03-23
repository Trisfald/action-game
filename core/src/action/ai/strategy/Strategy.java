package action.ai.strategy;

import action.ai.Ai;
import action.ai.behaviour.Behaviour;
import action.ai.behaviour.Behaviour.BehaviourType;

/**
 * An abstract base for all strategies
 * 
 * @author Andrea
 */
public abstract class Strategy {

	protected Ai ai;
	/** The behaviour that the strategy can decide */
	private Behaviour behaviour;
	protected Tactic tactic;
	
	public Strategy(Ai ai) {
		this.ai = ai;
	}
	
	protected void init() {
		if (behaviour == null)
			return;
		if (behaviour.isFinished())
			behaviour = null;
	}
	
	public abstract void decide();
	
	protected abstract void initTactic();
	
	protected BehaviourType getRunningType() {
		if (behaviour == null)
			return BehaviourType.IDLE;
		return behaviour.getType();
	}

	public Behaviour getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
	}
	
	public Ai getAi() {
		return ai;
	}

	public void update(float delta) {
		if (behaviour == null)
			return;
		behaviour.update(delta);
	}
	
}
