package action.entity;

import action.item.Item;

/**
 * Interface for entities from which the player can pick items
 * 
 * @author Andrea
 */
public interface Lootable {

	public void removeMoney(int money);
	
	public void removeItem(Item item);
	
}
