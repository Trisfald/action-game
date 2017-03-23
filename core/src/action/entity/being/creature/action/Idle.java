package action.entity.being.creature.action;

import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;

/**
 * Class to manage the Idle action
 *
 * @author Andrea
 */
public class Idle extends Action {

	public Idle(Creature owner, Logic logic) {
		super(owner, ActionType.IDLE);
		logic.setBusy(BusyLevel.FREE);
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		updateStdMotions(gfx, logic, inv, SimpleMotionType.IDLE, delta);
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		resetStdMotions(gfx, logic, inv, SimpleMotionType.IDLE);
	}
	
}
