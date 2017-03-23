package action.entity.being.creature.action;

import action.combat.Impact;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;
import action.ui.GameUI;

/**
 * Class to manage the Talk action
 *
 * @author Andrea
 */
public class Talk extends Action {
	
	private GameUI ui;
	
	public Talk(Creature owner, Logic logic, GameUI ui) {
		super(owner, ActionType.TALK);
		logic.setBusy(BusyLevel.SOFT_BUSY);
		this.ui = ui;
	}
	
	@Override
	public void takeImpact(Logic logic, Stats stats, Impact impact) {
		logic.terminateAction(true);
		ui.closeGameChat();
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
