package action.world;

import action.world.subworld.SubWorld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

/**
 * A special camera that can't look outside the world
 *
 * @author Andrea
 */
public class BoundedCamera extends OrthographicCamera {

    private World world;
    
    /** Temporary var used for X component computations */
    private float cameraX;
    /** Temporary var used for Y component computations */
    private float cameraY;
    
	private float mapWidth;
	private float mapHeight;

    public void centerOn(float x, float y) {
    	switch(world.getMode()) {
    		case SINGLE_MAP:
    			centerOnSingle(x, y, viewportWidth, viewportHeight);
    			break;
    		case MULTI_MAP:
    			centerOnMulti(x, y, viewportWidth, viewportHeight);
    			break;
    	}
    	
    	/* Save the computed position */
    	position.x = MathUtils.round(cameraX + viewportWidth / 2);
    	position.y = MathUtils.round(cameraY + viewportHeight / 2);
    }
    
    public void centerOnSmooth(float x, float y) {
    	switch(world.getMode()) {
    		case SINGLE_MAP:
    			centerOnSingle(x, y, viewportWidth, viewportHeight);
    			break;
    		case MULTI_MAP:
    			centerOnMulti(x, y, viewportWidth, viewportHeight);
    			break;
    	}
    	
    	/* Save the computed position */
    	position.x = MathUtils.round(MathUtils.lerp(position.x, cameraX + viewportWidth / 2, 0.1f));
    	position.y = MathUtils.round(MathUtils.lerp(position.y, cameraY + viewportHeight / 2, 0.1f));
    }
   
    private void centerOnMulti(float x, float y, float w, float h) {
    	boolean outN = false, outS = false, outE = false, outW = false;
        float mapsize = World.MAP_SIZE_EFFECTIVE;
 	
        /* Try to set the given position as center of the camera by default */
        cameraX = x - w / 2;
        cameraY = y - h / 2;  

        /* Get in which square is the center of the camera */
        int squareX = SubWorld.pointToMapX(x);
        int squareY = SubWorld.pointToMapY(y);

    	/* Check if the camera is out of some border */
        if (cameraX < mapsize * squareX)
        	outW = true;
        if (cameraX + w > mapsize * (squareX+1))
        	outE = true;
        if (cameraY < mapsize * squareY)
        	outN = true;
        if (cameraY + h > mapsize * (squareY+1))
        	outS = true;
        
        /* Check normal borders */
        if (outN)
        	if (!world.checkViability(squareX, squareY-1))
        		cameraY = squareY * mapsize;

        if (outS)
        	if (!world.checkViability(squareX, squareY+1))
        		cameraY = (squareY+1) * mapsize - h;

        if (outW)
        	if (!world.checkViability(squareX-1, squareY))
        		cameraX = squareX * mapsize;
        
        if (outE)
        	if (!world.checkViability(squareX+1, squareY))
        		cameraX = (squareX+1) * mapsize - w;  
        
        /* Recompute out of borders */
        outN = false; outS = false; outE = false; outW = false;
        if (cameraX < mapsize * squareX)
        	outW = true;
        if (cameraX + w > mapsize * (squareX+1))
        	outE = true;
        if (cameraY < mapsize * squareY)
        	outN = true;
        if (cameraY + h > mapsize * (squareY+1))
        	outS = true;
        
        /* Check diagonals */
        if (outN) {
        	if (outW)
            	if (!world.checkViability(squareX-1, squareY-1))
            		cameraX = squareX * mapsize;
        	if (outE)
            	if (!world.checkViability(squareX+1, squareY-1))
            		cameraX = (squareX+1) * mapsize - w;  	
        }
        
        if (outS) {
        	if (outW)
            	if (!world.checkViability(squareX-1, squareY+1))
            		cameraX = squareX * mapsize;
        	if (outE)
            	if (!world.checkViability(squareX+1, squareY+1))
            		cameraX = (squareX+1) * mapsize - w;  	
        }
    }
    
    private void centerOnSingle(float x, float y, float w, float h) {
    	/* Try to set the given position as center of the camera by default */
    	cameraX = x - w / 2;
    	cameraY = y - h / 2;
	  
    	/* Prevent horizontal black screen */
    	if(cameraX < 0) 
    		cameraX = 0;
    	if(cameraX + w > mapWidth) 
    		cameraX = mapWidth - w;
	  
    	/* Prevent vertical black screen */
    	if(cameraY < 0)
    		cameraY = 0;
    	if(cameraY + h > mapHeight)
    		cameraY = mapHeight - h;
    }
    
    public void calibrate(World world) {
    	this.world = world;
    	this.mapWidth = world.getSingleMapWidth();
    	this.mapHeight = world.getSingleMapHeight();
    }

}