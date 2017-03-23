package action.ai.behaviour.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.ai.Ai;
import action.ai.behaviour.GenericBehaviour;
import action.ai.behaviour.micro.GoTo;
import action.utility.ExtraMath;
import action.utility.Vector2i;
import action.utility.timer.Timer;
import action.world.World;

public class Wander extends GenericBehaviour {
	
	private Timer timer;
	private GoTo servant;
	/** Original position of the creature */
	private Vector2i pos;
	/* How many steps (in nodes) the creature can make in one direction */
	private int movRange;
	
	private static final float MIN_ROUTINE_TIME = 4;
	private static final float MAX_ROUTINE_TIME = 10;
	private static final float WANDERING_SPEED = 0.4f;

	/**
	 * Create a wander behaviour
	 * @param movRange How many steps (in world's standard unit) the creature can make in one direction
	 */
	public Wander(Ai ai, float movRange) {
		super(ai);
		this.movRange = Math.round(movRange / (float)World.NODE_SIZE);
		timer = new Timer(ExtraMath.random(MIN_ROUTINE_TIME, MAX_ROUTINE_TIME));
		pos = world.getCreatureNodePos(owner);
	}
	
	/**
	 * Create a wander behaviour with the ai default wander distance
	 */
	public Wander(Ai ai) {
		this(ai, ai.getPersonality().getProperties().wander);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.WANDER;
	}

	private void refresh(float delta) {
		timer.update(delta);
	}
	
	@Override
	protected void doProgress(float delta) {
		/* 0 movRange is for static-position AI and they don't need to do any movement */
		if (movRange == 0)
			return;
		
		refresh(delta);
		if (!timer.expired())
			return;
		
		/* Time up, prepare timer randomly */
		timer.setLength(ExtraMath.random(MIN_ROUTINE_TIME, MAX_ROUTINE_TIME));
		timer.restart();
		
		/* Get all possible destinations and sort them randomly */
		List<Vector2i> points = new ArrayList<Vector2i>();
		for (int i = -movRange; i <= movRange; i++)
			for (int j = -movRange; j <= movRange; j++) {
				points.add(new Vector2i(pos.x+j, pos.y+i));
			}
		Collections.shuffle(points);
		
		/* Enter a movement */
		servant = new GoTo(ai, points, WANDERING_SPEED, false, false);
		setState(BehaviourState.PAUSED);
	}
	
	@Override
	protected void doPaused(float delta) {
		servant.update(delta);
		if (servant.isFinished()) {
			setState(BehaviourState.PROGRESS);
		}
	}

}
