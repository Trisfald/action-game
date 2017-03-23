package action.ai.strategy.battle;

import action.ai.behaviour.Behaviour.BehaviourType;
import action.ai.behaviour.micro.Face;
import action.ai.behaviour.micro.Movement;
import action.ai.behaviour.micro.Movement.MovementMode;
import action.ai.strategy.BattleStrategy;
import action.entity.Entity;
import action.interfaces.GlobalVar;

/**
 * @author Andrea
 */
public class TacticIdle extends TacticBusy {

	public TacticIdle(BattleStrategy strategy) {
		super(strategy);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.IDLE;
	}
	
	@Override
	public void decide(Entity enemy) {
		if (decideBlock())
			return;
		if (decideAttack(enemy))
			return;
		/* No attack available, so try to keep a good distance to the enemy */
		checkPosition(enemy, getCombatMinDistance(), getCombatMaxDistance(), getGuardDistance());
	}
	
	/**
	 * @param min The min desired distance
	 * @param max The max desired distance
	 */
	private void checkPosition(Entity enemy, float min, float max, float guard) {
		float distance = strategy.getAi().getOwner().computeBorderDistance(enemy);
		
		boolean guarded = false;
		if (GlobalVar.GUARDED_ENABLED)
			guarded = distance <= guard && decideGuarded();

		if (distance < min)
			strategy.setBehaviour(new Movement(strategy.getAi(), enemy, MovementMode.ESCAPE, min, guarded));
		else if (distance > max)
			strategy.setBehaviour(new Movement(strategy.getAi(), enemy, MovementMode.FOLLOW, max, guarded));
		else
			strategy.setBehaviour(new Face(strategy.getAi(), enemy, guarded));
	}
	
}
