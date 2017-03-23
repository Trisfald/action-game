package action.ai.personality;

import java.util.EnumMap;
import java.util.Map;

import action.ai.Ai.AiInfo;
import action.interfaces.GlobalVar;
import action.utility.Statistic;
import action.utility.Statistic.StatisticInfo;

/**
 * Ai statistics for creatures
 * 
 * @author Andrea
 */
public class Personality {

	private Map<AiStatType, Statistic> stats = new EnumMap<AiStatType, Statistic>(AiStatType.class);
	private Properties properties;
	
	public Personality(AiInfo info) {
    	for (Map.Entry<AiStatType, StatisticInfo> x : info.stats.entrySet())
    		stats.put(x.getKey(), new Statistic(x.getValue()));  
    	this.properties = info.properties;
	}
	
    public Statistic getStat(AiStatType type) {
    	return stats.get(type);
    }
    
    public Properties getProperties() {
    	return properties;
    }
    
    /**
     * @return The maximum distance at which it can see other entities
     */
    public float getSpotDistance() {
    	return stats.get(AiStatType.AWARENESS).factor() * GlobalVar.BASE_SPOT_DISTANCE; 
    }
    
    /**
     * @return The time for the reflexes of this creature
     */
    public float getReflexTime() {
    	return GlobalVar.BASE_REFLEX_TIME / stats.get(AiStatType.REFLEXES).factor();
    }
    
	
	/**
	 * @author Andrea
	 */
	public enum AiStatType {
		
		AGGRESSIVITY,
		INTELLIGENCE, 
		AWARENESS,
		DEFENCE, 
		REFLEXES;
		
	}
}
