package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

import action.world.Faction;

/**
 * Database for Factions
 *
 * @author Andrea
 */
public class FactionDB extends Database {
            
	private Map<Integer, Faction> data = new HashMap<Integer, Faction>();
	
    public FactionDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element, child;
        int j;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            int id = element.getIntAttribute("id");
            int[] standing = new int[element.getChildCount()];
            for (j = 0; j < element.getChildCount(); j++) {
                child = element.getChild(j);
                standing[j] = child.getIntAttribute("value"); 
            }
            data.put(id, new Faction(id, standing));
        }
    }
    
    public Faction getElement(int key) {
    	return data.get(key);
    }
}
