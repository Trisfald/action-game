package action.world.subworld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import action.core.Game;
import action.entity.Entity;
import action.interfaces.GlobalVar;
import action.utility.ExtraMath;
import action.utility.Vector2i;
import action.utility.enums.Direction;
import action.world.World;
import action.world.ambience.Light;
import action.world.ambience.weather.MeteoInfo;
import action.world.map.MapWrapper;
import action.world.map.Transition;
import action.world.subworld.ShiftLoader.ShiftInfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Manages multiple maps together and provide a single interface for them
 * 
 * @author Andrea
 */
public class SubWorld {

	private Map<Direction, MapWrapper> maps = new EnumMap<Direction, MapWrapper>(Direction.class);
	private SubWorldMode mode;
	/** Viability matrix for crossing maps */
	private boolean[][] viability;
	/** Ray used to check blocked field of view */
	private Ray ray = new Ray(new Vector3(), new Vector3());
	private Vector3 rayResult = new Vector3();
	/** Transitions present in the subworld */
	private List<Transition> transitions = new ArrayList<Transition>();
	/** List used to contain render requests */
	private List<RenderRequest> requests = new ArrayList<RenderRequest>(4);
	
	public static final Pool<RenderRequest> REQUESTS_POOL = new Pool<RenderRequest>() {
	    @Override
	    protected RenderRequest newObject() {
	        return new RenderRequest();
	    }
	};
	
	/** Size of the map matrix */
	public static final int MAP_MATRIX_SIZE = 3;
	/** True if it should interpolate the value of location darkness in multi map mode */
	private static final boolean MULTI_DARKNESS_INTER = true;
	
	public SubWorld(Map<Direction, MapWrapper> maps) {
		this.maps = maps;
		if (maps.size() == 1)
			mode = SubWorldMode.SINGLE_MAP;
		else {
			mode = SubWorldMode.MULTI_MAP;
			computeViability();
		}
		computeTransitions();
	}
	
	public Map<Direction, MapWrapper> getMaps() {
		return maps;
	}
	
	public boolean[][] getViability() {
		return viability;
	}
    
    public void computeViability() {
    	viability = new boolean[3][3];
    	
    	for (Direction x : maps.keySet())
    		viability[x.getMapOffsetXEff() / World.MAP_SIZE_EFFECTIVE][x.getMapOffsetYEff() / World.MAP_SIZE_EFFECTIVE] = true;
    }
    
    public SubWorldMode getMode() {
    	return mode;
    }
    
    public void dispose() {
    	for (MapWrapper map : maps.values())
    		map.dispose();
    }
    
    public void render(OrthographicCamera camera, List<Entity> entities, Collection<? extends Light> lights, SpriteBatch batch) {
    	renderLights(camera, lights, batch);
    	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	batch.setShader(Game.shader);
    	batch.begin();
    	World.FBO.getColorBufferTexture().bind(1);
    	World.LIGHT_TEXTURE.bind(0);
    	batch.end();
    	switch (mode) {
    		case SINGLE_MAP:
    			renderSingle(camera, entities, batch);
    			break;
    		case MULTI_MAP:
    			renderMulti(camera, entities, batch);
    			break;
    	}
    	batch.setShader(null);
    }

    public void renderSingle(OrthographicCamera camera, List<Entity> entities, SpriteBatch batch) {
    	getCentralMap().getRenderer().setView(camera);
    	getCentralMap().getRenderer().render(GlobalVar.TERRAIN_LAYERS);
    	renderEntities(camera, entities, batch);
    	getCentralMap().getRenderer().render(GlobalVar.ABOVE_LAYERS);
    }
    
