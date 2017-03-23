package action.utility;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import action.combat.Damage.DamageType;
import action.combat.effect.Effect;
import action.combat.effect.Effect.EffectType;
import action.combat.effect.Status;
import action.combat.effect.Status.StatusType;
import action.core.Game;
import action.utility.Statistic.StatisticInfo;

import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * Holder class for physical properties of a body
 * 
 * @author Andrea
 *
 */
public class Body {

	private Map<BodyStatType, Statistic> stats = new EnumMap<BodyStatType, Statistic>(BodyStatType.class);
	private Map<DefenceType, Statistic> defences = new EnumMap<DefenceType, Statistic>(DefenceType.class);
	private EnumSet<StatusType> immunities;
	
	public Body(BodyInfo info) {
    	for (Map.Entry<BodyStatType, StatisticInfo> x : info.stats.entrySet())
    		stats.put(x.getKey(), new Statistic(x.getValue()));   
    	for (Map.Entry<DefenceType, StatisticInfo> x : info.defences.entrySet())
    		defences.put(x.getKey(), new Statistic(x.getValue()));   
    	immunities = EnumSet.copyOf(info.immunities);
	}
	
    public Statistic getStat(BodyStatType type) {
    	return stats.get(type);
    }
    
	public float getResistanceValue(DamageType type) {
		return defences.get(type.getResistance()).value();
	}
	
	public float getReductionValue(DamageType type) {
		return defences.get(type.getReduction()).value();
	}
	
	public float getSoakValue(DamageType type) {
		return defences.get(type.getSoak()).value();
	}
	
	public Statistic getStat(DefenceType type) {
		return defences.get(type);
	}
	
	public boolean isImmune(Effect effect) {
		/* Can be immune only to a status - effect */
		if (effect.getEffectType() == EffectType.STATUS)
			return immunities.contains(((Status) effect).getType());
		return false;
	}
	
	public boolean isAlive() {
		return (getStat(BodyStatType.HP).value() > 0);
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class BodyInfo {
		
        public final Map<BodyStatType, StatisticInfo> stats;
        public final Map<DefenceType, StatisticInfo> defences;
        public final EnumSet<StatusType> immunities;

		public BodyInfo(Map<BodyStatType, StatisticInfo> stats, Map<DefenceType, StatisticInfo> defences, EnumSet<StatusType> immunities) {
			this.stats = stats;
			this.defences = defences;
			this.immunities = immunities;
		}
		
	    public static BodyInfo loadFromXml(Element element) {
	        return new BodyInfo(loadStats(element.getChildByName("stats")), 
	        		loadDefences(element.getChildByName("defences")),
	        		loadImmunities(element.getChildByName("immunities")));
	    }
	    
	    private static Map<BodyStatType, StatisticInfo> loadStats(Element element) {
	    	Map<BodyStatType, StatisticInfo> stats = new EnumMap<BodyStatType, StatisticInfo>(BodyStatType.class);
	        Element child;
	        if (element == null)
	        	return stats;

	        for (int i = 0; i < element.getChildCount(); i++) {
	            child = element.getChild(i);
	            stats.put(BodyStatType.valueOf(child.getAttribute("type")), StatisticInfo.loadFromXML(child));
	        }
	        return stats;
	    }
	    
	    private static Map<DefenceType, StatisticInfo> loadDefences(Element element) {
	    	Map<DefenceType, StatisticInfo> stats = new EnumMap<DefenceType, StatisticInfo>(DefenceType.class);
	        Element child;
	        if (element == null)
	        	return stats;

	        for (int i = 0; i < element.getChildCount(); i++) {
	            child = element.getChild(i);
	            stats.put(DefenceType.valueOf(child.getAttribute("type")), StatisticInfo.loadFromXML(child));
	        }
	        return stats;
	    }
	    
	    private static EnumSet<StatusType> loadImmunities(Element element) {
	    	EnumSet<StatusType> set;
	        Element child;
	        if (element == null)
	        	return EnumSet.noneOf(StatusType.class);
	        
	    	List<StatusType> list = new ArrayList<StatusType>();

	        for (int i = 0; i < element.getChildCount(); i++) {
	        	child = element.getChild(i);
	            list.add(StatusType.valueOf(child.getAttribute("type")));
	        }
	        if (list.isEmpty())
	        	return EnumSet.noneOf(StatusType.class);
	        set = EnumSet.copyOf(list);
	    	return set;
	    }
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum BodyStatType {
		
		HP(20),
		MASS(21);
		
		private String name;
		
		BodyStatType(int nameId) {
			this.name = Game.assets.getDialog(nameId);
		}
		
		@Override
		public String toString() {
			return name;
		}

	}
	
	
	/**
	 * @author Andrea
	 */
	public enum DefenceType {
		
		/* Resistances and reductions */
		RESISTANCE_PHYSICAL(10),
		REDUCTION_PHYSICAL(11),
		RESISTANCE_ELEMENTAL(12),
		REDUCTION_ELEMENTAL(13),
		
		/* Soak for items */
		SOAK_PHYSICAL(14),
		SOAK_ELEMENTAL(15);
		
		private String name;
		
		DefenceType(int nameId) {
			this.name = Game.assets.getDialog(nameId);
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}

}
