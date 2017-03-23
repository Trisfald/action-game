package action.ai.behaviour.micro;

import java.util.ArrayList;
import java.util.List;

import action.ai.Ai;
import action.ai.behaviour.GenericBehaviour;
import action.entity.Entity;
import action.entity.enums.BlockType;
import action.utility.ExtraMath;
import action.utility.SquaredCirconference;
import action.utility.Vector2i;
import action.utility.timer.Timer;
import action.world.World;

/**
 * @author Andrea
 */
public class Movement extends GenericBehaviour {

	private Entity target;
	private float distance;
	private GoTo servant;
	/** The target position for which we have stored the destination */
	private Vector2i cachedTargetPos = new Vector2i();
	private MovementMode mode;
	/** Timer to check if the target has changed position */
	private Timer timer = new Timer(ROUTINE_TIME);
	/** True if blocking and walking at the same time */
	private boolean guarded;
	
	private static final float ROUTINE_TIME = 0.6f;
	/** Tells if the list of destination get reduced before computing pathfinding */
	private static final boolean FILTER_DESTINATION = true;
	
	public Movement(Ai ai, Entity target, MovementMode mode, float distance, boolean guarded) {
		super(ai);
		servant = new GoTo(ai, 1, guarded, true);
		this.target = target;
		this.mode = mode;
		this.distance = distance;
		this.guarded = guarded;
		findDestinations();
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.MOVEMENT;
	}

	private void findDestinations() {
		if (guarded)
			owner.tryBlock(BlockType.STANDARD);
		
		List<Vector2i> points = new ArrayList<Vector2i>();
		
		cachedTargetPos.set(World.getCreatureNodeX(target), World.getCreatureNodeY(target));
		
		/* Check if the distance is already the desired one */
		if (mode == MovementMode.FOLLOW) {
			if (owner.computeBorderDistance(target) < distance) {
				setState(BehaviourState.COMPLETED);
				return;
			}
		}
		else {
			if (owner.computeBorderDistance(target) > distance) {
				setState(BehaviourState.COMPLETED);
				return;
			}
		}
		
		/* Get the all the possible points for movement by using a circonference */
		points = new SquaredCirconference(World.getCreatureNodeX(target), 
				World.getCreatureNodeY(target), distance, World.NODE_SIZE)
				.getCirconferenceByNearest(World.getCreatureNodeX(owner), 
						World.getCreatureNodeY(owner));
		
		/* Destination filtering */
		if (FILTER_DESTINATION)
			points = ExtraMath.filterList(points, 0.6, 1.1);
		
		servant.updateDestinations(points);
		setState(BehaviourState.PAUSED);
	}
	
	@Override
	protected void doPaused(float delta) {
		servant.update(delta);

		if (servant.getState() == BehaviourState.COMPLETED) {
			setState(BehaviourState.COMPLETED);
		}
		else if (servant.getState() == BehaviourState.FAILED) {
			setState(BehaviourState.PROGRESS);
		}
	}
	
	private void refresh(float delta) {
		timer.update(delta);
		if (timer.expired()) {
			timer.restart();
			/* Time is up, recheck the target position. If different from the cached one recompute the destinations */
			if ((!(cachedTargetPos).equals(World.getCreatureNodeX(target), World.getCreatureNodeY(target)))) 	
				findDestinations();
		}
	}
	
	@Override
	protected void preAct(float delta) {
		refresh(delta);
	}
	
	@Override
	public void shiftPosition(float dx, float dy) {
		servant.shiftPosition(dx, dy);
	}
	
	public void setGuarded(boolean b) {
		guarded = b;
		servant.setGuarded(b);
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum MovementMode {
		
		FOLLOW,
		ESCAPE;
		
	}

}
