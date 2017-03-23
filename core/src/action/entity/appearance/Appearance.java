package action.entity.appearance;

import action.entity.being.creature.action.Action.ActionType;
import action.entity.enums.BusyLevel;


/**
 * A simple interface to access entity's data from the point of view of another entity
 * 
 * @author Andrea
 */
public interface Appearance {

	public float ratioHP();
	
	public float ratioStamina();
	
	public ActionType getActionType();

	public BusyLevel getBusy();
	
	/**
	 * @return True if the entity is recovering and vulnerable to attacks
	 */
	public boolean isRecovering();
	
}
