package action.event;

import com.badlogic.gdx.utils.XmlReader.Element;


/**
 * Base class for events
 * 
 * @author Andrea
 */
public abstract class Event {
	
	private EventType type;
	
	public Event(EventType type) {
		this.type = type;
	}
	
	public EventType getType() {
		return type;
	}
	
	public static Event loadFromXml(Element element) {
    	switch (EventType.valueOf(element.getAttribute("type"))) {
    		case NEW_QUEST:
    			return NewQuestEvent.loadFromXml(element);
    		case DEATH:
    			return DeathEvent.loadFromXml(element);
    		case MAP:
    			return MapEvent.loadFromXml(element);
			default:
				return null;
    	}
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum EventType {
		
		NEW_QUEST,
		DEATH,
		MAP;
		
	}

}
