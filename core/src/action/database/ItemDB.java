package action.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import action.item.Item.ItemInfo;
import action.item.Item.ItemType;
import action.item.Protection.ProtectionInfo;
import action.item.shield.Shield.ShieldInfo;
import action.item.weapon.Weapon.WeaponInfo;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Database for Items
 *
 * @author Andrea
 */
public class ItemDB extends Database {
	
	private Map<Integer, ItemInfo> data = new HashMap<Integer, ItemInfo>();
	
    public ItemDB(FileHandle file) throws IOException {
        fillData(load(file));
    }
    
    @Override
    protected void fillData(Element root) {
        Element element;
        for (int i = 0; i < root.getChildCount(); i++) {
            element = root.getChild(i);
            switch (ItemType.valueOf(element.getAttribute("type"))) {
            	case STANDARD:
            		data.put(Integer.valueOf(element.getIntAttribute("id")), ItemInfo.loadFromXml(element));
    				break;
            	case PROTECTION:
            		data.put(Integer.valueOf(element.getIntAttribute("id")), ProtectionInfo.loadFromXml(element));
            		break;
            	case WEAPON:
            		data.put(Integer.valueOf(element.getIntAttribute("id")), WeaponInfo.loadFromXml(element));
            		break;
            	case SHIELD:
            		data.put(Integer.valueOf(element.getIntAttribute("id")), ShieldInfo.loadFromXml(element));
            		break;
            }
        }
    }
    
    public ItemInfo getElement(int key) {
    	return data.get(key);
    }

}
