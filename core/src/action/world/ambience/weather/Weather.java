package action.world.ambience.weather;

import com.badlogic.gdx.graphics.Color;

public interface Weather {

	/**
	 * Draw the weather
	 * @param width Canvas width
	 * @param height Canvas height
	 */
	public void draw(int width, int height);
	
	public void update(float delta);
	
	public void start();
	
	public void forceStart();
	
	public void end();
	
	public WeatherType getType();
	
	public boolean isStarted();
	
	public boolean isEnded();

	/**
	 * @return The modification to the ambient illumination provided by this weather
	 */
	public Color getIllumination();
	
	
	public enum WeatherType {
		
		SUNNY,
		RAIN;
		
	}

}
