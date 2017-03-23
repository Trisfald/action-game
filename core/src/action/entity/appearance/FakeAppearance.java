package action.entity.appearance;

import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.BusyLevel;


/**
 * @author Andrea
 */
public class FakeAppearance implements Appearance {

	@Override
	public float ratioHP() {
		return 1;
	}

	@Override
	public float ratioStamina() {
		return 1;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.IDLE;
	}

	@Override
	public BusyLevel getBusy() {
		return BusyLevel.FREE;
	}

	@Override
	public boolean isRecovering() {
		return false;
	}

}
