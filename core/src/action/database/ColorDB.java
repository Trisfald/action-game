package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for standard color definitions
 * 
 * @author Andrea
 */
public class ColorDB extends Database {
	
	private Map<String, Color> data = new HashMap<String, Color>();
	
    public ColorDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
        	element = root.getChild(i);
            data.put(element.getAttribute("id"), new Color(
            		element.getFloatAttribute("red"),
            		element.getFloatAttribute("green"),
            		element.getFloatAttribute("blue"),
            		element.getFloatAttribute("alpha")          		
            		));
        }
    }
    
    public Color getElement(String key) {
    	return data.get(key);
    }
}
