package action.utility.pathFinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import action.entity.Entity;
import action.utility.Vector2i;
import action.utility.enums.Direction;
import action.world.Collision;
import action.world.World;

import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * @author kevin
 * @author Andrea
 */
public class PathFinder implements PathFindingContext {

	/** The set of nodes that have been searched through */
	private ArrayList<Node> closed = new ArrayList<Node>();
	/** The set of nodes that we do not yet consider fully searched */
	private PriorityList open = new PriorityList();
	
	/** The maximum depth of search we're willing to accept before giving up */
	private int maxSearchDistance;
	
	/** The complete set of nodes across the map */
	private Node[][] nodes;
	/** True if we allow diaganol movement */
	private boolean allowDiagMovement;
	/** The heuristic we're applying to determine which nodes to search first */
	private AStarHeuristic heuristic;
	/** The node we're currently searching from */
	private Node current;
	
	/** The entity going through the path */
	private Entity mover;
	/** The x coordinate of the source tile we're moving from */
	private int sourceX;
	/** The y coordinate of the source tile we're moving from */
	private int sourceY;
	/** The distance searched so far */
	private int distance;
	
	private World world;
	/** Maximum number of nodes searched before giving up */
	private final int maxNodeSearched;
	/** Nodes actually searched */
	private int nodeSearched;
	
	/** The set of nodes occupied by entities */
	private Set<Vector2i> entitiesArea;
	
	/** Offset x that translates from node matrix to the world blocked matrix */
	private int offsetX;
	/** Offset y that translates from node matrix to the world blocked matrix */
	private int offsetY;
	/** Tells if the node matrix has initialized */
	private boolean initialized;
	/** Vector used for tests */
	private Vector2i tester = new Vector2i();
	
	
	/**
	 * Create a path finder with the default heuristic - closest to target.
	 * 
	 * @param entitiesArea The set of points occupied by entities
	 * @param maxSearchDistance The maximum depth we'll search before giving up
	 * @param allowDiagMovement True if the search should try diaganol movement
	 */
	public PathFinder(World world, Entity mover, Set<Vector2i> entitiesArea,
			int maxSearchDistance, boolean allowDiagMovement) {
		this(world, mover, entitiesArea, maxSearchDistance, allowDiagMovement, new ClosestHeuristic());
	}

	/**
	 * Create a path finder 
	 * 
	 * @param entitiesArea The set of points occupied by entities
	 * @param heuristic The heuristic used to determine the search order of the map
	 * @param maxSearchDistance The maximum depth we'll search before giving up
	 * @param allowDiagMovement True if the search should try diaganol movement
	 */
	public PathFinder(World world, Entity mover, Set<Vector2i> entitiesArea,
			int maxSearchDistance, boolean allowDiagMovement, AStarHeuristic heuristic) {
		
		this.heuristic = heuristic;
		this.world = world;
		this.maxNodeSearched = maxSearchDistance * 10;
		this.maxSearchDistance = maxSearchDistance;
		this.allowDiagMovement = allowDiagMovement;
		this.mover = mover;
		this.entitiesArea = entitiesArea;

	}
	
	/**
	 * Get the X coordinate of the node currently being evaluated
	 * 
	 * @return The X coordinate of the node currently being evaluated
	 */
	public int getCurrentX() {
		if (current == null) {
			return -1;
		}
		
		return current.x;
	}

	/**
	 * Get the Y coordinate of the node currently being evaluated
	 * 
	 * @return The Y coordinate of the node currently being evaluated
	 */
	public int getCurrentY() {
		if (current == null) {
			return -1;
		}
		
		return current.y;
	}
	
	/**
	 * Get the first element from the open list. This is the next
	 * one to be searched.
	 * 
	 * @return The first element in the open list
	 */
	protected Node getFirstInOpen() {
		return (Node) open.first();
	}
	
	/**
	 * Add a node to the open list
	 * 
	 * @param node The node to be added to the open list
	 */
	protected void addToOpen(Node node) {
		node.setOpen(true);
		open.add(node);
	}
	
	/**
	 * Check if a node is in the open list
	 * 
	 * @param node The node to check for
	 * @return True if the node given is in the open list
	 */
	protected boolean inOpenList(Node node) {
		return node.isOpen();
	}
	
	/**
	 * Remove a node from the open list
	 * 
	 * @param node The node to remove from the open list
	 */
	protected void removeFromOpen(Node node) {
		node.setOpen(false);
		open.remove(node);
	}
	
	/**
	 * Add a node to the closed list
	 * 
	 * @param node The node to add to the closed list
	 */
	protected void addToClosed(Node node) {
		node.setClosed(true);
		closed.add(node);
	}
	
	/**
	 * Check if the node supplied is in the closed list
	 * 
	 * @param node The node to search for
	 * @return True if the node specified is in the closed list
	 */
	protected boolean inClosedList(Node node) {
		return node.isClosed();
	}
	
