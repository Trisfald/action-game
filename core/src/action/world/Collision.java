package action.world;

import java.util.List;

import action.entity.Entity;
import action.utility.Vector2i;
import action.utility.enums.Direction;
import action.utility.geom.Shape;
import action.world.subworld.SubWorld;
import action.world.subworld.SubWorld.SubWorldMode;

import com.badlogic.gdx.utils.Array;

/**
 * Manage various types of collision on the Tiled Map
 * 
 * @author Andrea
 */
public class Collision {

    private Array<Vector2i> collidingTiles = new Array<Vector2i>();
    private Array<Entity> collidingEntities = new Array<Entity>();
    private boolean outOfMap = false;
    private boolean canMoveTiles = true;
    private boolean canMoveEntities = true;
    
    /** The minimum distance between two entities to enable the collision check */
    private static final float MIN_COLL_DISTANCE = 500;
    /** Tells if the check for out of map borders is enabled */
    private static final boolean CHECK_OUT_MAP = true;
    
    /**
     * Set up a collision check for a general movement on the map
     * @param fullCheck True if it's using full shapes instead of perspective ones
     */
    public Collision(Entity main, float dx, float dy, World world, boolean fullCheck) {
        tileCollision(main, dx, dy, null, world, fullCheck);
        entityCollision(main, dx, dy, world.getEntities(), fullCheck);
    }
    
    /**
     * Set up a collision check for a beam movement on the map
     */
    public Collision(Entity main, Direction dir, World world) {
        tileCollision(main, 0, 0, dir, world, true);
        entityCollision(main, 0, 0, world.getEntities(), true);
    }
    
    /**
     * Set up a collision check for a standard movement on the map
     */
    public Collision(Entity main, float dx, float dy, World world) {
    	this(main, dx, dy, world, false);
    }
    
    /**
     * Set up a simple collision check between shapes
     */
    public Collision (Shape shape, List<Entity> list) {
        shapeCollision(shape, list);
    }
    
    
    /**
     * Check if an entity collide with tiles
     * 
     * @param main Entity
     * @param dx Displacement in X
     * @param dy Displacement in Y
     * @param Direction Orientation of the entity
     * @param world The game world
     * @param fullCheck True if it's using full shapes instead of perspective ones
     */
    private void tileCollision(Entity main, float dx, float dy, Direction dir, World world, boolean fullCheck)  {
    	Shape shape;
    	/* Destination coordinates */
        float dstX = main.getX()+dx;
        float dstY = main.getY()+dy;
        /* Start Tile coordinates */
        int sx = World.getCreatureTileX(main);
        int sy = World.getCreatureTileY(main);   
        
        /* Size of the square of tiles to check */
        int r = Collision.getSquareRadius(main, World.TILE_SIZE, fullCheck);
        
        /* Check out of map */
        if (CHECK_OUT_MAP) {
	        if (world.getMode() == SubWorldMode.SINGLE_MAP) {
		        if ((dstX + main.getWidth() > world.getSingleMapWidth() * World.TILE_SIZE) || 
		        		(dstX < 0) || 
		        		(dstY+main.getHeight() > world.getSingleMapHeight()* World.TILE_SIZE) || 
		        		(dstY < 0))
		            outOfMap = true;     
	        }
	        else
	        	outOfMap = this.checkOutBorder(world, main, dstX, dstY);
        }  
        
        if (fullCheck)
        	shape = main.getShape(dx, dy);
        else
        	shape = main.getPerspectiveShape(dx, dy);
        
        /* Check that the path is not blocked */    
        Array<Vector2i> movingSquare = Collision.getMovingSquare(sx, sy, dx, dy, dir, r);
        for (Vector2i p : movingSquare) {
            if (world.blockedTile(p.x, p.y)) {
                World.TILE_SHAPE.setLocation(p.x * World.TILE_SIZE, p.y * World.TILE_SIZE);   
                if (collide(shape, World.TILE_SHAPE)) {
                    collidingTiles.add(new Vector2i(p.x, p.y));
                    canMoveTiles = false;
                }
            }
        }
    }
    
    /**
     * Check if an entity collide with other entities
     * 
     * @param main Entity
     * @param dx Displacement in X
     * @param dy Displacement in Y
     * @param list All entities
     * @param fullCheck True if it's using full shapes instead of perspective ones
     */
    private void entityCollision(Entity main, float dx, float dy, List<Entity> list, boolean fullCheck) {  
    	Shape shape, target;
    	
        if (fullCheck)
        	shape = main.getShape(dx, dy);
        else
        	shape = main.getPerspectiveShape(dx, dy);  
        
        for (int i = 0; i < list.size(); i++) {
        	/* Must esclude itself in the check */
            if (i != list.indexOf(main)) {
                if (main.computeDistance(list.get(i)) < MIN_COLL_DISTANCE) {          	
                	/* Get the appropriate target shape */
                	if (fullCheck)
                		target = list.get(i).getShape();
                	else
                		target = list.get(i).getPerspectiveShape();
                	/* See if the two shapes collides */
                    if(collide(shape, target)) {
                        collidingEntities.add(list.get(i));
                        if (list.get(i).isBlockingMov())
                        	canMoveEntities = false;
                    }
                }
            }
        }
    }
    
