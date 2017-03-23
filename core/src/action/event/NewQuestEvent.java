package action.event;

import action.diary.QuestRequirement;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Event of a new quest
 * 
 * @author Andrea
 */
public class NewQuestEvent extends Event {

	private int newQuest;
	private QuestRequirement questRequirement;
	
	public NewQuestEvent(int newQuest, QuestRequirement questRequirement) {
		super(EventType.NEW_QUEST);
		this.newQuest = newQuest;
		this.questRequirement = questRequirement;
	}

	public int getNewQuest() {
		return newQuest;
	}

	public QuestRequirement getQuestRequirement() {
		return questRequirement;
	}
	
	public static Event loadFromXml(Element element) {
		return new NewQuestEvent(
				element.getIntAttribute("newQuest"),
    			QuestRequirement.loadFromXml(element.getChildByName("questRequirement"))
				);
	}
	
}
