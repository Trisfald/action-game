package action.interfaces;

import action.core.Game;

/**
 * A wrapper interface to hold global variables of different kind
 * 
 * @author Andrea
 */
public interface GlobalVar {

	/*
	 * Data
	 */
	public final static String AVATAR_PATH = "data/avatar/";
	public final static String FILLEDBAR_PATH = "data/filledbar/";
	public final static String MAP_PATH = "data/map/";
	public final static String RESOURCE_PATH = "data/resource.xml";
	public final static String SPLASH_PATH = "data/splash/";
	public final static String TILESET_PATH = "data/tileset/";
	
	
	/*
	 * Gameplay
	 */
	public static final float DEFAULT_PERSPECTIVE_RATIO = 0.4f;
	/** Basic movespeed for all entities */
	public static final float BASE_MOVESPEED = 115f;
	/** Basic dodge movespeed */
	public static final float DODGE_MOVESPEED = BASE_MOVESPEED * 3;
    /** Maximum amount of dx or dy that can be checked in one iteration */
    public static final float MAX_DELTA_DISPLACEMENT_CHECK = 8;
    /** Default value */
    public static final float HITBOX_RADIUS = 15f;
    /** Offset to sum to the angle of a vector to get the equivalent angle valid for a shape */
    public static final float SHAPE_IMG_ANGLE_OFFSET = -90;
    /** Combat timer length in seconds */
    public static final float COMBAT_TIMER_LENGTH = 5;
    /** Minimum duration to enter the hold state for a creature who's taking damage */
    public static final float HOLD_MIN_DURATION = 0.5f;
    /** Duration of hold state after the block has been broken */
    public static final float BREAK_BLOCK_HOLD = 0.5f;
    /** Speed of the game clock (how much time is faster than reality) */
	public static final int CLOCK_SPEED = 100; 
	/** Base cost in stamina for dodge action */
	public static final float DODGE_COST = 10;
	/** Base duration for recovering after dodge */
	public static final float DODGE_RECOVER_DURATION = 0.5f;
	/** Base distance covered by a dodge */
	public static final int DODGE_DISTANCE = 36;
	/** Minimum finesse needed to perform a dodge action */
	public static final int DODGE_MIN_FINESSE = 60;
	/** Ratio of stamina at which the malus starts */
	public static final float STAMINA_MALUS_START = 0.5f;
	/** Max amount of stamina malus */
	public static final float STAMINA_MALUS_MAX = 0.4f;
	
	/** Time ahead used to foresee a danger */
	public static final float DANGER_EVOLUTION_TIME = 1;
	/** Start distance of the danger point origin */
	public static final float DANGER_START_DISTANCE = 1000;
	
	
	/*
	 * AI
	 */
	/** Max path length for pathfinder */
	public static final int MAX_PATH_LENGTH = 80;
	/** Basic spot distance for AI */
	public static final float BASE_SPOT_DISTANCE = 350;
	/** Probability to block for AI */
	public static final float BASE_BLOCK_PROBABILITY = 0.5f;
	/** Base time for AI reflexes */
	public static final float BASE_REFLEX_TIME = 0.225f;
	/** Base maximum border distance for AI while fighting */
	public static final float BASE_COMBAT_MAX_DISTANCE = 80;
	/** Base stamina ratio required to keep the block up for AI (~ min def: 70) */
	public static final float BASE_GUARDED_STAMINA = 0.75f;
	/** Guarded feature for AI */
	public static final boolean GUARDED_ENABLED = false;
	/** Maximum border distance from an enemy to be in melee range for AI */
	public static final float MELEE_RANGE_DISTANCE = 150;
	/** Distance interval for AI field of view ray casting */
	public static final float FOV_RAY_DISTANCE_INTERVAL = 16;
	 
    
	/*
	 * Misc
	 */
	/** Horizontal size of color scales images */
	public static final int SCALE_SIZE = 100;
	
	public static final String WHITE_YELLOW_RED_SCALE = "MISC_WHITE_RED_SCALE";
	public static final String DEFAULT_EFFECT_ICON = "EFFECT_ICON_DEFAULT";
	public static final int EFFECT_ICON_SIZE = 32;
	
	public static final int FAKE_MONEY_ITEM_ID = 0;
	
	/** Time needed to switch from normal mode to grayscale mode */
	public static final float GREYSCALE_TURN_TIME = 3;
	
	/** Margin to add to the camera when deciding if an object is inside it */
	public static final float ONCAMERA_MARGIN = 100;
	
	
	/*
	 * Map layers
	 */
	public static final int[] TERRAIN_LAYERS = new int[]{0, 1};
	public static final int[] ABOVE_LAYERS = new int[]{2, 3};
    public static final int TILE_PROPERTIES_LAYER = 4;
    
    
    /*
     * Sound
     */
    public static final float VOLUME_SFX = 0.2f;
    public static final float VOLUME_MUSIC = 0.1f;
    /** Max distance at which a sound can be heard */
    public static final float SOUND_MAX_DISTANCE = 600f;
    
    
    /*
     * Text
     */
	public static final String ALERT_QUEST_NEW = Game.assets.getDialog(43);
	public static final String ALERT_QUEST_COMPLETED = Game.assets.getDialog(44);

    
}
