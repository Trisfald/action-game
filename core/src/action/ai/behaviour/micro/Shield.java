package action.ai.behaviour.micro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import action.ai.Ai;
import action.ai.behaviour.GenericBehaviour;
import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.BlockType;
import action.hitbox.DangerZone;
import action.utility.enums.Direction;
import action.utility.timer.Timer;
import action.world.Collision;

/**
 * Behaviour used to defend ourself
 * 
 * @author Andrea
 */
public class Shield extends GenericBehaviour {

	private Timer timer;
	private List<DangerZone> dangers = new ArrayList<DangerZone>();
	/** True if the shielding behaviour is done to counter some dangers */
	private boolean counter;
	
	/**
	 * Normal shielding
	 * @param owner
	 * @param dir Direction to face and block
	 * @param duration How much the block will last (-1 for infinite);
	 */
	public Shield(Ai ai, Direction dir, int duration) {
		super(ai);
		/* Turn in the right direction */
		owner.setDir(dir);
		timer = new Timer(duration);
	}
	
	/**
	 * Shielding without a fix duration
	 * @param owner
	 * @param dir Direction to face and block
	 */
	public Shield(Ai ai, Direction dir) {
		this(ai, dir, -1);
	}
	
	/**
	 * Shielding from a danger
	 */
	public Shield(Ai ai, DangerZone danger) {
		super(ai);
		owner.setDir(danger.getBlockingDir(owner.getCenterX(), owner.getCenterY()));
		counter = true;
		dangers.add(danger);
		timer = new Timer(ai.getPersonality().getReflexTime());
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.SHIELD;
	}
	
	@Override
	protected void doProgress(float delta) {
		if (!refresh(delta))
			setState(BehaviourState.PAUSED);
		owner.tryBlock(BlockType.STANDARD);
		
		/* If action is different from block it means that somehow the block has been broken */
		if (ai.getMirror().currentAction() != ActionType.BLOCK)
			setState(BehaviourState.FAILED);
	}
	
	@Override
	protected void doPaused(float delta) {
		/* Wait for the completion of the animation and then change state */
		if (ai.getMirror().currentAction() != ActionType.BLOCK)
			setState(BehaviourState.COMPLETED);
	}
	
	/**
	 * @return False if the duration of the block expired
	 */
	private boolean refresh(float delta) {
		/* If counter, block until dangers last */
		if (counter) {
			refreshDangers();
			if (dangers.isEmpty()) {
				/* Let some time pass before releasing the block */
				timer.update(delta);
				return !timer.expired();
			}
			else
				return true;
		}
		
		/* Else is based on time */
		timer.update(delta);
		return !timer.expired();
	}
	
	/**
	 * Removes any inactive danger
	 */
	private void refreshDangers() {
		for (Iterator<DangerZone> iter = dangers.iterator(); iter.hasNext();) {
			DangerZone x = iter.next();
			if (!x.isActive() || !Collision.collide(x.getHitBox().getShape(), owner.getShape()))
				iter.remove();
		}
			
	}

}
