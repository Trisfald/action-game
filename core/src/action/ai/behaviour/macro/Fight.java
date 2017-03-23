package action.ai.behaviour.macro;

import action.ai.Ai;
import action.ai.behaviour.GenericBehaviour;
import action.ai.strategy.BattleStrategy;
import action.ai.strategy.Strategy;
import action.entity.Entity;

/**
 * @author Andrea
 */
public class Fight extends GenericBehaviour {

	private Strategy strategy;
	private Entity target;
	
	public Fight(Ai ai, Entity target) {
		super(ai);
		this.target = target;
		strategy = new BattleStrategy(ai, target);
	}
	
	@Override
	public BehaviourType getType() {
		return BehaviourType.FIGHT;
	}

	@Override
	protected void preAct(float delta) {
		if (!target.isAlive() || target.computeDistance(owner) > ai.getPersonality().getSpotDistance())
			setState(BehaviourState.COMPLETED);
	}
	
	@Override
	protected void doProgress(float delta) {
		strategy.decide();
		strategy.update(delta);
	}
		
}
