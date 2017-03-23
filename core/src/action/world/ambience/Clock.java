package action.world.ambience;

import action.utility.ExtraMath;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * Class to manage in-game time
 * 
 * @author Andrea
 */
public class Clock {
	
	private int hour = START_HOUR;
	private int minute = START_MINUTE;
	private float timer = 0;
	/** How many real seconds are needed to form a minute */
	private float msMinute = 0;
	
	private static final int START_HOUR = 17;
	private static final int START_MINUTE = 0;

	private static final int HOURS = 24;
	private static final int MINUTES = 60;

	private static final float MAX_DARK = 0.6f;
	private static final float MIN_DARK = 0;

	/**
	 * @param ratio How many times is this clock faster than reality
	 */
	public Clock(float ratio) {
		msMinute = 60f / ratio;
	}
	
	public void update(float delta) {	
		increaseHour(increaseMinute(increaseTimer(delta)));
	}
	
	/**
	 * @return How many minutes have passed
	 */
	private int increaseTimer(float delta) {
		float result;
		if (delta == 0)
			return 0;
		timer += delta;
		result = timer / msMinute;
		if (result >= 1)
			timer = timer*result - msMinute;
		return MathUtils.floor(result);
	}
	
	/**
	 * @param i Minutes to add
	 * @return How many hours have passed
	 */
	private int increaseMinute(int i) {
		int result;
		if (i == 0)
			return 0;
		minute += i;
		result = minute / MINUTES;
		if (result >= 1)
			minute = minute % MINUTES;
		return result;
	}
	
	/**
	 * @param i Hours to add
	 */
	private void increaseHour(int i) {
		if (i == 0)
			return;
		hour += i;
		hour = hour % HOURS;
	}
	
	/**
	 * @return The value of darkness (between 0.0 and 1.0) associated to the current time of the day
	 */
	private float getDarkness() {
		/* Night */
		if (hour >= 23 || hour < 5 ) 
			return MAX_DARK;
		/* Day */
		else if (hour >= 10 && hour < 18) 
			return MIN_DARK;
		/* Morning */
		else if (hour >= 5 && hour < 10)
			return (1 - ExtraMath.advancement(5, hour + minuteToHour(), 10)) * MAX_DARK; 
		/* Evening */
		else
			return ExtraMath.advancement(18, hour + minuteToHour(), 23) * MAX_DARK;
	}
	
	/**
	 * Convert minute from base 60 to base 1
	 */
	private float minuteToHour() {
		return ((float) minute / 60);
	}
	
	public Color getIllumination() {
		return new Color(0, 0, 0, getDarkness());
	}
	
	/**
	 * @return The current time
	 */
	public Time getTime() {
		return new Time(hour, minute);
	}
	
	
	/**
	 * Class that represent a measurement of time
	 * 
	 * @author Andrea
	 */
	public static class Time {
		
		private int hour;
		private int minute;
		
		public Time(int hour, int minute) {
			this.hour = hour;
			this.minute = minute;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}
				
		@Override
		public String toString() {
			return hour + ":" + minute;
		}
		
	}
}
