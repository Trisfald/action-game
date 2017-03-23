package action.world;

/**
 * Class for factions of entities
 *
 * @author Andrea
 */
public class Faction {
	
    private final int id;
    /** Indicates the status of the relationships towards other factions (100 worst, 0 best) */
    private int[] standing;
    
    private static final float AGGRESSIVE_THRESHOLD = 50;
    
    public Faction (int id, int[] standing) {
        this.id = id;
        this.standing = standing;
    }
    
    public int getId() {
        return id;
    }
    
    public int getStanding(int index) {
        return standing[index];
    }
    
    public void setStanding(int index, int value) {
        standing[index] = value;
    }
    
    public void updateStanding(int index, int delta) {
        standing[index] += delta;
    }
    
    public boolean isAggresiveTo(Faction faction) {
    	if (this.getStanding(faction.getId()) >= AGGRESSIVE_THRESHOLD)
    		return true;
		return false;
    }
}
