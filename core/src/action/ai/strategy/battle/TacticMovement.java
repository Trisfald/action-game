package action.ai.strategy.battle;

import action.ai.behaviour.Behaviour.BehaviourType;
import action.ai.behaviour.micro.Movement;
import action.ai.strategy.BattleStrategy;
import action.entity.Entity;
import action.interfaces.GlobalVar;

/**
 * @author Andrea
 */
public class TacticMovement extends TacticBusy {

	public TacticMovement(BattleStrategy strategy) {
		super(strategy);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.MOVEMENT;
	}
	
	@Override
	public void decide(Entity enemy) {
		if (decideBlock())
			return;
		if (decideAttack(enemy))
			return;
		if (GlobalVar.GUARDED_ENABLED)
			checkGuarded(enemy); 
	}
	
	/**
	 * Control if the movement must be guarded or not
	 */
	private void checkGuarded(Entity enemy) {
		/* Must have guarded enabled and must be near enough */
		((Movement) strategy.getBehaviour()).setGuarded(decideGuarded() && 
				(strategy.getAi().getOwner().computeBorderDistance(enemy) <= getGuardDistance()));
	}

}
