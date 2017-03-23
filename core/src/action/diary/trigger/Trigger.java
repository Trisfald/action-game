package action.diary.trigger;

import action.diary.trigger.DeathTrigger.DeathTriggerInfo;
import action.diary.trigger.MapTrigger.MapTriggerInfo;
import action.event.Event;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Base trigger class
 * 
 * @author Andrea
 */
public abstract class Trigger {

	private TriggerType type;
	
	public Trigger(TriggerType type) {
		this.type = type;
	}
	
	public TriggerType getType() {
		return type;
	}
	
	/**
	 * Take a general event and updates this trigger
	 */
	public abstract void takeEvent(Event event);
	
	/**
	 * @return True if this trigger has been set off
	 */
	public abstract boolean isTriggered();
	
	
	
	/**
	 * @author Andrea
	 */
	public enum TriggerType {
		
		DEATH,
		MAP;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public abstract static class TriggerInfo {
		
		public final TriggerType type;
		
		public TriggerInfo(TriggerType type) {
			this.type = type;
		}
		
		public abstract Trigger load();
		
		public static TriggerInfo loadFromXml(Element element) {
    		switch (TriggerType.valueOf(element.getAttribute("type"))) {
    			case DEATH:
    				return DeathTriggerInfo.loadFromXml(element);
    			case MAP:
    				return MapTriggerInfo.loadFromXml(element);
				default:
					return null;
    		}
		}
		
	}
	
}
