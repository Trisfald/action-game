package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.ai.Ai.AiInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for AI
 *
 * @author Andrea
 */
public class AiDB extends Database {

	private Map<Integer, AiInfo> data = new HashMap<Integer, AiInfo>();
	
    public AiDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(element.getIntAttribute("id"), (AiInfo.loadFromXml(element)
            		));
        }
    }

    public AiInfo getElement(int key) {
    	return data.get(key);
    }
}
