package action.diary.trigger;

import action.event.DeathEvent;
import action.event.Event;
import action.event.Event.EventType;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Trigger on the death of a creature
 * 
 * @author Andrea
 */
public class DeathTrigger extends Trigger {

	/** Storycode of the creature type to be killed */
	private String creature;
	/** Amount of creatures that must be killed */
	private int amount;
	
	public DeathTrigger(String creature, int amount) {
		super(TriggerType.DEATH);
		this.creature = creature;
		this.amount = amount;
	}
	
	public DeathTrigger(DeathTriggerInfo info) {
		this(info.creature, info.amount);
	}

	public String getCreatureCode() {
		return creature;
	}
	
	public int getAmount() {
		return amount;
	}

	@Override
	public void takeEvent(Event event) {
		/* Incoming event must be a death to do something meaningful */
		if (event.getType() != EventType.DEATH)
			return;
		
		DeathEvent evt = (DeathEvent) event;
		
		if (creature.equals(evt.creature))
			amount -= evt.amount;
	}

	@Override
	public boolean isTriggered() {
		return amount <= 0;
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class DeathTriggerInfo extends TriggerInfo {
		
		public final String creature;
		public final int amount;
		
		public DeathTriggerInfo(String creature, int amount) {
			super(TriggerType.DEATH);
			this.creature = creature;
			this.amount = amount;
		}

		@Override
		public Trigger load() {
			return new DeathTrigger(this);
		}
		
		public static TriggerInfo loadFromXml(Element element) {
			return new DeathTriggerInfo(
					element.getAttribute("creature"),
					element.getIntAttribute("amount")
					);
		}
		
	}
	
}
