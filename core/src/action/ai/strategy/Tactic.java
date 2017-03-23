package action.ai.strategy;

import action.ai.behaviour.Behaviour.BehaviourType;
import action.entity.Entity;

public interface Tactic {

	public BehaviourType getType();

	public void decide(Entity enemy);

	/**
	 * Do action at the end of this tactic
	 */
	public void conclude();
	
	
	/**
	 * Represents varius modes for the AI to position itself in respect to the enemy
	 * 
	 * @author Andrea
	 */
	public enum TacticPosition {
		
		/** Position for fighting in melee */
		MELEE,
		/** Position for shooting from afar */
		RANGED;
		
	}

}