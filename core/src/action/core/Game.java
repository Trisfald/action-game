package action.core;

import action.database.ResourceManager;
import action.diary.Diary;
import action.input.GameInput;
import action.master.GameMaster;
import action.master.OfflineMaster;
import action.sound.SoundManager;
import action.ui.GameUI;
import action.world.World;
import action.world.map.Transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * 
 * @author Andrea
 *
 */
public class Game extends com.badlogic.gdx.Game {
	 
	World world;
	InLoading inLoading;
	InGame inGame;
	InMenu inMenu;
	InTransition inTransition;
	private SpriteBatch batch;
	
	public static ResourceManager assets = new ResourceManager();
	/** Game master */
	private static GameMaster master = new OfflineMaster();
	/** Platform dependent input */
	public static GameInput gameInput;
	/** Sound manager */
	public static SoundManager sounds;
	public static ShaderProgram shader;
	public static Diary diary = new Diary();
	
	/** Internal resolution width */
	public static int width;
	/** Internal resolution height */
	public static int height;
	
	public static boolean debug_entityHB = false;
	public static boolean debug_dangerHB = false;
	public static boolean debug_fps = true;
	
	@Override
	public void create() {
		setInternalResolution();
		batch = new SpriteBatch();
		/* Create the shader */
		shader = new ShaderProgram(Gdx.files.internal("data/shader/vertex.glsl").readString(), 
				Gdx.files.internal("data/shader/final.glsl").readString());
		ShaderProgram.pedantic = false;
		/* Create the screens */
		inLoading = new InLoading(this);
		inGame = new InGame(this);
		inMenu = new InMenu(this);
		inTransition = new InTransition(this);
		setScreen(inLoading);
		inLoading.load();
	}
	
	private void setInternalResolution() {
		final int specWidth = Gdx.graphics.getWidth();
		final int specHeight = Gdx.graphics.getHeight();	
		final float realRatio = (float)specWidth / specHeight;
		height = Math.min(600, specHeight);
		width = Math.round(height * realRatio);
	}

	/**
	 * Open the in game screen
	 */
	public void openInGame() {
		setScreen(inGame);
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	/**
	 * Load and open a subworld
	 */
	public void openSubWorld(Transition transition) {
		inTransition.preEnter(transition);
		setScreen(inTransition);
	}
	
	/**
	 * Initializations done after loading is completed
	 */
	public void initAfterLoading() {
		sounds = new SoundManager();
		inGame.ui = new GameUI(this);
		inGame.ui.create();
        world = new World(this, inGame.ui);
        diary.setUI(inGame.ui);
	}
	
	public void renewWorld() {
		world.dispose();
		world = new World(this, inGame.ui);
		diary.clear();
	}
	
	public World getWorld() {
		return world;
	}
	
	public void openMenu() {
		setScreen(inMenu);
	}
	
	public static GameMaster master() {
		return master;
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		shader.dispose();
		world.dispose();
		inLoading.dispose();
		inGame.dispose();
		inMenu.dispose();
		inTransition.dispose();
		assets.dispose();
	}

}

