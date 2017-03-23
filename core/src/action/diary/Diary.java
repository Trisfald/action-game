package action.diary;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import action.core.Game;
import action.diary.Quest.QuestState;
import action.event.Event;
import action.event.NewQuestEvent;
import action.event.Event.EventType;
import action.interfaces.GlobalVar;
import action.ui.GameUI;

/**
 * Class to manage quests
 * 
 * @author Andrea
 */
public class Diary {

	private GameUI ui;
	private Map<Integer, Quest> active = new LinkedHashMap<Integer, Quest>();
	private Set<Integer> completed = new HashSet<Integer>();
	

	public void setUI(GameUI ui) {
		this.ui = ui;
	}
	
	public Collection<Quest> getActive() {
		return active.values();
	}
	
	public void takeEvent(Event event) {
		/* Treat new quest event in a different way */
		if (event.getType() == EventType.NEW_QUEST)
			takeNewQuest((NewQuestEvent) event);
		else {
			/* All normal events are passed down to all quests */
			for (Iterator<Entry<Integer, Quest>> iter = active.entrySet().iterator(); iter.hasNext();) {
				Entry<Integer, Quest> entry = iter.next();
				entry.getValue().takeEvent(event);
				if (entry.getValue().getState() == QuestState.COMPLETED) {
					ui.addNotice(GlobalVar.ALERT_QUEST_COMPLETED + ": " + entry.getValue().getTitle());
					completed.add(entry.getKey());
					iter.remove();
				}
			}
		}
	}
	
	private boolean hasQuestActive(int quest) {
		return active.containsKey(quest);
	}
	
	private boolean hasQuestCompleted(int quest) {
		return completed.contains(quest);
	}

	/**
	 * Take a new quest proposal
	 * 
	 * @return True if the quest has been added to the current active ones
	 */
	public boolean takeNewQuest(NewQuestEvent event) {
		if (!checkRequirements(event.getQuestRequirement()))
			return false;

		takeNewQuest(event.getNewQuest());
		return true;
	}
	
	/**
	 * Take a new quest
	 */
	public void takeNewQuest(int questID) {
		/* Put quest in active */
		Quest quest = new Quest(ui, Game.assets.getQuestInfo(questID));
		quest.start();
		active.put(questID, quest);
	}

	/**
	 * Check requirements and return true if they are satisfied
	 */
	public boolean checkRequirements(QuestRequirement req) {
		if (req == null)
			return true;
		
		for (Integer i : req.getActive())
			if (!hasQuestActive(i))
				return false;
		for (Integer i : req.getCompleted())
			if (!hasQuestCompleted(i))
				return false;	
		for (Integer i : req.getNotActive())
			if (hasQuestActive(i))
				return false;
		for (Integer i : req.getNotCompleted())
			if (hasQuestCompleted(i))
				return false;
		
		return true;
	}
	
	public void clear() {
		active.clear();
		completed.clear();
	}

	public GameUI getUI() {
		return ui;
	}
	
}
