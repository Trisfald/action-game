package action.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import action.core.Game;
import action.entity.Entity;
import action.entity.being.Player;
import action.entity.being.creature.action.Action.ActionType;
import action.event.Event;
import action.event.MapEvent;
import action.hitbox.DangerZone;
import action.interfaces.GlobalVar;
import action.interfaces.Mover;
import action.spawner.Spawner;
import action.ui.GameUI;
import action.utility.ExtraMath;
import action.utility.PoolableArray;
import action.utility.Vector2i;
import action.utility.geom.Rectangle;
import action.utility.pathFinding.PathFindingContext;
import action.world.ambience.Ambience;
import action.world.ambience.Light;
import action.world.ambience.weather.MeteoInfo;
import action.world.map.Transition;
import action.world.subworld.ShiftLoader;
import action.world.subworld.ShiftLoader.ShiftInfo;
import action.world.subworld.SubWorld;
import action.world.subworld.SubWorld.SubWorldInfo;
import action.world.subworld.SubWorld.SubWorldMode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/**
 * Class to manage the game world
 *
 * @author Andrea
 */
public class World {

	private Game game;
	private GameUI ui;
	private SubWorld subWorld;
	private BoundedCamera camera;
	private OrthographicCamera canvasCamera;
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private SpriteBatch batch;
    private Player player;
    
	private EntitiesManager entities = new EntitiesManager();
    /** All spawners currently active */
    private List<Spawner> spawners = new ArrayList<Spawner>();
    /** All lights currently active */
    private Set<Light> lights = new HashSet<Light>();
    /** Manages time and other environment options */
    private Ambience ambience = new Ambience(this);
    /** True if the world is in transition */
    private boolean transitionLock = true;
    /** True if the world is shifting */
    private boolean shiftLock;
    /** True if the world shift is waiting assets */
    private boolean shiftAssets;
    /** Reserved for shifting in multi map mode */
	private FutureTask<ShiftInfo> task;
	/** Used to control if the world has been shifted during an interval of time */
    private int shiftCounter = 0;
    /** The id of the current map that contains the player */
    private int currentMapID = -1;
	
	/** Max size for multi map mode */
    public static final int MAP_SIZE = 100;
    /** How many nodes per tile side */
    public static final int NODE_FACTOR = 2;
    public static final int TILE_SIZE = 32;
    public static final int NODE_SIZE = TILE_SIZE / NODE_FACTOR;
    /** Effective map size for multi map mode */
    public static final int MAP_SIZE_EFFECTIVE = MAP_SIZE * TILE_SIZE;
    public static final int SHIFT_RANGE = MAP_SIZE_EFFECTIVE;
	/** Shape to detect collision with tiles */
    public static final Rectangle TILE_SHAPE = new Rectangle(0, 0, TILE_SIZE, TILE_SIZE);
    /** Texture of a light */
    public static Texture LIGHT_TEXTURE;
    /** Frame buffer used for lighting */
    public static FrameBuffer FBO;
    /** Particle pool for blood effect */
    public static ParticleEffectPool BLOOD_EFFECT_POOL;

    public World(Game game, GameUI ui) {
    	this.game = game;
    	this.ui = ui;
		batch = game.getBatch();
    	create();
    	ui.inizialize(player, batch, canvasCamera);
    	ui.setMode(Game.gameInput.getUImode());
    }
    
    public void create() {
    	LIGHT_TEXTURE = new Texture("data/misc/light.png");
    	BLOOD_EFFECT_POOL = new ParticleEffectPool(Game.assets.get("data/particle/blood.p", ParticleEffect.class), 1, 6);
    	/* Create player */
    	player = new Player(0, 0, this, Game.assets.getCreatureInfo(0), Game.assets.getFaction(1));
    	/* Init cameras */
    	camera = new BoundedCamera();
    	camera.setToOrtho(false, Game.width, Game.height);
    	camera.translate(player.getCenterX(), player.getCenterY());
    	canvasCamera = new OrthographicCamera();
    	canvasCamera.setToOrtho(false, Game.width, Game.height);
    }
    
