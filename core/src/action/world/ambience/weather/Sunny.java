package action.world.ambience.weather;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Andrea
 */
public class Sunny implements Weather {

	private static final float DARKNESS_MOD = 0;
	
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
		return new Color(0, 0, 0, DARKNESS_MOD);
	}

	@Override
	public WeatherType getType() {
		return WeatherType.SUNNY;
	}
}
