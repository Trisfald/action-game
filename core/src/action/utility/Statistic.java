package action.utility;

import java.text.DecimalFormat;
import java.util.Random;

import action.core.Game;

import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * An utility to hold and manage statistics
 *
 * @author Andrea
 */
public class Statistic {

	private float value;
	private float maximum;
	private float flatBonus = 0;
	private float flatBonusMax = 0;
	private float flatMalus = 0;
	private float multiplier = 1;
	/** Tells if the statistic can have negative value */
	private boolean negative;
	/** Another statistic. Its ratio influences the multiplier */
	private Statistic link;
	/** How much does to other stat influence the multiplier */
	private float influence;
	
	/** The default, average value for a statistic */
	private static final float DEFAULT_AVG_VALUE = 100.0f;
	
	/** Format for decimal numbers */
	public final static DecimalFormat FORMAT = new DecimalFormat("0.#");
	
	public Statistic(StatisticInfo info) {
		this(info.value, info.variation, info.negative);
	}
	
	public Statistic(float value, float variation, boolean negative) {
		this.value = this.maximum = randomizer(value, variation);
		this.negative = negative;
	}
	
	/**
	 * Create a non negative, fixed valued statistic
	 */
	public Statistic(float value) {
		this(value, 0, false);
	}
	
	public float value() {
		return (value + flatBonus - getFlatMalus()) * getMultiplier();
	}
	
	public float maximum() {
		return (maximum + flatBonus - getFlatMalus()) * getMultiplier();
	}
	
	private float getFlatMalus() {
		if (!negative)
			/* If negative is not allowed, the malus cannot surpass the value plus the bonus */
			return Math.min(flatMalus, value + flatBonus);
		return flatMalus;
	}
	
	private float getMultiplier() {
		float m = multiplier;
		
		/* Link check */
		if (link != null)
			m -= m * ((1 - link.ratio()) * influence);
		
		/* Negative check */
		if (!negative && m < 0)
			return 0;
		
		return m;
	}
	
	public void setValue(float amount) {
		value = (amount - flatBonus + getFlatMalus()) / getMultiplier();
	}
	
	public void setValueToZero() {
		this.value = 0;
		this.flatBonus = 0;
	}

	public void reset() {
		flatBonus = 0;
		flatBonusMax = 0;
		flatMalus = 0;
		multiplier = 1;
	}
	
	/**
	 * @param amount
	 * @return How much the value has been effectively increased
	 */
	public float increaseValue(float amount) {
		if (getMultiplier() == 0)
			return 0;
		return updateValue(amount / Math.abs(getMultiplier())) * Math.abs(getMultiplier());
	}
	
	/**
	 * @return How much the value has been effectively decreased
	 */
	public float decreaseValue(float amount) {
		if (getMultiplier() == 0)
			return 0;
		return -updateValue(-amount / Math.abs(getMultiplier())) * Math.abs(getMultiplier());
	}
	
	/**
	 * Decrease the value by a fraction of itself
	 * @return How much the value has been effectively decreased
	 */
	public float decreaseValueByFraction(float fraction) {
		return decreaseValue(fraction * value());
	}
	
	public boolean decreaseValueIfEnough(float amount) {
		if (value() >= amount) {
			updateValue(-amount);
			return true;
		}
		return false;
	}
	
	/**
	 * @param amount
	 * @return The difference between the new value and the old one
	 */
	private float updateValue(float amount) {
		float before = value();
		
		if (amount == 0)
			return 0;
		else if (amount < 0) {
			/* Take from flat bonus and then from value */
			amount = takeFromFlat(-amount);
			takeFromValue(-amount);
		}
		else if (amount > 0) {
			/* Recover value and then the flat bonus */
			amount = addToValue(amount);
			addToFlat(amount);
		}
		
		return value() - before;
	}
	
	/**
	 * Subtract as much as it can from the flat bonus
	 * @return Amount after the subtraction
	 */
	private float takeFromFlat(float amount) {
		float difference = flatBonus - amount;
		if (difference >= 0) {
			/* Bonus absorbed the amount */
			flatBonus -= amount;
			return 0;
		}
		/* Bonus absorbed partially the amount */
		flatBonus = 0;
		return difference;
	}
	
	private void addToFlat(float amount) {
		flatBonus += amount;
		if (flatBonus > flatBonusMax)
			flatBonus = flatBonusMax;
	}
	
