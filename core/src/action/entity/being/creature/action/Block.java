package action.entity.being.creature.action;

import action.combat.BlockLevel;
import action.combat.Impact;
import action.core.Game;
import action.entity.being.creature.Creature;
import action.entity.being.creature.CreatureInventory;
import action.entity.being.creature.Graphics;
import action.entity.being.creature.Logic;
import action.entity.being.creature.Stats;
import action.entity.being.creature.Stats.CreatureStatType;
import action.entity.enums.BlockState;
import action.entity.enums.BlockType;
import action.entity.enums.BusyLevel;
import action.entity.enums.SimpleMotionType;
import action.hitbox.IndexedHitBox;
import action.interfaces.GlobalVar;
import action.item.shield.Blocker;
import action.utility.enums.Direction;
import action.utility.geom.Shape;
import action.utility.timer.Timer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Class to manage the Block action
 *
 * @author Andrea
 */
public class Block extends Action {
	
    private Blocker item;
    private IndexedHitBox hitBox;
    private Timer timer;
    private BlockState blockState = BlockState.SET_UP;
    private Vector2 movDir = new Vector2(0, 0);
    private float moveSpeed;
    private boolean active = true;
    private boolean activeMov;
    /** Active mov of the past update cycle */
    private boolean activeMovPast;
    private BlockLevel blockLevel;
	/** ID of the sound instance used */
	private long soundID;
    
    /** Multiplier for the basic movespeed in case of block */
    private static final float MOVESPEED_MOD = 0.5f;
    /** Stamina recovering rate while blocking standing still */
    private static final float STAMINA_RECOVER_BLOCK = STAMINA_REGEN_BASEMOD * 1/4;
    /** Stamina recovering rate while blocking and walking */
    private static final float STAMINA_RECOVER_MOVE = STAMINA_RECOVER_BLOCK * 2/3;
    
    private static final float SET_UP_TIME = 0.1f;
    private static final float SET_OFF_TIME = 0.1f;
    
    private static final SimpleMotionType MOTION_WALK = SimpleMotionType.WALK;
    private static final SimpleMotionType MOTION_IDLE = SimpleMotionType.IDLE;
    private static final SimpleMotionType MOTION_BLOCK_WALK = SimpleMotionType.BLOCK_WALK;
    private static final SimpleMotionType MOTION_BLOCK_IDLE = SimpleMotionType.BLOCK_IDLE;
	
	public Block(Creature owner, Logic logic, BlockType blockType, Blocker item, long soundID) {
		super(owner, ActionType.BLOCK);
		logic.setBusy(BusyLevel.SOFT_BUSY);
		this.owner = owner;
		this.item = item;
		this.item.setCurrentBlock(blockType);
		this.soundID = soundID;
		timer = new Timer(SET_UP_TIME);
		blockLevel = item.getBlockCapability();
		hitBox = new IndexedHitBox(0, 0, item.getBlockHitBoxWidth(), item.getBlockHitBoxLength());
		/* If there is a valid sound ID it means that previously the movement was active */
		activeMovPast = soundID != -1;
	}
	
	@Override
	public void initialize() {
		active = false;
		activeMov = false;
	}
	
	@Override
	public void maintenance(Stats stats, float delta) {
		recoverFocus(stats, FOCUS_REGEN_BASEMOD, delta);
		if (activeMov)
			recoverStamina(stats, STAMINA_RECOVER_MOVE, delta);
		else
			recoverStamina(stats, STAMINA_RECOVER_BLOCK, delta);
	}
	
	@Override
	public void updateLogic(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
        if (activeMov) {
        	checkMoveSpeed(stats);
			computeDisplacement(logic, stats);
			logic.updateMovV(movDir);
        }
        /* Check sound activation/deactivation */
        if (activeMovPast && !activeMov)
            gfx.getSound(MOTION_WALK).stop(soundID);
        else if (!activeMovPast && activeMov)
    		soundID = gfx.getSound(MOTION_WALK).loop(Game.sounds.getSfxVolume() * 
    				owner.getWorld().getSoundIntensity(owner.getCenterX(), owner.getCenterY()));
        activeMovPast = activeMov;
        
		switch (blockState) {
			case SET_UP:
				timer.update(delta * computeRealBlockSpeed(stats));
				if (timer.expired())
					setState(gfx, logic, BlockState.READY);
				break;
			case READY:
				if (!active) {
					timer.setLength(SET_OFF_TIME);
					setState(gfx, logic, BlockState.SET_OFF);
				}
				break;
			case SET_OFF:
				timer.update(delta * computeRealBlockSpeed(stats));
				if (timer.expired())
					logic.terminateAction(true);
				break;
		}
	}
	
	@Override
	protected void updateGfx(Graphics gfx, Logic logic, Stats stats, CreatureInventory inv, float delta) {
		if (activeMov)
			switch (blockState) {
				case SET_UP: 
					updateBodyMotion(gfx, MOTION_WALK, delta * moveSpeed);
					break;
				case READY: case SET_OFF:
					updateBodyMotion(gfx, MOTION_BLOCK_WALK, delta * moveSpeed * MOVESPEED_MOD);
					break;
			}
	}

