package action.event;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Event at the death of a creature
 * 
 * @author Andrea
 */
public class DeathEvent extends Event {
	
	public final String creature;
	public final int amount;
	
	public DeathEvent(String creature, int amount) {
		super(EventType.DEATH);
		this.creature = creature;
		this.amount = amount;
	}

	public static Event loadFromXml(Element element) {
		return new DeathEvent(
				element.getAttribute("creature"),
				element.getIntAttribute("amount")
				);
	}
	
}