    private void shapeCollision(Shape shape, List<Entity> list) {
        int i;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).computeDistance(shape) < MIN_COLL_DISTANCE) {
                if(collide(shape, list.get(i).getPerspectiveShape())) 
                    collidingEntities.add(list.get(i));
            }
        }        
    }
    
    /**
     * Tells if movement is possible
     * @param goThrough True if the movement ignore entities and goes through them
     */
    public boolean canMove(boolean goThrough) {
    	return (!outOfMap && canMoveTiles && (canMoveEntities || goThrough));
    }
    
    public static boolean collide(Shape shape1, Shape shape2) {
        return (shape1.intersects(shape2) || shape1.contains(shape2) || shape2.contains(shape1));
    }

    public Array<Entity> getCollidingEntities() {
        return collidingEntities;
    }
    
    public Array<Vector2i> getCollidingTiles() {
    	return collidingTiles;
    }
    
    /**
     * Checks if the given point is out of the walkable maps borders (multi mode only)
     */
    private boolean checkOutBorder(World world, Entity main, float dstX, float dstY) {
    	boolean outN = false, outS = false, outE = false, outW = false;
    	
    	/* Get map indexes */
    	int mapIndexX = SubWorld.pointToMapX(main.getX());
    	int mapIndexY = SubWorld.pointToMapY(main.getY());
    	
    	/* Check if it's out of some border */
        if (dstX < World.MAP_SIZE_EFFECTIVE * mapIndexX)
        	outW = true;
        if (dstX + main.getWidth() > World.MAP_SIZE_EFFECTIVE * (mapIndexX+1))
        	outE = true;
        if (dstY <= World.MAP_SIZE_EFFECTIVE * mapIndexY)
        	outN = true;
        if (dstY + main.getHeight() > World.MAP_SIZE_EFFECTIVE * (mapIndexY+1))
        	outS = true;

        /* Check nord border */
        if (outN) {
        	if (!world.checkViability(mapIndexX, mapIndexY-1))
        		return true;
        	
        	if (outW)
            	if (!world.checkViability(mapIndexX-1, mapIndexY-1))
            		return true;
        	
        	if (outE)
            	if (!world.checkViability(mapIndexX+1, mapIndexY-1))
            		return true;	
        }
        
        /* Check sud border */
        if (outS) {
        	if (!world.checkViability(mapIndexX, mapIndexY+1))
        		return true;
        	
        	if (outW)
            	if (!world.checkViability(mapIndexX-1, mapIndexY+1))
            		return true;
        	
        	if (outE)
            	if (!world.checkViability(mapIndexX+1, mapIndexY+1))
            		return true;
        }
    	
        /* Check remaining */
        if (outW)
        	if (!world.checkViability(mapIndexX-1, mapIndexY))
        		return true;
        
        if (outE)
        	if (!world.checkViability(mapIndexX+1, mapIndexY))
        		return true;
        
    	return false;
    }
    
    /**
     * @return A list of all the point that must be free to allow movement
     */
    public static Array<Vector2i> getMovingSquare(int sx, int sy, float dx, float dy, int radius) {
    	return getMovingSquare(sx, sy, Direction.unitVectorToDir(dx, dy), radius);
    }
    
    /**
     * @return A list of all the point that must be free to allow movement
     */
    public static Array<Vector2i> getMovingSquare(int sx, int sy, float dx, float dy, Direction dir, int radius) {
    	if (dir == null)
    		return getMovingSquare(sx, sy, Direction.unitVectorToDir(dx, dy), radius);
    	else
    		return getMovingSquare(sx, sy, dir, radius);
    }
    
    /**
     * @return A list of all the point that must be free to allow movement
     */
    public static Array<Vector2i> getMovingSquare(int sx, int sy, Direction dir, int radius) {
    	Array<Vector2i> list = new Array<Vector2i>();
    	int xStart =  sx-radius;
    	int xEnd = sx+radius;
    	int yStart =  sy-radius;
    	int yEnd = sy+radius;
    	
    	/* No movement, empty collision list */
    	if (dir == Direction.CENTER)
    		return list;
    	
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
	    		for (int py = yStart; py <= yEnd; py++)
	    			list.add(new Vector2i(px, py));

	    }
    	else {
    		/* Diagonal movement */
	    	switch (dir) {
	    		case UPPER_RIGHT:
	    			for (int px = xStart; px <= xEnd; px++)
	    				list.add(new Vector2i(px, yEnd));
	    			for (int py = yStart; py < yEnd; py++)
	    				list.add(new Vector2i(xEnd, py));
	    			break;
	    		case UPPER_LEFT:
	    			for (int px = xStart; px <= xEnd; px++)
	    				list.add(new Vector2i(px, yEnd));
	    			for (int py = yStart; py < yEnd; py++)
	    				list.add(new Vector2i(xStart, py));
	    			break;
	    		case DOWN_RIGHT:
	    			for (int px = xStart; px <= xEnd; px++)
	    				list.add(new Vector2i(px, yStart));
	    			for (int py = yStart+1; py <= yEnd; py++)
	    				list.add(new Vector2i(xEnd, py));
	    			break;
	    		case DOWN_LEFT:
	    			for (int px = xStart; px <= xEnd; px++)
	    				list.add(new Vector2i(px, yStart));
	    			for (int py = yStart+1; py <= yEnd; py++)
	    				list.add(new Vector2i(xStart, py));
	    			break;
				default:
					break;
	    	}
    	}
    	return list;
    }
    
    
    /**
     * @param entity
     * @param squareSize The width of the square unit
     * @param fullCheck True if it's using full shapes instead of perspective ones
     * @return The 'radius' (in multiples of squareSize) of the square that fully contains entity on the tiledmap
     */
    public static int getSquareRadius(Entity entity, int squareSize, boolean fullCheck) {
    	if (fullCheck)
    		return (int) Math.ceil(entity.getShape().getBoundingCircleRadius()/ squareSize);
    	else
    		return (int) Math.ceil(entity.getPerspectiveShape().getBoundingCircleRadius()/ squareSize);
    }
    
}