    public void renderMulti(OrthographicCamera camera, List<Entity> entities, SpriteBatch batch) {
    	/* Get the info about what needs to be rendered */
    	computeRenderRequests(camera.position.x, camera.position.y, 
    			camera.viewportWidth, camera.viewportHeight);
    	
    	/* Render the terrain */
    	renderMultiLayers(camera, requests, GlobalVar.TERRAIN_LAYERS);
    	/* Render the entities */
		camera.update();
		renderEntities(camera, entities, batch);
		/* Render above */
		renderMultiLayers(camera, requests, GlobalVar.ABOVE_LAYERS);
		
		for (RenderRequest request : requests)
			REQUESTS_POOL.free(request);
    }
    
    /**
     * Render the given layers in the multi map mode
     */
    private void renderMultiLayers(OrthographicCamera camera, List<RenderRequest> requests, int[] layers) {
    	TiledMapRenderer renderer;
    	for (RenderRequest request : requests) {
    		renderer = maps.get(request.dir).getRenderer();
    		camera.translate(-request.dir.getMapOffsetXEff(), -request.dir.getMapOffsetYEff());
    		camera.update();
    		renderer.setView(camera);
    		renderer.render(layers);
    		camera.translate(+request.dir.getMapOffsetXEff(), +request.dir.getMapOffsetYEff());
    	}
    }
    
    public void renderEntities(OrthographicCamera camera, List<Entity> entities, SpriteBatch batch) {
    	batch.setProjectionMatrix(camera.combined);
    	batch.begin();
    	for (Entity entity : entities) {
    		if (entity.isOnCamera(camera))
    			entity.draw(batch);
    	}
    	batch.end();
    }
    
    private void renderLights(OrthographicCamera camera, Collection<? extends Light> lights, SpriteBatch batch) {
		World.FBO.begin();
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (Light light : lights) {
			if (!light.isOnCamera(camera))
				continue;
			batch.setColor(light.getColor());
			//batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			batch.draw(World.LIGHT_TEXTURE, light.getX()-light.getSize()/2, 
					light.getY()-light.getSize()/2, light.getSize(), light.getSize());
			batch.setColor(Color.WHITE);
			//batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		batch.end();
		World.FBO.end();
    }
    
    /**
     * Compute the current transitions
     */
	private void computeTransitions() {
		for (Entry<Direction, MapWrapper> x : maps.entrySet()) {
			for (Transition t : x.getValue().getTransitions())
				t.applyOffset(x.getKey());
			transitions.addAll(x.getValue().getTransitions());
		}
	}
	
