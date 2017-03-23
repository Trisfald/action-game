package action.entity.being.creature.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;
import action.utility.timer.Timer;

/**
 * Class to manage the Hold action
 *
 * @author Andrea
 */
public class Hold extends Action {

	/** Timer for duration of the action */
	private Timer timer;
	
	public Hold(Creature owner, Logic logic, float duration) {
		super(owner, ActionType.HOLD);
		timer = new Timer(duration);
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
		updateStdMotions(gfx, logic, inv, SimpleMotionType.HURT, delta);
	}
	
	@Override
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		gfx.drawSimple(batch, SimpleMotionType.HURT, x, y);
	}
	
	public void extend(float time) {
		timer.extend(time);
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		resetStdMotions(gfx, logic, inv, SimpleMotionType.HURT);
	}

}