	/**
	 * Subtract as much as it can to value
	 * @return Amount not yet added
	 */
	private float addToValue(float amount) {
		float difference = maximum - value;
		value += amount;
		if (value > maximum) {
			value = maximum;
			return (amount - difference);
		}
		/* All amount absorbed */
		return 0;
	}
	
	private void takeFromValue(float amount) {
		value -= amount;
		/* Integrity checks */
		if (!negative && value < 0) {
			value = 0;
		}
	}
	
	public void increaseMaximum(float amount) {
		setMaximum(amount);
		value += amount;
	}
	
	public void decreaseMaximum(float amount) {
		setMaximum(-amount);
		if (value > maximum)
			value = maximum;
	}
	
	private void setMaximum(float amount) {
		maximum += amount;
		if (maximum < 0)
			maximum = 0;
	}
	
	public void increaseMultiplier(float amount) {
		multiplier += amount;
	}
	
	public void decreaseMultiplier(float amount) {
		multiplier -= amount;
	}
	
	public void increaseMultiplier(int amount) {
		multiplier += (float) amount/100f;
	}
	
	public void decreaseMultiplier(int amount) {
		multiplier -= (float) amount/100f;
	}	
	
	public void increaseFlatBonus(float amount) {
		if (amount < 0) 
			flatMalus -= amount;
		else {
			flatBonus += amount;
			flatBonusMax += amount;
		}
	}
	
	public void decreaseFlatBonus(float amount) {
		if (amount < 0)
			flatMalus += amount;
		else {
			flatBonus -= amount;
			if (flatBonus < 0)
				flatBonus = 0;
			flatBonusMax -= amount;
		}
	}
	
	/**
	 * Increase value by %/sec of the maximum
	 */
	public void recoverValuebyMaximum(float percent, float delta) {
		this.updateValue(((maximum()/100)*percent)*(delta));
	}
	
	/**
	 * Increase value by %/sec of the maximum
	 * @param balance Limit the recover to this threshold
	 */
	public void recoverValuebyMaximum(float percent, float balance, float delta) {
		/* First recover normally */
		recoverValuebyMaximum(percent, delta);
		
		/* Fix the value if it's more than the balance */
		if (value() > balance)
			setValue(balance);
	}
	
	/**
	 * Decrease value by %/sec of the maximum
	 */
	public void decayValuebyMaximum(float percent, float delta) {
		recoverValuebyMaximum(-percent, delta);
	}
	
	/**
	 * Decrease value by %/sec of the maximum
	 * @param balance Limit the decay to this threshold
	 */
	public void decayValuebyMaximum(float percent, float balance, float delta) {
		/* First decay normally */
		decayValuebyMaximum(percent, delta);
		
		/* Fix the value if it's less than the balance */
		if (value() < balance)
			setValue(balance);
	}
	
	/**
	 * Increase value by %/sec of the current value
	 * @param min Current value will be not considered lower than this number
	 */
	public void recoverValuebyCurrent(float percent, float min, float delta) {
		float current;
		if (value() / maximum() >= min)
			current = value();
		else
			current = maximum() * min;
		this.updateValue(((current/100)*percent)*(delta));
	}
	
	
	/**
	 * Randomize a number in the given interval (from -variation to +variation) with variation as a fraction of the original number.
	 * Uses the master's random number generator
	 */
    public static float randomizer(float n, float variation) {
    	return randomizer(n, variation, Game.master().getRandom());
    }
    
	/**
	 * Randomize a number in the given interval (from -variation to +variation) with variation as a fraction of the original number
	 */
    public static float randomizer(float n, float variation, Random rng) {
        if (n == 0)
            return 0;
        if (variation == 0)
        	return n;
        
        float random = rng.nextFloat() * variation;
        if (rng.nextFloat() >= 0.5)
            return (n*(1f + random));
        return (n*(1f - random));
    }
    
    /**
     * Return the minumum value of the number with the variation
     */
    public static float minimum(float n, float variation) {
        if (n == 0)
            return 0;
        if (variation == 0)
        	return n;
        
        return n-(n*variation);
    }
    
    /**
     * Return the maximum value of the number with the variation
     */
    public static float maximum(float n, float variation) {
        if (n == 0)
            return 0;
        if (variation == 0)
        	return n;
        
        return n+(n*variation);
    }
    
