package action.entity.being.creature;

import action.entity.appearance.Appearance;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.BusyLevel;
import action.utility.Body.BodyStatType;

public class CreatureAppearance implements Appearance {

	private Creature owner;
	
	public CreatureAppearance(Creature owner) {
		this.owner = owner;
	}
	
	@Override
	public float ratioHP() {
		return owner.getStats().getStat(BodyStatType.HP).ratio();
	}

	@Override
	public float ratioStamina() {
		return owner.getStatsMemory().getStat(CreatureStatType.STAMINA).ratio();
	}

	@Override
	public ActionType getActionType() {
		return owner.getLogic().getAction().getType();
	}

	@Override
	public BusyLevel getBusy() {
		return owner.getLogic().getBusy();
	}

	@Override
	public boolean isRecovering() {
		return owner.getLogic().getAction().isRecovering();
	}

}
