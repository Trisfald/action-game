package action.diary.trigger;

import action.event.Event;
import action.event.Event.EventType;
import action.event.MapEvent;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Trigger on the current map
 * 
 * @author Andrea
 */
public class MapTrigger extends Trigger {
	
	/** Id of the target map */
	private int map;
	private boolean triggered;
	
	public MapTrigger(MapTriggerInfo info) {
		super(TriggerType.MAP);
		this.map = info.map;
	}
	
	public int getMap() {
		return map;
	}
	
	@Override
	public boolean isTriggered() {
		return triggered;
	}
	
	@Override
	public void takeEvent(Event event) {
		if (event.getType() != EventType.MAP)
			return;

		triggered = (map == ((MapEvent) event).map);
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class MapTriggerInfo extends TriggerInfo {
		
		public final int map;
		
		public MapTriggerInfo(int map) {
			super(TriggerType.MAP);
			this.map = map;
		}

		@Override
		public Trigger load() {
			return new MapTrigger(this);
		}
		
		public static TriggerInfo loadFromXml(Element element) {
			return new MapTriggerInfo(
					element.getIntAttribute("map")
					);
		}
		
	}

}
