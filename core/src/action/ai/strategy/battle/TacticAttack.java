package action.ai.strategy.battle;

import action.ai.behaviour.Behaviour.BehaviourState;
import action.ai.behaviour.Behaviour.BehaviourType;
import action.ai.strategy.BattleStrategy;
import action.entity.Entity;

/**
 * @author Andrea
 */
public abstract class TacticAttack extends TacticBusy {

	public TacticAttack(BattleStrategy strategy) {
		super(strategy);
	}
	
	@Override
	public void conclude() {
		strategy.getAttackTimer().restart();
	}
	
	@Override
	public void decide(Entity enemy) {
		/* If we are not in the 'move' phase, there's nothing to do */
		if (strategy.getBehaviour().getState() != BehaviourState.PROGRESS)
			return;
		if (decideBlock())
			return;
	}
	
	
	public static class TacticMelee extends TacticAttack {
		
		public TacticMelee(BattleStrategy strategy) {
			super(strategy);
		}

		@Override
		public BehaviourType getType() {
			return BehaviourType.MELEE_ATTACK;
		}
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class TacticShoot extends TacticAttack {
		
		public TacticShoot(BattleStrategy strategy) {
			super(strategy);
		}

		@Override
		public BehaviourType getType() {
			return BehaviourType.SHOOT;
		}
		
	}

}
