package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.animation.Motion.MotionLoader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for Motions
 *
 * @author Andrea
 */
public class MotionDB extends Database {

	private Map<Integer, MotionLoader> data = new HashMap<Integer, MotionLoader>();
	
    public MotionDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
                    MotionLoader.loadFromXml(element));
        }
    }
    
    public MotionLoader getElement(int key) {
    	return data.get(key);
    }
    
}
