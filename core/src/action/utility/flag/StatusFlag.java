package action.utility.flag;

import java.util.ArrayList;
import java.util.List;

/**
 * A special flag used to store a status's condition
 * 
 * @author Andrea
 */
public class StatusFlag extends Flag {
	
	/** List with all the current status givers */
	private List<StatusGiver> givers = new ArrayList<StatusGiver>();
	
	/**
	 * Issue an hold caused by a specific giver. This hold and the giver are linked: if one of them get released the other will be
	 * released too.
	 */
	public void hold(StatusGiver giver) {
		givers.add(giver);
		hold();
	}
	
	/**
	 * Release the hold caused by the specified giver. If the giver is not registered the hold won't be released.
	 */
	public void release(StatusGiver giver) {
		if (!givers.remove(giver))
			return;
		
		release();
	}
	
	/**
	 * Clear completely this status flag
	 */
	public void clear() {
		counter = 0;
		givers.clear();
	}
	
	
	
	/**
	 * Interface for objects able to inflict direct status alterations
	 * 
	 * @author Andrea
	 *
	 */
	public interface StatusGiver {

		
	}


}
