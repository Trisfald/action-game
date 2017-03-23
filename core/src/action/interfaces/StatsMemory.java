package action.interfaces;

import action.entity.being.creature.Stats.CreatureStatType;
import action.utility.Body.DefenceType;
import action.utility.Statistic;

/**
 * Interface for objects that can give stats info
 * 
 * @author Andrea
 */
public interface StatsMemory {

    public Statistic getStat(CreatureStatType type);
    
    public Statistic getStat(DefenceType type);
    
}