package action.entity.being.creature.action;

import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;
import action.interfaces.GlobalVar;
import action.utility.enums.Direction;
import action.utility.timer.Timer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Dodge extends Action {
	
	private Vector2 dir;
	private float speed;
	private Timer timer;
	/** True if movement has been completed and now the creature is recovering balance */
	private boolean recovering;
	
    private static final SimpleMotionType MOTION = SimpleMotionType.DODGE;

	public Dodge(Creature owner, Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, Vector2 dir) {
		super(owner, ActionType.DODGE);
		logic.setBusy(BusyLevel.SOFT_BUSY);
		/* Set direction in logic */
		logic.updateDir(Direction.unitVectorToDir(dir));	
		this.dir = dir;
		speed = stats.getDodgeSpeed();
		/* Load the timer with the time needed to travel the full distance with this speed */
		timer = new Timer((int) (stats.getDodgeDistance() / getRealDisplacement()));
	}
	
	@Override
	public void maintenance(Stats stats, float delta) {
		recoverFocus(stats, FOCUS_REGEN_BASEMOD, delta);
	}
	
	@Override
	public void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		timer.update(delta);
		if (!recovering)
			updateDash(gfx, logic, stats, inv, delta);
		else
			updateRecover(logic);
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		if (!recovering)
			updateStdMotions(gfx, logic, inv, MOTION, delta);
	}
	
	private void updateDash(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		if (timer.expired()) {
			recovering = true;
			logic.setBusy(BusyLevel.HARD_BUSY);
			timer.setLength(stats.getDodgeRecover());
			timer.restart();
			return;
		}
		computeDisplacement(logic, stats);
	}
	
	private void updateRecover(Logic logic) {
		if (timer.expired())
			logic.terminateAction(true);
	}
	
	private void computeDisplacement(Logic logic, Stats stats) {
		logic.updateMovV(new Vector2(dir.x * getRealDisplacement(), dir.y * getRealDisplacement()));
	}

	private float getRealDisplacement() {
		return speed * GlobalVar.DODGE_MOVESPEED;
	}

	@Override
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		gfx.drawSimple(batch, SimpleMotionType.WALK, x, y);
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		resetStdMotions(gfx, logic, inv, MOTION);
	}

	@Override
	public boolean isRecovering() {
		return recovering;
	}
}
