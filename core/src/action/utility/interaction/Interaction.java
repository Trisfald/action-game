package action.utility.interaction;

import action.entity.Interactable.InteractionType;

/**
 * Class to represent a non combat oriented interaction between the player and an object
 * 
 * @author Andrea
 */
public class Interaction {

	private InteractionType type;

	public Interaction(InteractionType type) {
		this.type = type;
	}
	
	public InteractionType getType() {
		return type;
	}
	
}
