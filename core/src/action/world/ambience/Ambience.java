package action.world.ambience;

import java.util.EnumMap;
import java.util.Map;

import action.core.Game;
import action.interfaces.GlobalVar;
import action.ui.GameUI.GameUImode;
import action.utility.timer.Timer;
import action.world.World;
import action.world.ambience.Clock.Time;
import action.world.ambience.weather.MeteoSimulator;
import action.world.ambience.weather.Rain;
import action.world.ambience.weather.Sunny;
import action.world.ambience.weather.Weather;
import action.world.ambience.weather.Weather.WeatherType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;



/**
 * Utility to manage time, atmospheric conditions, etc
 * 
 * @author Andrea
 */
public class Ambience {

	private World world;
	private Clock clock = new Clock(GlobalVar.CLOCK_SPEED);
	private MeteoSimulator meteo = new MeteoSimulator(clock.getTime());
	/** Current active weather */
	private WeatherType currentWeather = WeatherType.SUNNY;
	/** The next weather appointed by the changing process */
	private WeatherType nextWeather;
	/** Map with all weathers */
	private Map<WeatherType, Weather> weathers = new EnumMap<WeatherType, Weather>(WeatherType.class);
	private Mode mode = Mode.READY;
	private Color ambientColor;
	/** True if the scene is inside a building */
	private boolean inDoor;
	/** Greyscale intensity (0 disabled, 1 full) */
	private float greyscale = 0;
	/** Timer for greyscale application */
	private Timer greyTimer = new Timer(GlobalVar.GREYSCALE_TURN_TIME);
	/** True if the ambience is switching to greyscale */
	private boolean greyMode;
	
	public Ambience(World world) {
		this.world = world;
		initWeathers();
	}
	
	private void initWeathers() {
		weathers.put(WeatherType.SUNNY, new Sunny());
		weathers.put(WeatherType.RAIN, new Rain());
	}
	
	public void update(float delta) {
		clock.update(delta);
		
		meteo.update(getTime());
		changeWeather(meteo.getCurrent());
		
		weathers.get(currentWeather).update(delta);
		
		switch(mode) {
			case ENDING:
				if (weathers.get(currentWeather).isEnded()) {
					/** Start the new weather */
					currentWeather = nextWeather;
					weathers.get(currentWeather).start();
					mode = Mode.STARTING;
				}	
				break;
			case STARTING:
				if (weathers.get(currentWeather).isStarted()) {
					mode = Mode.READY;
				}
				break;
			default:
				break;
		}
		
		computeAmbientColor();
		computeGrayscale(delta);
	}
	
	public void draw() {
		weathers.get(currentWeather).draw(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public Time getTime() {
		return clock.getTime();
	}
	
	private void computeAmbientColor() {
		/* Get the illumination of the location */
		ambientColor = world.getIllumination();
		/* If out door apply weather mod and hour of the day mod */
		if (!inDoor) {
			//TODO must change both color and intensity
			//ambientColor.a = clock.getIllumination().a * weathers.get(currentWeather).getIllumination().a;
		}
	}
	
	private void computeGrayscale(float delta) {
		if (!greyMode)
			return;
		
		greyTimer.update(delta);
		greyscale = greyTimer.getProgress();
		if (greyscale == 1)
			world.getUI().setMode(GameUImode.GAMEOVER);
	}
	
	public void setGreyModeActive(boolean active) {
		/* Go on only if the current setting changes */
		if (active == greyMode)
			return;
		
		greyMode = active;
		if (!active) {
			greyTimer.restart();	
		}
		else
			world.getUI().setMode(GameUImode.OFF);
	}
	
	public void prepareAmbientRendering() {
		Game.shader.begin();
		Game.shader.setUniformi("u_lightmap", 1);
		Game.shader.setUniformf("ambient", ambientColor.r*ambientColor.a, ambientColor.g*ambientColor.a,
				ambientColor.b*ambientColor.a);
		Game.shader.setUniformf("greyscale", greyscale);
		Game.shader.end();
	}
	
	/**
	 * @return The color of the ambient illumination
	 */
	public Color getAmbientLight() {
		return ambientColor;
	}
	
	public void changeWeather(WeatherType newWeather) {
		if (newWeather == currentWeather || mode != Mode.READY || newWeather == null)
			return;
		/** Request to the current weather to end */
		weathers.get(currentWeather).end();
		/** Save which will be the next wheater */
		nextWeather = newWeather;
		
		mode = Mode.ENDING;
	}
	
	/**
	 * Immediately set a new weather
	 */
	public void setWeather(WeatherType weather) {
		currentWeather = weather;
		meteo.setWeather(weather);
		weathers.get(currentWeather).forceStart();
	}

	public void takeTransition() {
		meteo.setMeteoInfo(world.getWeatherInfo());
		setWeather(world.getWeatherInfo().randomWeather());
		computeAmbientColor();
	}
	
	public void takeShift() {
		meteo.setMeteoInfo(world.getWeatherInfo());
	}
	
	private enum Mode {
		
		/** Executing a weather */
		READY,
		/** Ending a weather */
		ENDING,
		/** Starting a weather */
		STARTING;
		
	}
}
