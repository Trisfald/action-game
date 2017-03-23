package action.world.ambience.weather;

import action.world.ambience.Clock.Time;
import action.world.ambience.weather.Weather.WeatherType;

/**
 * Decides the current weather 
 * 
 * @author Andrea
 */
public class MeteoSimulator {
	
	private WeatherType current;
	/** Map data about weathers */
	private MeteoInfo info;
	/** Time of the last update */
	private Time lastUpdate;
	/** For how many hours the current weather has been running */
	private int streak;
	
	private static final float MULTIPLICATOR_START = 0.1f;
	/** How much the multiplicator decreases per hour */
	private static final float MULTIPLICATOR_STEP = 0.2f;
	
	public MeteoSimulator(Time time) {
		lastUpdate = time;
	}
	
	public void update(Time time) {
		/* Update only if an hour has passed and the info is valid */
		if (lastUpdate.getHour() == time.getHour() || info == null)
			return;
		lastUpdate = time;
		
		WeatherType picked = pickWeather();
		if (picked == current)
			streak++;
		else {
			current = picked;
			streak = 1;
		}
	}
	
	public WeatherType getCurrent() {
		return current;
	}
	
	public void setWeather(WeatherType type) {
		current = type;
		streak = 1;
	}
	
	private WeatherType pickWeather() {
		return info.randomWeather(current, computeMultiplicator());
	}
	
	private float computeMultiplicator() {
		/* Function is e^2/x */
		return (float) (Math.pow(Math.E,2)/(MULTIPLICATOR_START + MULTIPLICATOR_STEP * streak));
	}
	
	public void setMeteoInfo(MeteoInfo info) {
		this.info = info;
	}

}
