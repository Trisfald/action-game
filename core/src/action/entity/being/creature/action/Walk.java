package action.entity.being.creature.action;

import action.core.Game;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;
import action.interfaces.GlobalVar;
import action.utility.enums.Direction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Class to manage the movement action
 *
 * @author Andrea
 */
public class Walk extends Action {
	
	/** Tells if a movement attempt has been issued since the last update */
	private boolean activeMov = true;
	/** Tells if a running attempt has been issued since the last update */
	private boolean activeRun = false;
	private boolean run = false;
	private float runTimer = 0;
	private Vector2 movDir = new Vector2(0, 0);
	private Vector2 runDir = new Vector2(0, 0);
	/** Speed of the creature */
	private float speed;
	/** Relative speed at which the creature chooses to move */
	private float relativeSpeed;
	/** ID of the sound instance used */
	private long soundID;
	
	/** Max run speed multiplier */
    private static final float RUN_MAX_SPEED = 1.7f;
    /** Time to reach max run speed */
    private static final float RUN_ACCEL_TIME = 1;
    
    private static final float STAMINA_RECOVER_MOD = STAMINA_REGEN_BASEMOD * 2/3;
    
    private static final SimpleMotionType MOTION = SimpleMotionType.WALK;
	
	public Walk(Creature owner, Graphics gfx, Logic logic, Stats stats) {
		super(owner, ActionType.WALK);
		logic.setBusy(BusyLevel.FREE);
		checkSpeed(stats);
		soundID = gfx.getSound(MOTION).loop(Game.sounds.getSfxVolume() * 
				owner.getWorld().getSoundIntensity(owner.getCenterX(), owner.getCenterY()));
	}
	
	@Override
	public void initialize() {
		activeMov = false;
		activeRun = false;
	}
	
	@Override
	public void maintenance(Stats stats, float delta) {
		if (!run)
			recoverStamina(stats, STAMINA_RECOVER_MOD, delta);
		else
			if (!stats.requestStaminaUse(20*delta/1000))
				activeRun = false;
		recoverFocus(stats, FOCUS_REGEN_BASEMOD, delta);
	}
	
	@Override
	public void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		if (!internalStatus(logic, delta))
			return;
		checkSpeed(stats);
		computeDisplacement(logic, stats);
		logic.updateMovV(movDir);
	}
	
	@Override
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		gfx.drawSimple(batch, MOTION, x, y);
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		resetStdMotions(gfx, logic, inv, MOTION);
		gfx.getSound(MOTION).stop(soundID);
	}
	
	private void checkSpeed(Stats stats) {
		speed = getRealMoveSpeed(stats);
	}
	
	/**
	 * @return False if the movement has stopped
	 */
	private boolean internalStatus(Logic logic, float delta) {
		if (!activeMov) {
			logic.terminateAction(true);
			return false;
		}	
		if (activeRun) {
			run = true;
			runTimer += delta;
		}
		else {
			run = false;
			runTimer = 0;
		}
		return true;
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		gfx.updateSimple(MOTION, delta*speed*relativeSpeed);
	}
	
	private void signalMovement() {
		activeMov = true;
	}
	
	private void computeDisplacement(Logic logic, Stats stats) {
		if (run)
			movDir.set(runDir);
		logic.updateDir(Direction.unitVectorToDir(movDir));	
		movDir.set(movDir.x * getRealDisplacement(), movDir.y * getRealDisplacement()); 
	}
	
	@Override
	public void updateRoute(Logic logic, Graphics gfx, Vector2 dir, float speed) {
		movDir = dir;
    	signalMovement();
    	relativeSpeed = speed;
	}
	
	public void signalRun() {
		activeRun = true;
		if (!run || (runDir.dst(movDir) > 0.6 && runDir.dst(movDir) < 0.8)) {
			runDir.set(movDir);
			runTimer = 0;
		}
	}
	
	private float getRealMoveSpeed(Stats stats) {
		if (!run)
			return stats.getEffectiveMoveSpeed() * relativeSpeed;
		else
			return stats.getEffectiveMoveSpeed() * getRunMultiplier() * relativeSpeed;
	}
	
	private float getRunMultiplier() {
		/** Multiplier increases linearly with time up to a given limit */
		float multiplier = 1 + (RUN_MAX_SPEED-1)/RUN_ACCEL_TIME * runTimer;
		if (runTimer >= RUN_ACCEL_TIME)
			return RUN_MAX_SPEED;
		else
			return multiplier;
	}
	
	private float getRealDisplacement() {
		return speed * GlobalVar.BASE_MOVESPEED;
	}

	public long getSoundID() {
		return soundID;
	}
	
}
