package action.combat;

import java.util.ArrayList;
import java.util.List;

import action.combat.Damage.DamageLoader.Scaling;
import action.core.Game;
import action.entity.being.creature.Stats.CreatureStatType;
import action.interfaces.StatsMemory;
import action.utility.Body.DefenceType;
import action.utility.Statistic;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class Damage {

	private DamageType dmgType;
	private float power;
	/** Armor penetration power */
	private float armorPen;

	/**
	 * Creates a fixed damage
	 */
	public Damage(DamageType dmgType, float power, float armorPen) {
		this.power = power;
		this.dmgType = dmgType;
		this.armorPen = armorPen;
	}
	
	/**
	 * Creates a randomized damage
	 */
	public Damage(DamageType dmgType, float power, float armorPen, float variation) {
		this(dmgType, Statistic.randomizer(power, variation), armorPen);
	}

	public float getPower() {
		return power;
	}
	
	public float getArmorPen() {
		return armorPen;
	}
	
	public DamageType getDmgType() {
		return dmgType;
	}
	
	public boolean exists() {
		return (power > 0);
	}
	
	/**
	 * Reduce the power first by resistance and after by reduction
	 * 
	 * @param resistance Amount of reduction
	 * @param reduction Fixed amount to subtract
	 * @param armorPen True to take into account the damage armor penetration
	 */
	public void reduce(float resistance, float reduction, boolean armorPen) {
		reduce(resistance, reduction, (armorPen) ? this.armorPen : 0);
	}
	
	/**
	 * Reduce the power first by resistance and after by reduction using a given value of armor penetration
	 */
	private void reduce(float resistance, float reduction, float armorPen) {
		power -= power * (resistance * (1-armorPen)); 
		power -= reduction * (1-armorPen);
		if (power < 0)
			power = 0;
	}

	/**
	 * Reduce the power by a fixed number
	 * @param amount
	 * @return How much the power has been reduced
	 */
	public float reduce(float amount) {
		float memory = power;
		power -= amount;
		if (power < 0)
			power = 0;
		return memory - power;
	}
	
	/**
	 * @return A copy of the object
	 */
	public Damage copy() {
		return new Damage(dmgType, power, armorPen);
	}
	
    /**
 	 * @param stats Provide stats for scaling, null value means only the base damage will be computed
     * @return The real damage, after adding scaling
     */
    public static Damage computeDamage(StatsMemory stats, DamageLoader loader) {
    	float power = loader.power;
    	for (Scaling x : loader.scaling)
    		power += x.ratio * stats.getStat(x.stat).value();
    	
    	return new Damage(loader.dmgType, power, loader.variation);
    }
	
	
	/**
	 * @author Andrea
	 */
	public static class DamageLoader {
		
		public final DamageType dmgType;
		public float power;
		public final float armorPen;
		public final float variation;
		public final List<Scaling> scaling;
		
		public DamageLoader(DamageType dmgType, float power, float armorPen, float variation, List<Scaling> scaling) {
			this.dmgType = dmgType;
			this.power = power;
			this.armorPen = armorPen;
			this.variation = variation;
			this.scaling = scaling;
		}
		
		public void multiplyPower(float multiplier) {
			power = power * multiplier;
		}
		
		public static DamageLoader loadFromXML(Element element) {
		    return new DamageLoader(
					DamageType.valueOf(element.getAttribute("type")),
					element.getFloatAttribute("power"),
					element.getFloatAttribute("armorPen", 0),
					element.getFloatAttribute("variation", 0),
					Scaling.loadFromXML(element.getChildrenByName("scaling"))
		    		);
		}
		
		
		/**
		 * Holder for damage scaling
		 * 
		 * @author Andrea
		 */
		public static class Scaling {
			
			public final CreatureStatType stat;
			public final float ratio;
			
			public Scaling(CreatureStatType stat, float ratio) {
				this.stat = stat;
				this.ratio = ratio;
			}
			
			public static List<Scaling> loadFromXML(Array<Element> elements) {
		    	List<Scaling> scaling = new ArrayList<Scaling>(elements.size);
		    	Element child;
		        for (int j = 0; j < elements.size; j++) {
		        	child = elements.get(j);
		        	scaling.add(new Scaling(CreatureStatType.valueOf(child.getAttribute("stat")), 
		        			child.getFloatAttribute("ratio")));
		        }
		        return scaling;
		    }
			
		}
		
	}
	
	
	/**
	 * Enumeration for all possible types of damage
	 * 
	 * @author andrea
	 */
	public enum DamageType {
		
		PHYSICAL(3, DefenceType.RESISTANCE_PHYSICAL, DefenceType.REDUCTION_PHYSICAL, DefenceType.SOAK_PHYSICAL),
		ELEMENTAL(4, DefenceType.RESISTANCE_ELEMENTAL, DefenceType.REDUCTION_ELEMENTAL, DefenceType.SOAK_ELEMENTAL),
		PURE(5, null, null, null);
		
    	private String name;
    	private DefenceType resistance, reduction, soak;

    	DamageType(int nameId, DefenceType resistance, DefenceType reduction, DefenceType soak) {
    		this.name = Game.assets.getDialog(nameId);
    		this.reduction = reduction;
    		this.resistance = resistance;
    		this.soak = soak;
    	}
		
		@Override
		public String toString() {
			return name;
		}
		
		public DefenceType getResistance() {
			return resistance;
		}
		
		public DefenceType getReduction() {
			return reduction;
		}
		
		public DefenceType getSoak() {
			return soak;
		}

	}
}
