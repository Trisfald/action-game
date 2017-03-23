package action.entity;

import action.utility.interaction.Interaction;

/**
 * Interface for objects that can interact with the player
 * 
 * @author Andrea
 */
public interface Interactable {

	public Interaction getInteraction();
	
	
	
	/**
	 * @author Andrea
	 */
	public enum InteractionType {
		
		CHAT,
		INVENTORY;
		
	}
	
}
