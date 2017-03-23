package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.entity.being.creature.Creature.CreatureInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for creatures
 * 
 * @author Andrea
 */
public class CreatureDB extends Database {

	private Map<Integer, CreatureInfo> data = new HashMap<Integer, CreatureInfo>();
	
    public CreatureDB(FileHandle file) throws IOException {
        fillData(load(file));
    }

    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
            		CreatureInfo.loadFromXml(element));
        }
    }
    
	public CreatureInfo getElement(int key) {
    	return data.get(key);
    }
	
}
