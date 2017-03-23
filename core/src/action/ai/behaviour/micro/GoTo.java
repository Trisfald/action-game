package action.ai.behaviour.micro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import action.ai.Ai;
import action.ai.behaviour.GenericBehaviour;
import action.entity.enums.BlockType;
import action.interfaces.GlobalVar;
import action.utility.Vector2i;
import action.utility.pathFinding.MultiPathFinder;
import action.utility.pathFinding.Path;
import action.utility.timer.Timer;
import action.utility.timer.TimerInt;
import action.world.World;

import com.badlogic.gdx.math.Vector2;

public class GoTo extends GenericBehaviour {

	private Vector2i destination = new Vector2i();
	private List<Vector2i> destinations;
	private Path path;
	/** Count at which step we are pointing the movement */
	private int stepIndex = 0;
	/** Cached owner position */
	private Vector2 ownerPos;
	/** Vector with the movement direction of one micro update */
	private Vector2 dirV = new Vector2();
	/** Relative speed of the creature's movements */
	private float speed;
	/** Temporary vector for computations */
	private Vector2 temp = new Vector2();
	
	private Timer timer = new Timer(ROUTINE_TIME);
	/** Timer to avoid stuck situations */
	private Timer stuckTimer = new Timer(STUCK_TIME);
	/** Timer for smooth direction changing during movement */
	private TimerInt movTimer = new TimerInt(MOV_TIMER_LOAD, MOV_TIMER_LOAD, false);
	
	private Callable<Path> pathFinder;
	private FutureTask<Path> task;
	
	/** Tells if a path has been computed */
	private boolean pathComputed = false;
	/** True if the behaviour stops after the first failure */
	private boolean retry;	
	/** True if blocking and walking at the same time */
	private boolean guarded;

	private static final float ROUTINE_TIME = 5;
	/** After how much time of no movement we can say the unit is stuck */
	private static final float STUCK_TIME = 1;
	/** Once how many updates the creature is allowed to change direction of movement */
	private static final int MOV_TIMER_LOAD = 8;
	
	
	/**
	 * Create a movement with many possible destinations
	 * 
	 * @param guarded True if the creature walk with the block active
	 * @param retry True if the movement does not stop after the first failure to find a path
	 */
	public GoTo(Ai ai, List<Vector2i> destinations, float speed, boolean guarded, boolean retry) {
		super(ai);
		this.speed = speed;
		this.guarded = guarded;
		this.retry = retry;
		this.destinations = destinations;
		computePath();
	}
	
	/**
	 * Create a movement with only one destination
	 * 
	 * @param guarded True if the creature walk with the block active
	 * @param retry True if the movement does not stop after the first failure to find a path
	 */
	public GoTo(Ai ai, Vector2i destination, float speed, boolean guarded, boolean retry) {
		super(ai);
		this.speed = speed;
		this.guarded = guarded;
		this.retry = retry;
		this.destinations = new ArrayList<Vector2i>();
		destinations.add(destination);
		computePath();
	}
	
	/**
	 * Create a movement holder
	 * 
	 * @param guarded True if the creature walk with the block active
	 * @param retry True if the movement does not stop after the first failure to find a path
	 */
	public GoTo(Ai ai, float speed,boolean guarded, boolean retry) {
		super(ai);
		this.speed = speed;
		this.guarded = guarded;
		this.retry = retry;
		setState(BehaviourState.PAUSED);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.GOTO;
	}
	
	private void computePath() {
		pathComputed = false;
		pathFinder = new MultiPathFinder(owner, world, world.getEntities(owner, GlobalVar.MAX_PATH_LENGTH * World.NODE_SIZE),
				destinations, GlobalVar.MAX_PATH_LENGTH);
		task = new FutureTask<Path>(pathFinder);
		Thread thread = new Thread(task);
		thread.start();
	}
	
	@Override
	protected void preAct(float delta) {
		refresh(delta);
		checkPath();
	}
	
	@Override
	protected void doProgress(float delta) {
		if (guarded)
			owner.tryBlock(BlockType.STANDARD);
		
		/* No path, nothing to do */
		if (path == null)
			return;
		
		/* Control if the creature reached the next step */
		if ((World.getCreatureNodeX(owner) == path.getStep(stepIndex).getX()) &&
				(World.getCreatureNodeY(owner) == path.getStep(stepIndex).getY()))
			stepIndex++;
		else
			/* If not, he may be stuck */
			if (owner.getPos().equals(ownerPos))
				stuckTimer.update(delta);
			else {
				ownerPos = owner.getPos();
				stuckTimer.restart();
			}
		
		/* Stop here if we reach the end of the path */
		if (stepIndex == path.getLength()) {
			setState(BehaviourState.COMPLETED);
			path = null;
			return;
		}
				
		/* If it arrives here it means that it has to try to move toward the next step */
		movTimer.update(1);
		temp.set(path.getStep(stepIndex).getX() - World.getCreatureNodeX(owner),
				path.getStep(stepIndex).getY() - World.getCreatureNodeY(owner));
		/* Direction must change smoothly (not oscillating) */
		if (!temp.equals(dirV)) {
			if (movTimer.expired()) {
				dirV.set(temp);	
				movTimer.restart();
			}
		}
		owner.tryMovement(dirV.nor(), speed);
	}

	private void refresh(float delta) {
		timer.update(delta);
		if (timer.expired() || stuckTimer.expired()) {
			/* Time is up, re-check the path */
			computePath();
			timer.restart();
			stuckTimer.restart();
			stepIndex = 0;
		}
	}
	
	/**
	 *  When the task is done see if the path has been found or not
	 */
	private void checkPath() {
		if (!pathComputed && task.isDone()) {
			try {	
				path = task.get();
				stepIndex = 0;
				pathComputed = true;
				if (path != null) {
					destination.set(path.getX(path.getLength()-1), path.getY(path.getLength()-1));
					ownerPos = owner.getPos();
				}
				else
					if (!retry)
						setState(BehaviourState.FAILED);

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateDestination(Vector2i newDest) {
		if (!newDest.equals(destination)) {
			List<Vector2i> list = new ArrayList<Vector2i>();
			list.add(newDest);
			updateDestinations(list);
		}
	}
	
	public void updateDestinations(List<Vector2i> newDests) {
		destinations = newDests;
		computePath();
		setState(BehaviourState.PROGRESS);
	}
	
	@Override
	public void shiftPosition(float dx, float dy) {
		/* Update all the steps in the path */
		if (path != null)
			for (int i = stepIndex; i < path.getLength(); i++) {
				/* Must convert from real coords to nodes coords */
				path.getStep(i).updateX(dx / (float) World.NODE_SIZE);
				path.getStep(i).updateY(dy / (float) World.NODE_SIZE);
			}
	}
	
	public void setGuarded(boolean b) {
		guarded = b;
	}
	
}