	/**
	 * Remove a node from the closed list
	 * 
	 * @param node The node to remove from the closed list
	 */
	protected void removeFromClosed(Node node) {
		node.setClosed(false);
		closed.remove(node);
	}
	
	/**
	 * Get the cost to move through a given location
	 * 
	 * @param mover The entity that is being moved
	 * @param sx The x coordinate of the tile whose cost is being determined
	 * @param sy The y coordiante of the tile whose cost is being determined
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The cost of movement through the given tile
	 */
	public float getMovementCost(Entity mover, int sx, int sy, int tx, int ty) {
		this.mover = mover;
		this.sourceX = sx;
		this.sourceY = sy;
		
		return world.getCost(this, tx, ty);
	}

	/**
	 * Get the heuristic cost for the given location. This determines in which 
	 * order the locations are processed.
	 * 
	 * @param mover The entity that is being moved
	 * @param x The x coordinate of the tile whose cost is being determined
	 * @param y The y coordiante of the tile whose cost is being determined
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The heuristic cost assigned to the tile
	 */
	public float getHeuristicCost(Entity mover, int x, int y, int tx, int ty) {
		return heuristic.getCost(null, mover, x, y, tx, ty);
	}
	
	/**
	 * A list that sorts any element provided into the list
	 *
	 * @author kevin
	 */
	private class PriorityList {
		/** The list of elements */
		private List<Object> list = new LinkedList<Object>();
		
		/**
		 * Retrieve the first element from the list
		 *  
		 * @return The first element from the list
		 */
		public Object first() {
			return list.get(0);
		}
		
		/**
		 * Empty the list
		 */
		public void clear() {
			list.clear();
		}
		
		/**
		 * Add an element to the list - causes sorting
		 * 
		 * @param o The element to add
		 */
		@SuppressWarnings("unchecked")
		public void add(Object o) {
			// float the new entry 
			for (int i=0;i<list.size();i++) {
				if (((Comparable<Object>) list.get(i)).compareTo(o) > 0) {
					list.add(i, o);
					break;
				}
			}
			if (!list.contains(o)) {
				list.add(o);
			}
			//Collections.sort(list);
		}
		
		/**
		 * Remove an element from the list
		 * 
		 * @param o The element to remove
		 */
		public void remove(Object o) {
			list.remove(o);
		}
	
		/**
		 * Get the number of elements in the list
		 * 
		 * @return The number of element in the list
 		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * Check if an element is in the list
		 * 
		 * @param o The element to search for
		 * @return True if the element is in the list
		 */
		@SuppressWarnings("unused")
		public boolean contains(Object o) {
			return list.contains(o);
		}
		
		@Override
		public String toString() {
			String temp = "{";
			for (int i=0;i<size();i++) {
				temp += list.get(i).toString()+",";
			}
			temp += "}";
			
			return temp;
		}
	}
	
	/**
	 * A single node in the search graph
	 */
	public static class Node implements Comparable<Object>, Poolable {
		/** The x coordinate of the node */
		private int x;
		/** The y coordinate of the node */
		private int y;
		/** The path cost for this node */
		private float cost = 0;
		/** The parent of this node, how we reached it in the search */
		private Node parent;
		/** The heuristic cost of this node */
		private float heuristic;
		/** The search depth of this node */
		private int depth = 0;
		/** In the open list */
		private boolean open;
		/** In the closed list */
		private boolean closed;
		
		/**
		 * Create an empty node
		 */
		public Node() {

		}

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void init(int x, int y) {
			this.x = y;
			this.y = y;
		}
		
		/**
		 * Set the parent of this node
		 * 
		 * @param parent The parent node which lead us to this node
		 * @return The depth we have no reached in searching
		 */
		public int setParent(Node parent) {
			depth = parent.depth + 1;
			this.parent = parent;
			
			return depth;
		}
		
		/**
		 * @see Comparable#compareTo(Object)
		 */
		@Override
		public int compareTo(Object other) {
			Node o = (Node) other;
			
			float f = heuristic + cost;
			float of = o.heuristic + o.cost;
			
			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}
		
		/**
		 * Indicate whether the node is in the open list
		 * 
		 * @param open True if the node is in the open list
		 */
		public void setOpen(boolean open) {
			this.open = open;
		}
		
		/**
		 * Check if the node is in the open list
		 * 
		 * @return True if the node is in the open list
		 */
		public boolean isOpen() {
			return open;
		}
		
		/**
		 * Indicate whether the node is in the closed list
		 * 
		 * @param closed True if the node is in the closed list
		 */
		public void setClosed(boolean closed) {
			this.closed = closed;
		}
		
		/**
		 * Check if the node is in the closed list
		 * 
		 * @return True if the node is in the closed list
		 */
		public boolean isClosed() {
			return closed;
		}

