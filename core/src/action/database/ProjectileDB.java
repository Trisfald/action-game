package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.entity.being.area.Projectile.ProjectileInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for projectiles
 * 
 * @author Andrea
 */
public class ProjectileDB extends Database {
	
	private Map<Integer, ProjectileInfo> data = new HashMap<Integer, ProjectileInfo>();
	
    public ProjectileDB(FileHandle file) throws IOException {
        fillData(load(file));
    }

    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            data.put(Integer.valueOf(element.getIntAttribute("id")), 
            		ProjectileInfo.loadFromXml(element));
        }
    }
    
    public ProjectileInfo getElement(int key) {
    	return data.get(key);
    }
    
}