	/**
	 * @return The cached transitions
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	public boolean blockedNode(int x, int y) {
    	switch (mode) {
    		case SINGLE_MAP:
    			return getCentralMap().blockedNode(x, y);
    		case MULTI_MAP:
    			return blockedNodeMulti(x, y);
			default:
    			return false;
    	}
	}

	public int getWidthInNodes() {
		return 3 * World.MAP_SIZE * World.NODE_FACTOR;
	}

	public int getHeightInNodes() {
		return 3 * World.MAP_SIZE * World.NODE_FACTOR;
	}
	
	public boolean blockedTile(int x, int y) {
    	switch (mode) {
    		case SINGLE_MAP:
    			return getCentralMap().blockedTile(x, y);
    		case MULTI_MAP:
    			return blockedTileMulti(x, y);
			default:
    			return false;
    	}
	}
	
	private boolean blockedTileMulti(int x, int y) {
		/* Get the map key by computing the coordinates of the map index to which this tile belongs */
		Direction dir = SubWorld.indexToMapDir((int) Math.floor(x / World.MAP_SIZE), (int) Math.floor(y / World.MAP_SIZE));
		if (dir == null || maps.get(dir) == null)
			return true;
		return maps.get(dir).blockedTile(x - (dir.getMapOffsetX() * World.MAP_SIZE), y - (dir.getMapOffsetY() * World.MAP_SIZE));
	}
	
	private boolean blockedNodeMulti(int x, int y) {
		/* Get the map key by computing the coordinates of the map index to which this node belongs */
		Direction dir = SubWorld.indexToMapDir((int) Math.floor(x / (World.MAP_SIZE * World.NODE_FACTOR)), 
				(int) Math.floor(y / (World.MAP_SIZE * World.NODE_FACTOR)));
		if (dir == null || maps.get(dir) == null)
			return true;
		return maps.get(dir).blockedNode(x - (dir.getMapOffsetX() * World.MAP_SIZE * World.NODE_FACTOR),
				y - (dir.getMapOffsetY() * World.MAP_SIZE * World.NODE_FACTOR));
	}
	
	private List<RenderRequest> computeRenderRequests(float cameraX, float cameraY, float w, float h) {
		requests.clear();
        float mapsize = World.MAP_SIZE_EFFECTIVE;
        RenderRequest request;
		
		/* Go from center coordinates to top left */
		cameraX -= w/2;
		cameraY -= h/2;
		
		if (cameraY < mapsize) {
			/* Check down left */
			if (cameraX < mapsize) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.DOWN_LEFT, 
						cameraX, 
						cameraY, 
						Math.min(w, mapsize - cameraX), 
						Math.min(h, mapsize - cameraY)
				);
				requests.add(request);
			}
				
			/* Check down */
			if ((cameraX + w > mapsize) && (cameraX < 2 * mapsize)) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.DOWN, 
						Math.max(mapsize, cameraX) - mapsize,
						cameraY, 
						Math.min(w, 2 * mapsize - cameraX), 
						Math.min(h, mapsize - cameraY)
				);
				requests.add(request);
			}
			/* Check down right */
			if (cameraX + w > 2 * mapsize) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.DOWN_RIGHT, 
						Math.max(2 * mapsize, cameraX) - 2 * mapsize,
						cameraY, 
						Math.min(w, 3 * mapsize - cameraX), 
						Math.min(h, mapsize - cameraY)
				);
				requests.add(request);
			}
		}
		
		if ((cameraY + h > mapsize) && (cameraY < 2 * mapsize)) {
			/* Check left */
			if (cameraX < mapsize) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.LEFT, 
						cameraX, 
						Math.max(mapsize, cameraY) - mapsize,
						Math.min(w, mapsize - cameraX), 
						Math.min(h, 2 * mapsize - cameraY)
				);
				requests.add(request);	
			}
			/* Check center */
			if ((cameraX + w > mapsize) && (cameraX < 2 * mapsize)) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.CENTER, 
						Math.max(mapsize, cameraX) - mapsize,
						Math.max(mapsize, cameraY) - mapsize,
						Math.min(w, 2 * mapsize - cameraX), 
						Math.min(h, 2 * mapsize - cameraY)
				);
				requests.add(request);	
			}
			/* Check right */
			if (cameraX + w > 2 * mapsize) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.RIGHT, 
						Math.max(2 * mapsize, cameraX) - 2 * mapsize,
						Math.max(mapsize, cameraY) - mapsize,
						Math.min(w, 3 * mapsize - cameraX), 
						Math.min(h, 2 * mapsize - cameraY)
				);
				requests.add(request);
			}
		}
		
		if (cameraY + h > 2 * mapsize) {
			/* Check upper left */
			if (cameraX < mapsize) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.UPPER_LEFT, 
						cameraX, 
						Math.max(2 * mapsize, cameraY) - 2 * mapsize,
						Math.min(w, mapsize - cameraX), 
						Math.min(h, 3 * mapsize - cameraY)
				);
				requests.add(request);
			}
			/* Check up */
			if ((cameraX + w > mapsize) && (cameraX < 2* mapsize)) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.UP, 
						Math.max(mapsize, cameraX) - mapsize,
						Math.max(2 * mapsize, cameraY) - 2 * mapsize,
						Math.min(w, 2 * mapsize - cameraX), 
						Math.min(h, 3 * mapsize - cameraY)
				);
				requests.add(request);
			}
			/* Check upper right */
			if (cameraX + w > 2 * mapsize) {
				request = REQUESTS_POOL.obtain();
				request.init(
						Direction.UPPER_RIGHT, 
						Math.max(2 * mapsize, cameraX) - 2 * mapsize,
						Math.max(2 * mapsize, cameraY) - 2 * mapsize,
						Math.min(w, 3 * mapsize - cameraX), 
						Math.min(h, 3 * mapsize - cameraY)
				);
				requests.add(request);
			}
		}
		
		return requests;
	}
	
	/**
	 * @return The effective width of the central map
	 */
	public float getSingleMapWidth() {
		return getCentralMap().getWidth() * World.TILE_SIZE;
	}
	
	/**
	 * @return The effective height of the central map
	 */
	public float getSingleMapHeight() {
		return getCentralMap().getHeight() * World.TILE_SIZE;
	}
	
	/**
	 * @return The current central map (multi-mode) or the current map (single-mode)
	 */
	public MapWrapper getCentralMap() {
		return maps.get(Direction.CENTER);
	}
	
	/**
	 * @return The map containing the given point
	 */
	public MapWrapper getMapContaining(float x, float y) {
		switch (mode) {
			case MULTI_MAP:
				return maps.get(indexToMapDir((int) x / World.MAP_SIZE_EFFECTIVE, (int) y / World.MAP_SIZE_EFFECTIVE));
			case SINGLE_MAP:
				return maps.get(Direction.CENTER);
		}
		return null;
	}
	
	public boolean checkViability(int x, int y) {
		if (x >= SubWorld.MAP_MATRIX_SIZE || x < 0 || y >= SubWorld.MAP_MATRIX_SIZE || y < 0)
			return false;
		return viability[x][y];
	}
	
	/**
	 * @return The x coord of the center of the world
	 */
	public float getCenterWorldX() {
		return (float) World.MAP_SIZE_EFFECTIVE * 1.5f;
	}
	
	/**
	 * @return The y coord of the center of the world
	 */
	public float getCenterWorldY() {
		return (float) World.MAP_SIZE_EFFECTIVE * 1.5f;
	}
	
	public void takeShift(ShiftInfo info) {
		maps = info.maps;
		computeViability();
		computeTransitions();
		/* Dispose maps not used anymore */
		for (MapWrapper map : info.toRemove)
			map.dispose();
	}
	
	/**
	 * @param origin Point to get the illumination at
	 * @return The current illumination
	 */
	public Color getIllumination(Vector2 origin) {
    	switch (mode) {
    		case SINGLE_MAP:
    			return getCentralMap().getIllumination();
    		case MULTI_MAP:
    			if (!MULTI_DARKNESS_INTER)
    				return getCentralMap().getIllumination();
    			return multiIllumination(origin);
			default:
    			return new Color(0, 0, 0, 0);
    	}
	}
	
	private Color multiIllumination(Vector2 origin) {	
		float distance;	
		float sumDistances = 0;
		float sumA = 0, sumR = 0, sumG = 0, sumB = 0;

		for (Entry<Direction, MapWrapper> x : maps.entrySet()) {
			/* Compute the inverse of the distance between the player and the center of the map */
			distance = ExtraMath.inverse(origin.dst(x.getKey().getMapOffsetXEff() + World.MAP_SIZE_EFFECTIVE / 2, 
					x.getKey().getMapOffsetYEff() + World.MAP_SIZE_EFFECTIVE / 2));
			sumDistances += distance;
			/* Multiply the distance for the value of color compenent of that map's illumination */
			sumR += distance * x.getValue().getIllumination().r;
			sumG += distance * x.getValue().getIllumination().g;
			sumB += distance * x.getValue().getIllumination().b;
			sumA += distance * x.getValue().getIllumination().a;
		}
		
		return new Color(sumR / sumDistances, sumG / sumDistances, sumB / sumDistances, sumA / sumDistances);
	}
	
	public MeteoInfo getWeatherInfo() {
		return maps.get(Direction.CENTER).getMeteoInfo();
	}

	/**
	 * @return True if the line going from start to end does not encounter blocked tiles
	 */
	public boolean clearLine(float sx, float sy, float tx, float ty) {
		float distance = Vector2.dst(sx, sy, tx, ty);
		/* Use the rayResult vector to store the direction vector */
		rayResult.set(tx - sx, ty - sy, 0).nor();
		ray.set(sx, sy, 0, rayResult.x, rayResult.y, 0);
		/* Check the ray end point at progressive intervals */
		for (float f = 0; f < distance; f += GlobalVar.FOV_RAY_DISTANCE_INTERVAL) {
			ray.getEndPoint(rayResult, f);
			/* If the ray passes through a blocked tile the line is not clear */
			if (blockedTile(World.absToTileX(rayResult.x), World.absToTileY(rayResult.y)))
				return false;
		}
		
		return true;
	}
   
	
	/**
	 * Converts map indexes to the direction in the maps window
	 */
	public static Direction indexToMapDir(int x, int y) {
		if (x == 0) {
			if (y == 0) 
				return Direction.DOWN_LEFT;
			else if (y == 1)
				return Direction.LEFT;
			else if (y == 2)
				return Direction.UPPER_LEFT;
		}
		else if (x == 1) {
			if (y == 0) 
				return Direction.DOWN;
			else if (y == 1)
				return Direction.CENTER;
			else if (y == 2)
				return Direction.UP;	
		}
		else if (x == 2) {
			if (y == 0)
				return Direction.DOWN_RIGHT;
			else if (y == 1)
				return Direction.RIGHT;
			else if (y == 2)
				return Direction.UPPER_RIGHT;
		}	
		return null;
	}
	
	/**
	 * Converts map indexes to the direction in the maps window
	 */
	public static Direction indexToMapDir(Vector2i coords) {
		return indexToMapDir(coords.x, coords.y);
	}
	
	/**
	 * @return The X coordinate of the map index to which this point belongs
	 */
	public static int pointToMapX(float x) {
        return (int) Math.min(Math.floor(x / (float) World.MAP_SIZE_EFFECTIVE), (float) SubWorld.MAP_MATRIX_SIZE-1);
	}
	
	/**
	 * @return The Y coordinate of the map index to which this point belongs
	 */
	public static int pointToMapY(float y) {
        return (int) Math.min(Math.floor(y / (float) World.MAP_SIZE_EFFECTIVE), (float) SubWorld.MAP_MATRIX_SIZE-1);
	}
	
	
	
    /**
     * Class that contains information about a sub world
     * 
     * @author Andrea
     */
    public static class SubWorldInfo {
    	
    	public final Map<Direction, MapWrapper> maps;
    	public final List<Entity> entities;
    	public final List<Light> lights;
    	public final int playerX;
    	public final int playerY;
    
		public SubWorldInfo(Map<Direction, MapWrapper> maps, List<Entity> entities, List<Light> lights,
				int playerX, int playerY) {
			this.maps = maps;
			this.entities = entities;
			this.lights = lights;
			this.playerX = playerX;
			this.playerY = playerY;
		} 	
		
    }
	
	
	/**
	 * Info for rendering a portion of a map
	 * 
	 * @author Andrea
	 */
	public static class RenderRequest implements Poolable {
		
		/** Identify the map we want to draw */
		public Direction dir;
		/** Top left point x */
		public float x;
		/** Top left point y */
		public float y;
		public float width;
		public float height;
		
		public RenderRequest(Direction dir, float x, float y, float width, float height) {
			this.dir = dir;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		/**
		 * Empty request
		 */
		public RenderRequest() {
			
		}

		public void init(Direction dir, float x, float y, float width, float height) {
			this.dir = dir;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public void reset() {
			
		}
		
	}
	
	    
    /**
     * @author Andrea
     */
    public enum SubWorldMode {
    	
    	SINGLE_MAP,
    	MULTI_MAP;
    	
    }

}
