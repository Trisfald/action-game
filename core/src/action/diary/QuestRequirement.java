package action.diary;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Utility to check if the diary has certain quest in a precise state
 * 
 * @author Andrea
 */
public class QuestRequirement {
	
	/** Quest that must be active */
	private List<Integer> active;
	/** Quest that must be completed */
	private List<Integer> completed;
	/** Quest that must not be active */
	private List<Integer> notActive;
	/** Quest that must not be completed */
	private List<Integer> notCompleted;
	
	public QuestRequirement(List<Integer> active, List<Integer> completed, List<Integer> notActive, List<Integer> notCompleted) {
		this.active = active;
		this.completed = completed;
		this.notActive = notActive;
		this.notCompleted = notCompleted;
	}

	public List<Integer> getActive() {
		return active;
	}

	public List<Integer> getCompleted() {
		return completed;
	}

	public List<Integer> getNotActive() {
		return notActive;
	}

	public List<Integer> getNotCompleted() {
		return notCompleted;
	}
	
	/**
	 * @param element Null allowed
	 * 
	 * @return A QuestRequirement or null
	 */
	public static QuestRequirement loadFromXml(Element element) {
    	if (element == null)
    		return null;
    	
    	return new QuestRequirement(
    			loadQuestRequirementList(element, "active"),
    			loadQuestRequirementList(element, "completed"),
    			loadQuestRequirementList(element, "notActive"),
    			loadQuestRequirementList(element, "notCompleted")
    			);
	}
	
	/**
	 * Load a single list of quest requirements from Xml
	 */
    private static List<Integer> loadQuestRequirementList(Element element, String name) {
    	Array<Element> children = element.getChildrenByName(name);
    	List<Integer> list = new ArrayList<Integer>(children.size);
    	
    	for (int i = 0; i < children.size; i++)
    		list.add(new Integer(children.get(i).getIntAttribute("id")));
    	
    	return list;
    }

}