    public void render(float delta) {
    	/* Update */
    	updateEntities(delta);
    	updateSpawners(delta);
    	updateLights(delta);
    	ambience.update(delta);
    	ui.update(delta);
    	checkTransition();
    	checkShift();
    	checkCurrentMap();
    	/* Prepare for normal rendering */
    	camera.centerOnSmooth(player.getCenterX(), player.getCenterY());
    	camera.update();
    	batch.setShader(Game.shader);
    	/* Prepare the ambient illumination */
    	ambience.prepareAmbientRendering();
    	/* Render subworld */
    	entities.sort();
    	subWorld.render(camera, entities.getAll(), lights, batch);
        /* Draw debug */
        drawDebug();
        /* Prepare for flat coordinates rendering */
        batch.setProjectionMatrix(canvasCamera.combined);
        ambience.draw();
        /* Disable shader */
        batch.setShader(null);
        ui.draw();
    }
    
	public void drawDebug() {
		/* At least one option must be active */
		if (!Game.debug_entityHB && 
				!Game.debug_dangerHB)
			return;
		
		/* Prepare the rendered */
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);

        for (Entity x : entities.getAll()) {
        	if (Game.debug_entityHB) {
            	shapeRenderer.setColor(1, 1, 1, 1);
        		shapeRenderer.rect(x.getShape().getX(), x.getShape().getY(), 
        				x.getShape().getWidth(), x.getShape().getHeight());
        	}
        	
        	if (Game.debug_dangerHB) {
        		shapeRenderer.setColor(1, 0, 0, 1);
	        	if (x.getDangerZone() != null) {
	        		shapeRenderer.polygon(x.getDangerZone().getHitBox().getShape().getPoints());
	        		if (x.getDangerZone().getHitBox().getStart() != null)
	        			shapeRenderer.circle(x.getDangerZone().getHitBox().getStart().x, 
	        					x.getDangerZone().getHitBox().getStart().y, 3);
	        	}
        	}
        }
        shapeRenderer.end();
	}
    
    private void updateEntities(float delta) {
    	/* Synchronize and lock entities to prevent modification during the update cycle */
    	entities.synchronize();
    	entities.lock();
    	
    	for (Iterator<Entity> iter = entities.getAll().iterator(); iter.hasNext();) {
    		Entity entity = iter.next();
    		
    		/* Check entity existance */
			if (!entity.exist()) {
				iter.remove();
				entity.dispose();
				if (entity == player)
					gameOver();
				continue;
			}
			if (!entity.isAlive())
				entity.die();   
			/* Update cycle */
    		entity.initialize();
    		entity.update(delta);
    		/* Check entity existance */
			if (!entity.exist()) {
				iter.remove();
				entity.dispose();
				if (entity == player)
					gameOver();
				continue;
			}
			if (!entity.isAlive())
				entity.die();           
        }    
    	
    	entities.unlock();
	}

    private void updateSpawners(float delta) {
    	for (Iterator<Spawner> iter = spawners.iterator(); iter.hasNext();) {
    		Spawner spawner = iter.next();
            if (spawner.isDone())
                iter.remove();
            else {
                spawner.update(delta);
            }             
        }    	    	
    }
    
    private void updateLights(float delta) {
    	for (Light light : lights)
    		light.update(delta);
    }
    
    public void spawnLight(Light light) {
    	lights.add(light);
    }
    
    public void spawnLight(Collection<? extends Light> collection) {
    	lights.addAll(collection);
    }
    
    public void killLight(Light light) {
    	lights.remove(light);
    }
    
    private void checkCurrentMap() {
    	int newMap = subWorld.getMapContaining(player.getX(), player.getY()).getId();
    	if (newMap != currentMapID) {
    		currentMapID = newMap;
        	takeEvent(new MapEvent(newMap));
    	}
    }

    /**
     * Controls if the player has stepped on one sub world transition
     */
    public void checkTransition() {
        if (player.isActionEqual(ActionType.WALK)) {
            for (Transition t : subWorld.getTransitions())
                if (Collision.collide(t.getShape(), player.getPerspectiveShape()))
                    changeSubWorld(t);
        }
    }
    
    private void changeSubWorld(Transition t) {
    	/* Lock world */
    	transitionLock = true;
    	game.openSubWorld(t);
    }
    
	public SpriteBatch getBatch() {
    	return batch;
    }
	
	public GameUI getUI() {
		return ui;
	}
    
	public void loadSubWorld(SubWorldInfo info) {
		/* Dispose old subworld, if present */
		if (subWorld != null)
			subWorld.dispose();
		subWorld = new SubWorld(info.maps);
    	camera.calibrate(this);
		/* Prepare entities */
        entities.clear();
        spawnEntity(player);
        spawnEntity(info.entities);
        /* Prepare ambience */
        ambience.takeTransition();
        /* Prepare lights */
        lights.clear();
        spawnLight(info.lights);
        /* Prepare player */
        player.setPos(info.playerX, info.playerY);
    	/* Set new current map */
    	checkCurrentMap();
        /* Prepare camera */
    	camera.centerOn(player.getCenterX(), player.getCenterY());
        /* Release lock */
        transitionLock = false;
        /* Change state */
        game.openInGame();
	}
	
    public int tileToNode(int tile) {
		return tile * NODE_FACTOR;
    }
    
    public void spawnEntity(Entity entity) {
    	entities.add(entity);
    }
    
    public void spawnEntity(List<Entity> list) {
    	entities.addAll(list);
    }
    
    public void spawnSpawner(Spawner spawner) {
    	spawners.add(spawner);
    }
    
	/**
	 * @return True if a map is present in the maps matrix on the given coordinates
	 */
	public boolean checkViability(int x, int y) {
		return subWorld.checkViability(x, y);
	}
	
	public SubWorldMode getMode() {
		return subWorld.getMode();
	}
	
	public void checkShift() {
		if (!transitionLock)
			if (getMode() == SubWorldMode.MULTI_MAP) {
				if (!shiftLock) {
					/* Check if we are too far from the center of the world */
					if (new Vector2(camera.position.x, camera.position.y).dst(
							new Vector2(subWorld.getCenterWorldX(), subWorld.getCenterWorldY())) >= World.MAP_SIZE_EFFECTIVE) {
						/* Get which is the new map and lock shifting */
						shiftLock = true;
						/* Request the assets needed */
						Callable<ShiftInfo> loader = new ShiftLoader(
								this,
								SubWorld.indexToMapDir(SubWorld.pointToMapX(camera.position.x), SubWorld.pointToMapY(camera.position.y)), 
								subWorld.getMaps());
						((ShiftLoader) loader).requestAssets();
						shiftAssets = true;
						task = new FutureTask<ShiftInfo>(loader);
					}
				}
				else {
					/* If a task has been been decided wait for resources before starting the thread */
					if (task != null) {
						if (Game.assets.update() && shiftAssets) {
							Thread thread = new Thread(task);
							thread.start();
							shiftAssets = false;
						}
					}
					/* It's shifting, so check if the loading is complete */
					if (task.isDone()) {
						try {
							ShiftInfo info = task.get();

							subWorld.takeShift(info);	
							ambience.takeShift();
							
							/* Compute the displacement due to the world shift */
							float offsetX = -info.dir.getVectorOfOnes().x * World.MAP_SIZE_EFFECTIVE;
							float offsetY = -info.dir.getVectorOfOnes().y * World.MAP_SIZE_EFFECTIVE;

							/* Update the position of entities and delete them if they are too far */
						    for (Iterator<Entity> iter = entities.getAll().iterator(); iter.hasNext();) {
						    	Entity entity = iter.next();
		
						    	entity.shiftPosition(offsetX, offsetY);
						    	/* Remove units that are too far */
								if (entity.getCenterX() < 0 || 
										entity.getCenterX() > MAP_SIZE_EFFECTIVE * SubWorld.MAP_MATRIX_SIZE ||
										entity.getCenterY() < 0 || 
										entity.getCenterY() > MAP_SIZE_EFFECTIVE * SubWorld.MAP_MATRIX_SIZE) {
									iter.remove();
									entity.dispose();
								}
							}
						    
						    /* Update the position of lights and delete them if they are too far */
						    for (Iterator<Light> iter = lights.iterator(); iter.hasNext();) {
						    	Light light = iter.next();
		
						    	light.shiftPosition(offsetX, offsetY);
								if (light.getX() < 0 || 
										light.getX() > MAP_SIZE_EFFECTIVE * SubWorld.MAP_MATRIX_SIZE ||
										light.getY() < 0 || 
										light.getY() > MAP_SIZE_EFFECTIVE * SubWorld.MAP_MATRIX_SIZE)
									iter.remove();
							}
						 
						    /* Add new entities and lights */
						    spawnEntity(info.entities);
						    spawnLight(info.lights);
						    
						    /* Release lock and increase the counter */
							shiftLock = false;
							increaseShiftCounter();
							task = null;
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	
	/**
	 * Start the game over procedure
	 */
	public void gameOver() {
		ambience.setGreyModeActive(true);
	}
	
	/**
	 * @return The shift counter, used to understand if the world has shifted during an interval of time
	 */
    public int getShiftCounter() {
    	return shiftCounter;
    }
    
    private void increaseShiftCounter() {
    	shiftCounter++;
    	if (shiftCounter > 1000)
    		shiftCounter = 0;
    }
	
	public void takeEvent(Event event) {
		Game.diary.takeEvent(event);
	}
    
	/**
	 * @param range Range of detection
	 * @return All the danger zones surrounding this entity
	 */
	public List<DangerZone> getDangers(Entity entity, float range) {
		List<DangerZone> list = new ArrayList<DangerZone>();
		/* Get dangers from all entities in range */
		for (Entity x : entities.getAll())
			if (!x.equals(entity) && x.computeDistance(entity) <= range && x.getDangerZone() != null && x.getDangerZone().isActive())
				list.add(x.getDangerZone());

		return list;
	}
	
	public float getSoundIntensity(float x, float y) {
		float distance = Vector2.dst(x, y, player.getX(), player.getY());
		/* Sounds too far cannot be heard */
		if (distance > GlobalVar.SOUND_MAX_DISTANCE)
			return 0;

		return (GlobalVar.SOUND_MAX_DISTANCE - distance) / GlobalVar.SOUND_MAX_DISTANCE;
	}
	
    /**
     * @return A new temporary array containing all entities around one entity in a given range
     */
    public PoolableArray<Entity> getEntities(Entity escluded, float range) {
    	return entities.getEntities(escluded, range);
    }
    
    public List<Entity> getEntities() {
        return entities.getAll();
    }
    
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 1f;
	}
    
    /**
     * @return The X position (in tiles)
     */
    public static int getCreatureTileX(Entity entity) {
        return absToTileX(entity.getCenterX());
    }   
    
    /**
     * Convert an absolute position to a tile position
     * 
     * @return The X position (in tiles)
     */
    public static int absToTileX(float x) {
    	return Math.round((x - (float)TILE_SIZE/2)/(float)TILE_SIZE);
    }
    
    /**
     * @return The Y position (in tiles)
     */
    public static int getCreatureTileY(Entity entity) {
        return absToTileY(entity.getPerspectiveCenterY());
    }
    
    /**
     * Convert an absolute position to a tile position
     * 
     * @return The Y position (in tiles)
     */
    public static int absToTileY(float y) {
        return Math.round((y - (float)TILE_SIZE/2)/(float)TILE_SIZE);      
    }
    
    /**
     * @return The X position (in nodes)
     */
    public static int getCreatureNodeX(Entity entity) {
        return absToNodeX(entity.getCenterX());
    }   
    
    /**
     * Convert an absolute position to a node position
     * 
     * @return The X position (in nodes)
     */
    public static int absToNodeX(float x) {
    	return Math.round((x - (float)NODE_SIZE/2)/(float)NODE_SIZE);
    }
    
    /**
     * @return The Y position (in nodes)
     */
    public static int getCreatureNodeY(Entity entity) {
        return absToNodeY(entity.getPerspectiveCenterY());
    }
    
    /**
     * Convert an absolute position to a node position
     * 
     * @return The Y position (in nodes)
     */
    public static int absToNodeY(float y) {
        return Math.round((y - (float)NODE_SIZE/2)/(float)NODE_SIZE);      
    }
    
    /**
     * @return A vector containing the position (in nodes)
     */
    public Vector2i getCreatureNodePos(Entity entity) {
    	return (new Vector2i(World.getCreatureNodeX(entity), World.getCreatureNodeY(entity)));
    }
    
	public float getSingleMapWidth() {
		return subWorld.getSingleMapWidth();
	}

	public float getSingleMapHeight() {
		return subWorld.getSingleMapHeight();
	}
	
	/**
	 * @return Height in nodes
	 */
	public int getHeightInNodes() {
		return subWorld.getHeightInNodes();
	}

	public void dispose() {
		LIGHT_TEXTURE.dispose();
		shapeRenderer.dispose();
		if (FBO != null)
			FBO.dispose();
		if (subWorld != null)
			subWorld.dispose();
	}
	
	/**
	 * @return A new istance of color with the ambient illumination for the current maps
	 */
	public Color getIllumination() {
		/* Fake null value if the world is in transition */
		if (transitionLock)
			return new Color(0, 0, 0, 0);
		return new Color(subWorld.getIllumination(player.getPos()));
	}
	
	/**
	 * @return The weather info of the current maps
	 */
	public MeteoInfo getWeatherInfo() {
		return subWorld.getWeatherInfo();
	}
	
	public boolean blockedNode(int tx, int ty) {
		return subWorld.blockedNode(tx, ty);
	}
	
	public boolean blockedTile(int x, int y) {
		return subWorld.blockedTile(x, y);
	}
	
	/**
	 * @see SubWorld#clearLine(float, float, float, float)
	 */
	public boolean clearLine(Vector2 start, Vector2 end) {
		return clearLine(start.x, start.y, end.x, end.y);
	}
	
	/**
	 * @see SubWorld#clearLine(float, float, float, float)
	 */
	public boolean clearLine(float sx, float sy, float tx, float ty) {
		return subWorld.clearLine(sx, sy, tx, ty);
	}
	
    
	/**
	 * @param smooth If true the mover will try to slide around the blocked tile
	 */
    public static void moveEntity(World world, Mover mover, float delta, boolean smooth) {
        float dx, dy;
        
        if (mover.getMovV().len() == 0)
        	return;
        
        dx = delta * mover.getMovV().x;
        dy = delta * mover.getMovV().y;

        int i = ExtraMath.findDivisionsNumber(dx, dy, GlobalVar.MAX_DELTA_DISPLACEMENT_CHECK);
        
        dx = dx / (float) i;
        dy = dy / (float) i;
        
        for (int j = 0; j < i; j++)
        	if (!mover.move(world, dx, dy, smooth)) {
        		break;
        	}
        
        mover.setMovV(0f, 0f);
    }
	
    /**
     * @return All nodes blocked by the specified entities
     */
    public static Set<Vector2i> getEntitiesArea(Iterable<Entity> entities) {
    	Set<Vector2i> set = new HashSet<Vector2i>();
    	
    	/* Insert occupied nodes in the set */
    	for (Entity x : entities) {
    		int r = Collision.getSquareRadius(x, World.NODE_SIZE, false) - 1;
    		for (int i = getCreatureNodeX(x) - r; i <= getCreatureNodeX(x) + r; i++)
    			for (int j = getCreatureNodeY(x) - r; j <= getCreatureNodeY(x) + r; j++) {
    				set.add(new Vector2i(i, j));		
    			}
    	}
    	
    	return set;
    }
    


}