package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for Dialogs
 *
 * @author Andrea
 */
public class DialogDB extends Database {
    
	private Map<Integer, String> data = new HashMap<Integer, String>();
	
    public DialogDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
                    element.getText());
        }
    }
    
    public String getElement(int key) {
    	return data.get(key);
    }
    
}
