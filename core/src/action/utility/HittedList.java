package action.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import action.entity.Entity;

/**
 * An utility to manage a list of entity hitted
 * 
 * @author Andrea
 */
public class HittedList {

    private List<HittedEntity> hitted = new ArrayList<HittedEntity>();
    /** How much time must pass before an entity is removed from the list (-1 infinite) */
    private float interval;
    
    /**
     * @param interval How much time must pass before an entity is removed from the list (-1 infinite)
     */
    public HittedList(float interval) {
    	this.interval = interval;
    }
    
    /**
     * HittedList that allows only one hit
     */
    public HittedList() {
    	this(-1);
    }
    
    public boolean contains(Entity x) {
    	for (HittedEntity h : hitted) 
    		if (h.is(x))
    			return true;
    	return false;
    }
    
    public void add(Entity x) {
    	hitted.add(new HittedEntity(x));
    }
    
    public void update(float delta) {
    	/* Don't update if interval is infinite */
    	if (interval == -1)
    		return;
    	for (Iterator<HittedEntity> iter = hitted.iterator(); iter.hasNext();) {
    		HittedEntity he = iter.next();
    		if (!he.update(delta))
    			iter.remove();
    	}
    }
    
    /**
     * Hold information for an entity that has been hitted
     * 
     * @author Andrea
     */
    public class HittedEntity {
    	
    	private Entity entity;
    	private float time = 0;
    	
    	public HittedEntity(Entity entity) {
    		this.entity = entity;
    	}
    	
    	/**
    	 * @return False if the this object has expired
    	 */
        public boolean update(float delta) {
        	time += delta;
        	return !(time >= interval);
        }
        
        public boolean is(Entity x) {
        	return (entity == x);
        }
    	
    }
    
}
