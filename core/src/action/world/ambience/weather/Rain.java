package action.world.ambience.weather;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Andrea
 */
public class Rain implements Weather {
	
	private static final float DARK_MOD = 0.1f;
	/** Maximum time to grow rain or to stop it */
	private static final int MAX_SET_TIME = 45000;
	/** Minimum time to grow rain or to stop it */
	private static final int MIN_SET_TIME = 15000;
	
	public Rain() {
		
	}
	
	@Override
	public void draw(int width, int height) {
		
	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void start() {

	}
	
	@Override
	public void forceStart() {

	}

	@Override
	public void end() {

	}

	@Override
	public boolean isStarted() {
		return true;
	}

	@Override
	public boolean isEnded() {
		return true;
	}

	@Override
	public Color getIllumination() {
		return new Color(0, 0, 0, DARK_MOD);
	}

	@Override
	public WeatherType getType() {
		return WeatherType.RAIN;
	}

}
