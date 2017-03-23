package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.entity.being.area.Explosion.ExplosionInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for explosions
 * 
 * @author Andrea
 */
public class ExplosionDB extends Database {

	private Map<Integer, ExplosionInfo> data = new HashMap<Integer, ExplosionInfo>();
	
    public ExplosionDB(FileHandle file) throws IOException {
        fillData(load(file));
    }

    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
            		ExplosionInfo.loadFromXml(element));
        }
    }
    
    public ExplosionInfo getElement(int key) {
    	return data.get(key);
    }
	
}
