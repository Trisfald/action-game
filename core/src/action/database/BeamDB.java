package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.entity.being.area.Beam.BeamInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for beams
 * 
 * @author Andrea
 */
public class BeamDB extends Database {
	
	private Map<Integer, BeamInfo> data = new HashMap<Integer, BeamInfo>();
	
    public BeamDB(FileHandle file) throws IOException {
        fillData(load(file));
    }

    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
                    BeamInfo.loadFromXml(element));
        }
    }
    
    public BeamInfo getElement(int key) {
    	return data.get(key);
    }
    
}
