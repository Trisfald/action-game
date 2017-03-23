package action.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import action.diary.Node.NodeInfo;
import action.diary.Quest.QuestInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class QuestDB extends Database {

	private Map<Integer, QuestInfo> data = new HashMap<Integer, QuestInfo>();
	
    public QuestDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
        	element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), (new QuestInfo(
            		element.getIntAttribute("title"),
            		loadNodes(element.getChildByName("nodes"))     		
            		)));
        }
    }

	public QuestInfo getElement(int key) {
    	return data.get(key);
    }
    
	private List<NodeInfo> loadNodes(Element element) {
		List<NodeInfo> list = new ArrayList<NodeInfo>();
		
		for (int i = 0; i < element.getChildCount(); i++)
			list.add(NodeInfo.loadFromXml(element.getChild(i)));
		
		return list;
	}
	
}
