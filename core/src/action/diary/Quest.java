package action.diary;

import java.util.ArrayList;
import java.util.List;

import action.core.Game;
import action.diary.Node.NodeInfo;
import action.event.Event;
import action.interfaces.GlobalVar;
import action.ui.Notifier;

/**
 * Class for a single quest
 * 
 * @author Andrea
 */
public class Quest {

	private Notifier notifier;
	private String title;
	private List<Node> nodes;
	/** Index of the current node */
	private int current = 0;
	private QuestState state = QuestState.PROGRESS;
	
	public Quest(Notifier notifier, QuestInfo info) {
		this.notifier = notifier;
		title = Game.assets.getDialog(info.titleDialog);
		nodes = new ArrayList<Node>(info.nodes.size());
		for (NodeInfo x : info.nodes)
			nodes.add(new Node(x));
	}
	
	public String getTitle() {
		return title;
	}
	
	public QuestState getState() {
		return state;
	}
	
	/**
	 * Advance the quest to the next node
	 */
	private void advance() {
		current++;
		notifier.addNotice(nodes.get(current).getText());
		if (current == nodes.size()-1)
			setState(QuestState.COMPLETED);
	}
	
	/**
	 * Start the quest
	 */
	public void start() {
		/* Display notices */
		notifier.addNotice(GlobalVar.ALERT_QUEST_NEW + ": " + title);
		notifier.addNotice(nodes.get(current).getText());
	}
	
	public void takeEvent(Event event) {
		nodes.get(current).takeEvent(event);
		if (nodes.get(current).isCompleted())
			advance();
	}
	
	public void setState(QuestState state) {
		this.state = state;
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum QuestState {
		
		PROGRESS,
		COMPLETED,
		FAILED;
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class QuestInfo {
		
		public final int titleDialog;
		public final List<NodeInfo> nodes;

		public QuestInfo(int titleDialog, List<NodeInfo> nodes) {
			this.titleDialog = titleDialog;
			this.nodes = nodes;
		}
		
	}
}
