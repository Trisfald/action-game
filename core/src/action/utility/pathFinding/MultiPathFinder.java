package action.utility.pathFinding;

import java.util.List;
import java.util.concurrent.Callable;

import action.entity.Entity;
import action.entity.being.creature.Creature;
import action.interfaces.GlobalVar;
import action.utility.PoolableArray;
import action.utility.SynchronizedPool;
import action.utility.Vector2i;
import action.utility.pathFinding.PathFinder.Node;
import action.world.World;

import com.badlogic.gdx.utils.Pool;

/**
 * An utility to find a path based on a set of possible destination
 * @author Andrea
 */
public class MultiPathFinder implements Callable<Path> {
	
	private List<Vector2i> destinations;	
	private Creature owner;
	private World world;
	private PoolableArray<Entity> entities;
	private int maxPathLength;
	
	public static final Pool<Node[][]> NODE_ARRAY_POOL = new SynchronizedPool<Node[][]>(10) {
	    @Override
	    protected synchronized Node[][] newObject() {
	    	Node[][] nodes = new Node[GlobalVar.MAX_PATH_LENGTH*2+1][GlobalVar.MAX_PATH_LENGTH*2+1];
			for (int x=0;x<GlobalVar.MAX_PATH_LENGTH*2+1;x++)
				for (int y=0;y<GlobalVar.MAX_PATH_LENGTH*2+1;y++) 
					nodes[x][y] = new Node(x, y);
	        return nodes;
	    }
	};
	
	/**
	 * @param owner Entity moving
	 * @param world Game world
	 * @param entities The entities for collisions
	 * @param destinations All the valid destinations
	 * @param maxPathLength Max length of the path in nodes
	 */
	public MultiPathFinder(Creature owner, World world, PoolableArray<Entity> entities, List<Vector2i> destinations, int maxPathLength) {
		this.destinations = destinations;
		this.owner = owner;
		this.world = world;
		this.entities = entities;
		this.maxPathLength = maxPathLength;
	}

	@Override
	public Path call() throws Exception {
		Path path;
		int shiftCounter;

		PathFinder pathFinder = new PathFinder(world, owner, 
				World.getEntitiesArea(entities), maxPathLength, true);

		for (Vector2i v:destinations) {
			shiftCounter = world.getShiftCounter();
			/* Repeat the pathfinding if the world shifted during the computations */
			do {
				path = pathFinder.findPath(World.getCreatureNodeX(owner), World.getCreatureNodeY(owner), v.x, v.y);
			}
			while (shiftCounter != world.getShiftCounter());
			if (path != null) {
				pathFinder.free();
				entities.free();
				return path;
			}
		}
		pathFinder.free();
		entities.free();
		return null;
	}	
}
