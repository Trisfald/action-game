package action.event;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Event on the change of the map
 * 
 * @author Andrea
 */
public class MapEvent extends Event {

	public final int map;

	public MapEvent(int map) {
		super(EventType.MAP);
		this.map = map;
	}
	
	public static Event loadFromXml(Element element) {
		return new MapEvent(
				element.getIntAttribute("map")
				);
	}
	
}
