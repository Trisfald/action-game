package action.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import action.entity.Entity;
import action.entity.Entity.ZorderComparator;
import action.utility.PoolableArray;
import action.utility.SynchronizedPool;

import com.badlogic.gdx.utils.Pool;

/**
 * @author Andrea
 */
public class EntitiesManager {

	private List<Entity> list = new ArrayList<Entity>();
	private List<Entity> buffer = new ArrayList<Entity>();
	private ZorderComparator zOrder = new ZorderComparator();
	private Pool<PoolableArray<Entity>> pool = new SynchronizedPool<PoolableArray<Entity>>() {
	    @Override
	    protected synchronized PoolableArray<Entity> newObject() {
	        return new PoolableArray<Entity>(pool);
	    }
	};
	private boolean lock;
	
	public void add(Entity e) {
		if (lock)
			buffer.add(e);
		else
			list.add(e);	
	}
	
	public void addAll(Collection<Entity> c) {
		if (lock)
			buffer.addAll(c);
		else
			list.addAll(c);	
	}
	
	public List<Entity> getAll() {
		return list;
	}
	
	public void lock() {
		lock = true;
	}
	
	public void unlock() {
		lock = false;
	}
	
	/**
	 * Insert the new entities in the right spot
	 */
	public void synchronize() {
		list.addAll(buffer);
		buffer.clear();
	}
	
	/**
	 * Sort the entities using Z-order
	 */
	public void sort() {
		Collections.sort(list, zOrder);
	}
	
    /**
     * @return A new list containing all entities around one entity in a given range
     */
    public PoolableArray<Entity> getEntities(Entity escluded, float range) {
    	PoolableArray<Entity> array = pool.obtain();
    	for (Entity x : list)
    		if (!x.equals(escluded) && x.computeDistance(escluded) <= range) {
    			array.add(x);
    		}
        return array;
    }
    
    /**
     * Removes all entities
     */
    public void clear() {
    	list.clear();
    	buffer.clear();
    }
	
}
