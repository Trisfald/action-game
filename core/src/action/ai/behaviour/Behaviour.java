package action.ai.behaviour;


/**
 * @author Andrea
 */
public interface Behaviour {
	
	public void update(float delta);
	
	public BehaviourState getState();
	
	public void setState(BehaviourState state);
	
	public void shiftPosition(float dx, float dy);
	
	public BehaviourType getType();
	
	/**
	 * @return True if the behaviour is completed, or failed
	 */
	public boolean isFinished();
	
	/**
	 * Defines the states of a behaviour
	 * 
	 * @author Andrea
	 */
	public enum BehaviourState {
		
		PROGRESS,
		PAUSED,
		COMPLETED,
		FAILED;
		
	}
	
	/**
	 * All the possible behaviours
	 * 
	 * @author Andrea
	 */
	public enum BehaviourType {
		
		FIGHT,
		GOTO,
		MELEE_ATTACK,
		MOVEMENT,
		SHIELD, 
		WANDER, 
		IDLE, 
		FACE,
		MASTER, 
		SHOOT;
		
	}

}