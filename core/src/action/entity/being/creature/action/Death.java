package action.entity.being.creature.action;

import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.BusyLevel;
import action.utility.timer.Timer;

/**
 * Manage the death action of a creature
 * 
 * @author Andrea
 */
public class Death extends Action {

	private Timer timer = new Timer(DEATH_TIME);
	
	private static final float DEATH_TIME = 2;
	
	public Death(Creature owner, Logic logic) {
		super(owner, ActionType.DEATH);
		logic.setBusy(BusyLevel.HARD_BUSY);
	}

	@Override
	public void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		timer.update(delta);

		if (timer.expired())
			logic.terminateAction(true);
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		gfx.getTint().a = 1-timer.getProgress();
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		logic.ceaseExistance();
	}

}
