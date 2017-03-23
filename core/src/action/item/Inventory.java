package action.item;

import java.util.ArrayList;
import java.util.List;

import action.core.Game;
import action.interfaces.GlobalVar;
import action.item.Item.ItemInfo;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Basic class to hold and manage items
 *
 * @author Andrea
 */
public class Inventory {
	
	/** Money */
	private int money;
	/** Item list */
	private List<Item> list = new ArrayList<Item>();
	/** Fake money item */
	private static Item moneyFake;
	
	/**
	 * Create an empty inventory
	 */
	public Inventory() {
		
	}
	
	public Inventory(InventoryInfo info) {
		for (Integer x : info.items) {
			ItemInfo i = (ItemInfo) Game.assets.getItemInfo(x);
			if (i != null)
				list.add(new Item(i));
		}
		
		moneyFake = new Item(Game.assets.getItemInfo(GlobalVar.FAKE_MONEY_ITEM_ID));
	}
	
	public List<Item> getList() {
		return list;
	}
	
	/**
	 * Add an item to the inventory
	 */
	public void addItem(Item item) {
		list.add(item);
	}
	
	/**
	 * Add a sum of money
	 */
	public void addMoney(int money) {
		this.money += money;
	}
	
	public int getMoney() {
		return money;
	}
	
	/**
	 * @return A fake item symbolizing money
	 */
	public static Item getMoneyItem() {
		return moneyFake;
	}
	
	
	public static class InventoryInfo {
		
		public final List<Integer> items;

		public InventoryInfo(List<Integer> items) {
			this.items = items;
		}
		
		public static InventoryInfo loadFromXml(Element element) {
	    	List<Integer> list = new ArrayList<Integer>(element.getChildCount());
	        for (int i = 0; i < element.getChildCount(); i++) {
	        	list.add(element.getChild(i).getIntAttribute("id"));
	        }
	    	return new InventoryInfo(list);
		}
		
	}
}