    /**
     * @return The interval of the number plus and minus its variation
     */
    public static Interval interval(float n, float variation) {
    	if (n == 0)
    		return new Interval(0);
        if (variation == 0)
        	return new Interval(n);
        
        return new Interval((n-(n*variation)), (n+(n*variation)));
    }
    
    /**
     * @return True if the value is equal or less than zero
     */
    public boolean isZero() {
    	return (value() <= 0);
    }
    
    /**
     * @return The ratio (in %) between the value and the maximum
     */
    public float filledPercentage() {
    	return (value() / maximum()) * 100;
    }
	
    /**
     * @return The ratio between the value and the maximum
     */
    public float ratio() {
    	return (value() / maximum());
    }
    
    /**
     * Computes the factor of this statistic.
     * The factor is centered around the average value (default 100) and it varies
     * with a less than linear behaviour.
     * <br>
     * <br>
     * Formula: y = (x / (x / 3 + ({@value #DEFAULT_AVG_VALUE} - {@value #DEFAULT_AVG_VALUE}/3)))
     * <br>
     * x = value
     */
    public float factor() {
    	return (value() / (value() / 3 + (DEFAULT_AVG_VALUE - DEFAULT_AVG_VALUE / 3)));
    }
    
    /**
     * Compute the linear version of the factor. 
     * <br>
     * <br> 
     * Formula y = x / {@value #DEFAULT_AVG_VALUE}
     */
    public float factorLinear() {
    	return (value() / DEFAULT_AVG_VALUE);
    }
    
    /**
     * Set another statistic to influence this one
     */
    public void setLink(Statistic link, float influence) {
    	this.link = link;
    	this.influence = influence;
    }
    
    /**
     * Remove the other statistic influence
     */
    public void removeLink() {
    	link = null;
    }
    
    /**
     * @return The current status of this statistic
     */
    public StatisticStatus getStatus() {
    	return (ratio() > 0.5) 
    			? StatisticStatus.GOOD 
    			: StatisticStatus.BAD;
    }
    
    /**
     * @return True if this statistic can assume negative values
     */
    public boolean canBeNegative() {
    	return negative;
    }
    
    
    /**
     * Info class for Statistic
     *
     * @author Andrea
     */
    public static class StatisticInfo {
    	
    	public float value;
    	public float variation;
    	public boolean negative;
    	
		public StatisticInfo(float value, float variation, boolean negative) {
			this.value = value;
			this.variation = variation;
			this.negative = negative;
		}
		
	    public static StatisticInfo loadFromXML(Element element) {
	    	return new StatisticInfo(element.getFloatAttribute("value"), 
	    			element.getFloatAttribute("variation", 0), 
	    			element.getBooleanAttribute("negative", false));
	    }
    }
    
    
    /**
     * An interval between two numbers
     * 
     * @author Andrea
     */
    public static class Interval {
    	
    	private float min;
    	private float max;
    	
    	public Interval(float a, float b) {
    		this.min = Math.min(a, b);
    		this.max = Math.max(a, b);
    	}
    	
    	public Interval(float n) {
    		min = max = n;
    	}

		public float getMin() {
			return min;
		}

		public float getMax() {
			return max;
		}
		
		public float getMean() {
			return (min + max) / 2;
		}
		
		/**
		 * Multiply this interval by a number
		 */
		public void multiply(float n) {
			min *= n;
			max *= n;
		}
		
		/**
		 * @param multiplier Multiplier to apply to the interval (for example use 100 to go from fraction to percent)
		 * 
		 * @return A text representation of the interval
		 */
		public String toString(float multiplier) {
			if (min == max)
				return FORMAT.format(min*multiplier);
			
			return FORMAT.format(min*multiplier) + " - " + FORMAT.format(max*multiplier);
		}
		
		@Override
		public String toString() {
			return toString(1);
		}
		
    }

    
    /**
     * Very brief description for the current status of a statistic
     * 
     * @author Andrea
     */
    public static enum StatisticStatus {
    	
    	GOOD,
    	BAD, 
    	ANY;

    	/**
    	 * @return True if this status is 'compatible' with a requested one
    	 */
		public boolean isCompatible(StatisticStatus request) {
			return (this == ANY || request == ANY || this == request);
		}
    	
    }
    
    
}

