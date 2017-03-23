package action.diary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import action.core.Game;
import action.diary.trigger.Trigger;
import action.diary.trigger.Trigger.TriggerInfo;
import action.event.Event;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Manages different stages of a quest
 * 
 * @author Andrea
 */
public class Node {

	private String text;
	private List<Trigger> triggers;
	
	public Node(NodeInfo info) {
		this.text = Game.assets.getDialog(info.textDialog);
		triggers = new ArrayList<Trigger>(info.triggers.size());
		for (TriggerInfo x : info.triggers)
			triggers.add(x.load());
	}
	
	public String getText() {
		return text;
	}
	
	public void takeEvent(Event event) {
		for (Iterator<Trigger> iter = triggers.iterator(); iter.hasNext();) {
			Trigger own = iter.next();
			own.takeEvent(event);
			if (own.isTriggered())
				iter.remove();
		}
	}
	
	public boolean isCompleted() {
		return triggers.isEmpty();
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class NodeInfo {
		
		public final int textDialog;
		public final List<TriggerInfo> triggers;
		
		public NodeInfo(int textDialog, List<TriggerInfo> triggers) {
			this.textDialog = textDialog;
			this.triggers = triggers;
		}

		public static NodeInfo loadFromXml(Element element) {
			return new NodeInfo(
        			element.getIntAttribute("text"),
        			loadTriggers(element.getChildByName("triggers"))
        			);
		}
		
		private static List<TriggerInfo> loadTriggers(Element element) {
			List<TriggerInfo> list = new ArrayList<TriggerInfo>();
			
			if (element == null)
				return list;

			for (int i = 0; i < element.getChildCount(); i++)
				list.add(TriggerInfo.loadFromXml(element.getChild(i)));
			
			return list;
		}
			
	}
	
}