	@Override
	public void drawBody(SpriteBatch batch, Graphics gfx, float x, float y) {
		switch (blockState) {
			case SET_UP:
				if (activeMov)
					gfx.drawSimple(batch, MOTION_WALK, x, y);
				else
					gfx.drawSimple(batch, MOTION_IDLE, x, y);
				break;
			case READY: case SET_OFF:
				if (activeMov)
					gfx.drawSimple(batch, MOTION_BLOCK_WALK, x, y);
				else
					gfx.drawSimple(batch, MOTION_BLOCK_IDLE, x, y);
				break;
		}
	}
	
	@Override
	public void conclude(Graphics gfx, Logic logic, CreatureInventory inv) {
		resetStdMotions(gfx, logic, inv, MOTION_WALK);
		resetStdMotions(gfx, logic, inv, MOTION_BLOCK_WALK);
		gfx.getSound(MOTION_WALK).stop(soundID);
	}
	
	private void setState(Graphics gfx, Logic logic, BlockState state) {
		timer.restart();
		blockState = state;
	}
	
    public Shape getBlockingShape(Direction dir) {
        if (isBlocking())
            return hitBox.getShape(owner.getCenterX(), owner.getCenterY(), dir);
        return null;
    }
    
    public boolean isBlocking() {
    	return (blockState == BlockState.READY);
    }
    
	public void signalActivity() {
		active = true;
	}
    
	@Override
	public void updateRoute(Logic logic, Graphics gfx, Vector2 dir, float speed) {
		movDir = dir;
    	signalMovement();
	}
	
	private void signalMovement() {
		activeMov = true;
	}
    
    @Override
	public void takeImpact(Logic logic, Stats stats, Impact impact) {
    	/* If the block level is not enough, do nothing */
    	if (blockLevel.value() < impact.getBlockLevel().value())
    		return;
    	
    	/* Absorb if the impact has not a defined hb */
    	if (impact.getHitBox() == null)
    		absorb(impact, stats);
    	else
    		/* If it has an hb, must be check if the hit is parried */
			if (IndexedHitBox.shapeCollide(impact.getHitBox().getShape(), getBlockingShape(logic.getDir())))
				if (checkShieldHit(logic, impact))
					absorb(impact, stats);	
    }
    
    /**
     * @return True if the impact hit the shield before hitting the body
     */
    private boolean checkShieldHit(Logic logic, Impact impact) {  
        Vector2 centerHB = impact.getHitBox().getStart();
        /* If the hitbox doesn't have well defined shape it's unblockable */
        if (centerHB == null)
        	return false;
        
        Vector2 centerShield = new Vector2(getBlockingShape(logic.getDir()).getCenterX(), getBlockingShape(logic.getDir()).getCenterY());
        Vector2 centerBody = new Vector2(logic.getShape().getCenterX(), logic.getShape().getCenterY());
        return (centerHB.dst2(centerShield) < centerHB.dst2(centerBody));
    }
    
    private void absorb(Impact impact, Stats stats) {	
    	/* Let the shield take the impact */
    	item.takeImpact(impact);
    	
    	absorbCrush(impact, stats);
    	
    	/* Play a blocking sound */
    	Game.sounds.getBlockSound().play(Game.sounds.getSfxVolume() * 
				owner.getWorld().getSoundIntensity(owner.getCenterX(), owner.getCenterY()));
    	
    	/* Break block if stamina is insufficient or if the shield broke */
    	if (stats.getStat(CreatureStatType.STAMINA).isZero() || !item.isAlive())
    		breakBlock();
    }
    
    /**
     * Decrease stamina for blocking the impact
     */
    private void absorbCrush(Impact impact, Stats stats) {
    	stats.getStat(CreatureStatType.STAMINA).decreaseValue(
    			impact.getCrushPower() / item.getCrushResist());
    }
    
    private void breakBlock() {
    	owner.tryHold(GlobalVar.BREAK_BLOCK_HOLD);
    }
    
	private void computeDisplacement(Logic logic, Stats stats) {
		logic.updateDir(Direction.unitVectorToDir(movDir));	
		movDir.set(movDir.x * getRealDistance(stats), movDir.y * getRealDistance(stats)); 
	}
	
	private float getRealDistance(Stats stats) {
		return moveSpeed * GlobalVar.BASE_MOVESPEED * MOVESPEED_MOD;
	}
	
	private void checkMoveSpeed(Stats stats) {
		moveSpeed = stats.getEffectiveMoveSpeed();
	}
	
	private void updateBodyMotion(Graphics gfx, SimpleMotionType type, float delta) {
		gfx.updateSimple(type, delta);
	}
	
	private float computeRealBlockSpeed(Stats stats) {
		return stats.getEffectiveBlockSpeed() * item.getBlockSpeed();
	}
	
	/**
	 * For debugging
	 */
	public IndexedHitBox getHitBox(Logic logic) {
		hitBox.getShape(owner.getCenterX(), owner.getCenterY(), logic.getDir());
		return hitBox;
	}
	
	@Override
	public float getKnockbackResist() {
		if (blockState == BlockState.READY)
			return 1.5f;
		return 1;
	}
	
	public boolean hasStartedBlock() {
    	return (blockState == BlockState.READY) || (blockState == BlockState.SET_UP);
	}
	
	@Override
	public boolean isRecovering() {
		return (blockState == BlockState.SET_OFF);
	}

}
