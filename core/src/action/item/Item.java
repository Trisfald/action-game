package action.item;

import action.core.Game;
import action.item.Protection.ProtectionInfo;
import action.item.shield.Shield;
import action.item.shield.Shield.ShieldInfo;
import action.item.weapon.Weapon;
import action.item.weapon.Weapon.WeaponInfo;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Basic class for defining items
 *
 * @author Andrea
 */
public class Item {
    
	private ItemType type;
	private String name;
	private String descr;
	private float weight;
	
	public Item(ItemInfo info) {
		this.type = info.type;
		this.name = Game.assets.getDialog(info.name);
		this.descr = Game.assets.getDialog(info.descr);
		this.weight = info.weight;
	}
	
	public ItemType getItemType() {
		return type;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescr() {
		return descr;
	}
	
	
	/**
	 * Info class for Item
	 *
	 * @author Andrea
	 */
	public static class ItemInfo {
		
		public final ItemType type;
		public final int name;
		public final int descr;
		public final float weight;
		
		public ItemInfo(ItemType type, int name, int descr, float weight) {
			this.type = type;
			this.name = name;
			this.descr = descr;
			this.weight = weight;
		}
		
		/**
		 * Copy constructor
		 */
		public ItemInfo(ItemInfo info) {
			this.type = info.type;
			this.name = info.name;
			this.descr = info.descr;
			this.weight = info.weight;
		}
		
		public static ItemInfo loadFromXml(Element element) {
			return new ItemInfo(
					ItemType.valueOf(element.getAttribute("type")),
					element.getIntAttribute("name"),
					element.getIntAttribute("descr"),
					element.getFloatAttribute("weight", 0)
					);
		}
	}
	
	/**
	 * Class to load/spawn a general item
	 *
	 * @author Andrea
	 */
	public static class ItemLoader {
		
		/** Id of the item */
		public final int id;
		/** Probability that the item will spawn */
		public final float probability;
		
		public ItemLoader(int id, float probability) {
			this.id = id;
			this.probability = probability;
		}

		/**
		 * Instantiates and returns the appropriate item
		 */
		public static Item loadItem(ItemLoader loader) {
			
			ItemInfo info = Game.assets.getItemInfo(loader.id);
			
			switch (info.type) {
				case STANDARD:
					return new Item((ItemInfo) info);
				case PROTECTION:
					return new Protection((ProtectionInfo) info);
				case WEAPON:
					return new Weapon((WeaponInfo) info);
				case SHIELD:
					return new Shield((ShieldInfo) info);
				default:
					return null;
			}
		}
		
	}
		
	
	/**
	 * Enumeration of all possible item types
	 *
	 * @author Andrea
	 */
	public enum ItemType {
		
		STANDARD,
		PROTECTION,
		WEAPON,
		SHIELD;
		
	}

}
