package action.ai;

import java.util.EnumMap;
import java.util.Map;

import action.ai.behaviour.Behaviour;
import action.ai.behaviour.Master;
import action.ai.personality.Personality;
import action.ai.personality.Personality.AiStatType;
import action.ai.personality.Properties;
import action.entity.being.creature.Creature;
import action.entity.being.creature.Mirror;
import action.utility.Statistic.StatisticInfo;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Artificial intelligence for Creatures
 *
 * @author Andrea
 */
public class Ai {
    
    private Creature owner;
    private Mirror mirror;
    private Behaviour behaviour;
    private Personality personality;
    
    public Ai(AiInfo info, Creature owner, Mirror mirror) {
        this.owner = owner;
        this.mirror = mirror;
        personality = new Personality(info);
        behaviour = new Master(this);
    } 
    
    public Creature getOwner() {
		return owner;
	}

	public Mirror getMirror() {
		return mirror;
	}

	public Personality getPersonality() {
		return personality;
	}

	public void behave(float delta) {
		if (!owner.isAlive())
			return;

    	behaviour.update(delta);
    }
    
    public void shiftPosition(float dx, float dy) {
    	if (behaviour != null)
    		behaviour.shiftPosition(dx, dy);
    }
    
    
    /**
     * @author Andrea
     */
    public static class AiInfo {
    	
    	public final Map<AiStatType, StatisticInfo> stats;
    	public final Properties properties;

		public AiInfo(Map<AiStatType, StatisticInfo> stats, Properties properties) {
			this.stats = stats;
			this.properties = properties;
		}
		
		public static AiInfo loadFromXml(Element element) {
			return new AiInfo(
					loadStats(element.getChildByName("statistics")),
					Properties.loadFromXml(element.getChildByName("properties"))
					);
		}
		
	    private static Map<AiStatType, StatisticInfo> loadStats(Element element) {
	    	Map<AiStatType, StatisticInfo> stats = new EnumMap<AiStatType, StatisticInfo>(AiStatType.class);
	        Element child;
	        for (int i = 0; i < element.getChildCount(); i++) {
	            child = element.getChild(i);
	            stats.put(AiStatType.valueOf(child.getAttribute("type")), StatisticInfo.loadFromXML(child));
	        }
	        return stats;
	    }
    	
    }
}
