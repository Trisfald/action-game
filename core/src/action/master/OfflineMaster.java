package action.master;

import java.util.Random;

/**
 * Master used for offline play
 * 
 * @author Andrea
 */
public class OfflineMaster implements GameMaster {
	
	private Random random = new Random();

	@Override
	public void dismiss() {}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public float random() {
		return random.nextFloat();
	}

	@Override
	public Random getRandom() {
		return random;
	}

	@Override
	public void update() {

	}

}