		/**
		 * Reset the state of this node
		 */
		@Override
		public void reset() {
			closed = false;
			open = false;
			cost = 0;
			depth = 0;
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "[Node "+x+","+y+"]";
		}
		
	}

	@Override
	public Entity getMover() {
		return mover;
	}

	@Override
	public int getSearchDistance() {
		return distance;
	}

	@Override
	public int getSourceX() {
		return sourceX;
	}

	@Override
	public int getSourceY() {
		return sourceY;
	}
	
	private boolean isValidLocation(Entity mover, int sx, int sy, int x, int y) {	
		int r;
		boolean invalid = false;
    	Direction dir;
    	int xStart, xEnd, yStart, yEnd;
		
		/* Add offset */
		sx += offsetX;
		x += offsetX;
		sy += offsetY;
		y += offsetY;

		if ((!invalid) && ((sx != x) || (sy != y))) {
			this.mover = mover;
			this.sourceX = sx - offsetX;
			this.sourceY = sy - offsetY;	
			tester.x = x;
			tester.y = y;
	        invalid = world.blockedNode(x, y) || entitiesArea.contains(tester);
		}
        if (!invalid) {
        	r = Collision.getSquareRadius(mover, World.NODE_SIZE, false);
        	/* Nodes around the starting position must be free */
        	/* Old implementation
            Array<Vector2i> movingSquare = Collision.getMovingSquare(sx, sy, x-sx, y-sy, r);
            for (Vector2i p : movingSquare)
    			if (world.blockedNode(p.x, p.y) || entitiesArea.contains(p))
    				return false;
            */

        	dir = Direction.unitVectorToDir(x-sx, y-sy);
        	xStart =  sx-r;
        	xEnd = sx+r;
        	yStart =  sy-r;
        	yEnd = sy+r;
        	
        	/* No movement, empty collision list */
        	if (dir == Direction.CENTER)
        		return false;
        	
        	/* Straight movement */
        	if (!dir.isDiagonal()) {
    	    	switch (dir) {
    	    		case RIGHT:
    	    			xStart = xEnd;
    	    			break;
    	    		case LEFT:
    	    			xEnd = xStart;
    	    			break;
    	    		case DOWN:
    	    			yEnd = yStart;
    	    			break;
    	    		case UP:
    	    			yStart = yEnd;
    	    			break;
    				default:
    					break;
    	    	}
	        	/* Check the square */
	        	for (int px = xStart; px <= xEnd; px++)
	        		for (int py = yStart; py <= yEnd; py++) {
	        			tester.set(px, py);
	        			if (world.blockedNode(px, py) || entitiesArea.contains(tester)) return false;
	        		}
        	}
        	else {
        		/* Diagonal movement */
    	    	switch (dir) {
    	    		case UPPER_RIGHT:
    	    			for (int px = xStart; px <= xEnd; px++) {
    	        			tester.set(px, yEnd);
    	        			if (world.blockedNode(px, yEnd) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			for (int py = yStart; py < yEnd; py++) {
    	        			tester.set(xEnd, py);
    	        			if (world.blockedNode(xEnd, py) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			break;
    	    		case UPPER_LEFT:
    	    			for (int px = xStart; px <= xEnd; px++) {
    	        			tester.set(px, yEnd);
    	        			if (world.blockedNode(px, yEnd) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			for (int py = yStart; py < yEnd; py++) {
    	        			tester.set(xStart, py);
    	        			if (world.blockedNode(xStart, py) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			break;
    	    		case DOWN_RIGHT:
    	    			for (int px = xStart; px <= xEnd; px++) {
    	        			tester.set(px, yStart);
    	        			if (world.blockedNode(px, yStart) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			for (int py = yStart+1; py <= yEnd; py++) {
    	        			tester.set(xEnd, py);
    	        			if (world.blockedNode(xEnd, py) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			break;
    	    		case DOWN_LEFT:
    	    			for (int px = xStart; px <= xEnd; px++) {
    	        			tester.set(px, yStart);
    	        			if (world.blockedNode(px, yStart) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			for (int py = yStart+1; py <= yEnd; py++) {
    	        			tester.set(xStart, py);
    	        			if (world.blockedNode(xStart, py) || entitiesArea.contains(tester)) return false;
    	    			}
    	    			break;
    				default:
    					break;
    	    	}
        	}	
        }
	    return !invalid;
	}
	
	private void initialize() {
		nodes = MultiPathFinder.NODE_ARRAY_POOL.obtain();
		initialized = true;
	}

	/**
	 * @param sx The x coordinate of the start location
	 * @param sy - The y coordinate of the start location
	 * @param tx - The x coordinate of the target location
	 * @param ty - The y coordinate of the target location 
	 * @return A path or null of no path has been found
	 */
	public Path findPath(int sx, int sy, int tx, int ty) {
			
		/* Assign offset */
		this.offsetX = sx - maxSearchDistance;
		this.offsetY = sy - maxSearchDistance;
		this.sourceX = sx = maxSearchDistance;
		this.sourceY = sy = maxSearchDistance;
		tx = tx - offsetX;
		ty = ty - offsetY;	

		current = null;
		this.distance = 0;
		this.nodeSearched = 0;

		// easy first check, if the destination is blocked or too far, we can't get there
		tester.x = tx;
		tester.y = ty;
		if (world.blockedNode(tx + offsetX, ty + offsetY) || 
				entitiesArea.contains(tester) ||
				tx < 0 || 
				ty < 0 || 
				(tx > maxSearchDistance * 2 + 1) || 
				(ty > maxSearchDistance * 2 + 1)) {
			return null;
		}
		
		/* Create the node matrix or clean it (if it has been created previously) */
		if (!initialized) {
			initialize();
		}
		else {	
			for (int x=0;x<maxSearchDistance * 2 + 1;x++) {
				for (int y=0;y<maxSearchDistance * 2 + 1;y++) {
					nodes[x][y].reset();
				}
			}
		}
		
		// initial state for A*. The closed group is empty. Only the starting
		// tile is in the open list and it's cost is zero, i.e. we're already there
		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		addToOpen(nodes[sx][sy]);
		
		nodes[tx][ty].parent = null;
		
		// while we haven't found the goal and haven't exceeded our max search depth
		int maxDepth = 0;
		while ((maxDepth < maxSearchDistance) && (open.size() != 0) && (nodeSearched < maxNodeSearched)) {			
			// pull out the first node in our open list, this is determined to 
			// be the most likely to be the next step based on our heuristic
			int lx = sx;
			int ly = sy;
			if (current != null) {
				lx = current.x;
				ly = current.y;
			}
			
			current = getFirstInOpen();
			distance = current.depth;
			
			if (current == nodes[tx][ty]) {
				if (isValidLocation(mover,lx,ly,tx,ty)) {
					break;
				}
			}
			
			removeFromOpen(current);
			addToClosed(current);
			
			// search through all the neighbours of the current node evaluating
			// them as next steps
			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {
					// not a neighbour, its the current tile
					if ((x == 0) && (y == 0)) {
						continue;
					}
					
					// if we're not allowing diaganol movement then only 
					// one of x or y can be set
					if (!allowDiagMovement) {
						if ((x != 0) && (y != 0)) {
							continue;
						}
					}
					
					// determine the location of the neighbour and evaluate it
					int xp = x + current.x;
					int yp = y + current.y;
					
					if (isValidLocation(mover,current.x,current.y,xp,yp)) {

						// the cost to get to this node is cost the current plus the movement
						// cost to reach this node. Note that the heursitic value is only used
						// in the sorted open list
						float nextStepCost = current.cost + getMovementCost(mover, current.x, current.y, xp, yp);
						Node neighbour = nodes[xp][yp];
						
						// if the new cost we've determined for this node is lower than 
						// it has been previously makes sure the node hasn't been discarded. We've
						// determined that there might have been a better path to get to
						// this node so it needs to be re-evaluated
						if (nextStepCost < neighbour.cost) {
							if (inOpenList(neighbour)) {
								removeFromOpen(neighbour);
							}
							if (inClosedList(neighbour)) {
								removeFromClosed(neighbour);
							}
						}
						
						// if the node hasn't already been processed and discarded then
						// reset it's cost to our current cost and add it as a next possible
						// step (i.e. to the open list)
						if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
							neighbour.cost = nextStepCost;
							neighbour.heuristic = getHeuristicCost(mover, xp, yp, tx, ty);
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));
							addToOpen(neighbour);
						} 
					}
				}
			}
			nodeSearched++;
		}

		// since we've got an empty open list or we've run out of search 
		// there was no path. Just return null
		if (nodes[tx][ty].parent == null) {
			return null;
		}

		// At this point we've definitely found a path so we can uses the parent
		// references of the nodes to find out way from the target location back
		// to the start recording the nodes on the way.
		Path path = new Path();
		Node target = nodes[tx][ty];
		while (target != nodes[sx][sy]) {
			/* Add offset */
			path.prependStep(target.x + offsetX, target.y + offsetY);
			target = target.parent;
		}
		path.prependStep(sx + offsetX, sy + offsetY);
		
		// thats it, we have our path 
		return path;
	}
	
	/**
	 * Free pooled objects
	 */
	public void free() {
		/* Free nodes */
		if (initialized) {
			for (int x=0;x<nodes.length;x++) {
				for (int y=0;y<nodes[x].length;y++) {
					nodes[x][y].reset();
				}
			}
			MultiPathFinder.NODE_ARRAY_POOL.free(nodes);
		}
	}
	
}
