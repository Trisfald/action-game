package action.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import action.core.Game;
import action.utility.geom.Polygon;
import action.utility.geom.Shape;

public class ExtraMath {

	
	/**
	 * Find how many divisions are needed to represent the two number less that the size specified
	 */
	public static int findDivisionsNumber(float n, float m, float size) {
		/* Find maximum */
		float max = Math.max(Math.abs(n), Math.abs(m));
		/* Find number of iterations */
		return (int) Math.ceil(max / size);
	}
	
	/**
	 * Find the minimum integer in the collection
	 */
	public static int minimum(Collection<Integer> collection) {
		int min = Integer.MAX_VALUE;
		for (int i : collection)
			if (i < min)
				min = i;
		return min;
	}
	
	/**
	 * Find the maximum integer in the collection
	 */
	public static int maximum(Collection<Integer> collection) {
		int max = Integer.MIN_VALUE;
		for (int i : collection)
			if (i > max)
				max = i;
		return max;
	}
	
	/**
	 * Find the maximum float
	 */
	public static float maximum(float... numbers) {
		float max = Float.NEGATIVE_INFINITY;
		for (float i : numbers) 
			if (i > max)
				max = i;
		return max;
	}
	
	public static <T> List<T> filterList(List<T> original, double interval, double grow) {
		List<T> list = new ArrayList<T>();
		int i = 1; 
		double counter = 0;
		
		if (original.isEmpty())
			return list;
		
		/* Pick the first element */
		list.add(original.get(0));

		for (; i < original.size(); i++){
			counter += interval;
			if (counter >= 0.5) {
				/* Pick one element, reset counter and decrease the interval */
				counter = 0;
				list.add(original.get(i));
				interval = Math.pow(interval, grow);
			}
		}
			
		return list;
	}
	
	/**
	 * @return The percent of advancement of the current value with the given extremas
	 */
	public static float advancement(float start, float current, float end) {
		return (current - start) / (end - start);
	}
	
	/**
	 * @return The percent of advancement (capped from 0.0 to 1.0) of the current value with the given extremas
	 */
	public static float cappedAdvancement(float start, float current, float end) {
		float result = advancement(start, current, end);
		
		if (result < 0)
			return 0;
		else if (result > 1)
			return 1;
		else
			return result;
	}
	
	/**
	 * @param start Start of the interval
	 * @param percent Progress in percent
	 * @param end End of the interval
	 * @return The progress in a differt interval from the standard 0 to 1
	 */
	public static float progress(float start, float percent, float end) {
		float intervalLength = end - start;
			if (intervalLength <= 0)
				return 0;
			
		/* Extend in the interval */
		float result = intervalLength * percent;
		
		/* Shift */
		result += start;
		
		return result;
	}
	
	/**
	 * @return The inverse of the number
	 */
	public static float inverse(float n) {
		if (n == 0)
			return Float.MAX_VALUE;
		return 1f / n; 
	}
	
	/**
	 * Works only with positive intervals
	 * 
	 * @return A random number in the interval
	 */
	public static int random(int start, int end) {
		if (start == end)
			return start;
		return Math.round(Game.master().random() * (end - start) + start);
	}
	
	/**
	 * Works only with positive intervals
	 * 
	 * This random is undependant from the network
	 * 
	 * @return A random number in the interval
	 */
	public static int personalRandom(int start, int end) {
		if (start == end)
			return start;
		return Math.round((float) Math.random() * (end - start) + start);
	}
	
	
	/**
	 * Works only with positive intervals
	 * 
	 * @return A random number in the interval
	 */
	public static float random(float start, float end) {
		if (start == end)
			return start;
		Random random = Game.master().getRandom();
		return random.nextFloat() * (end - start) + start;
	}
	
	/**
	 * Make a random check
	 * @param p Probability of success. True randomness lies in the interval from 0 inclusive to 1 exclusive.
	 * @return True if it's a success
	 */
	public static boolean randomCheck(float p) {
		return (Game.master().random() < p);
	}
	
	/**
	 * Fit a number in the given interval
	 * 
	 * @return The number after the correction
	 */
	public static float fitFloat(float n, float min, float max) {
		if (n < min)
			return min;
		else if (n > max)
			return max;
		return n;
	}
	
	/**
	 * Fit a number in the given interval
	 * 
	 * @return The number after the correction
	 */
	public static int fitInt(int n, int min, int max) {
		if (n < min)
			return min;
		else if (n > max)
			return max;
		return n;
	}
	
	/**
	 * @return The distance between two points
	 */
	public static double distance2Points(int x1, int y1, int x2, int y2) {
		return (Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2)));
	}
	
	/**
	 * @param positive True if the result must be positive, false if it must be negative
	 * 
	 * @return The smallest of the two numbers in absolute value
	 */
	public static float minAbs(float n1, float n2, boolean positive) {
		float n = Math.min(Math.abs(n1), Math.abs(n2));
		
		if (positive)
			return n;
		return -n;
	}
	
	/**
	 * @return A copy of this shape
	 */
	public static Shape copyShape(Shape shape) {
		return new Polygon(shape.getPoints());	
	}
	
}
