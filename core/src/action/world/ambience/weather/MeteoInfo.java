package action.world.ambience.weather;

import java.util.List;

import action.world.ambience.weather.Weather.WeatherType;

/**
 * Class that holds information about all weather and their probability to happen
 * 
 * @author Andrea
 */
public class MeteoInfo {

	private final List<WeatherInfo> stats;
	
	public MeteoInfo(List<WeatherInfo> stats) {
		this.stats = stats;
	}
	
	/**
	 * @return True if there's only one allowed weather
	 */
	public boolean isSingle() {
		return stats.size() == 1;
	}
	
	/**
	 * Use only after checking that this meteo is single
	 * @return The weather type
 	 */
	public WeatherType getSingle() {
		return stats.get(0).weather;
	}
	
	/**
	 * Choose a weather in a weighted random way
	 */
	public WeatherType randomWeather() {
		float p = 0;
		float n = (float) Math.random();
		
		for (WeatherInfo i : stats) {
			p += i.probability;
			if (n < p)
				return i.weather;
		}

		return null;
	}
	
	/**
	 * Choose a random weather, but one of the possible weathers has his probability altered
	 */
	public WeatherType randomWeather(WeatherType type, float multiplicator) {
		WeatherInfo current = getWeather(type);

		/* If the current weather is not in the info, choose from the other in stardard way */
		if (current == null)
			return randomWeather();
		
		/* If the current probability is 1 or more, simply return it */
		float currentP = current.probability * multiplicator;		
		if (currentP >= 1)
			return current.weather;
		
		float p = 0;
		float n = (float) Math.random();
		/* Compute the scaling that must be applied to the other weaters probabilities in order to have total p equal to 1 */
		float scaling = 1 / ((1 - current.probability) / (1 - currentP));
		
		/* Check the weathers */
		for (WeatherInfo i : stats) {
			if (i.weather == type)
				p += currentP;
			else
				p += i.probability * scaling;
			
			if (n < p)
				return i.weather;
		}

		/* Return current in case the error on the scaling precision prevent the selection of any weather */
		return current.weather;		
	}
	
	public WeatherInfo getWeather(WeatherType type) {
		for (WeatherInfo i : stats)
			if (i.weather == type)
				return i;
		return null;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class WeatherInfo {
		
		public final WeatherType weather;
		public final float probability;
		
		public WeatherInfo(WeatherType weather, float probability) {
			this.weather = weather;
			this.probability = probability;
		}

	}
	
}
