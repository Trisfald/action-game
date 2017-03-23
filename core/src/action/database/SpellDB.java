package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.spell.Spell;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * @author Andrea
 */
public class SpellDB extends Database {

	private Map<Integer, Spell> data = new HashMap<Integer, Spell>();
	
    public SpellDB(FileHandle file) throws IOException {
        fillData(load(file));
    }

    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
            		Spell.loadFromXml(element));
        }
    }
    
	public Spell getElement(int key) {
    	return data.get(key);
    }
	
}
