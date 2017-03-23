package action.ui;

import action.core.Game;
import action.entity.being.Player;
import action.utility.interaction.Interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;

/**
 * In game UI
 * 
 * @author Andrea
 */
public class GameUI implements Notifier {
	
	private Game game;
	private SpriteBatch batch;
	private Stage stage; 
	private GameUImode mode;
	private StatusPanel status;
	private Button attack; 
	private Button block;
	private Button menu;
	private MoveTouchpad touchpad;
	private NoticePanel notices;
	private BitmapFont font;
	
	private float width;
	private float height;
	
	/** Offset from the left */
	private static final int STATUS_OFFSET_X = 20;
	/** Offset from the top */
	private static final int STATUS_OFFSET_Y = 15;
	/** X = offset from left, Y = vertical distance from the status panel, W = width, H = height */
	private static final int[] NOTICE_XYWH = {20, 20, 300, 500};
	/** X = offsetX from right, Y = offsetY from block button, W = width, H = height */
	private static final int[] ATTACK_XYWH = {15,15,80,80};
	/** X = offsetX from right, Y = offsetY from bottom, W = width, H = height */
	private static final int[] BLOCK_XYWH = {90,15,80,80};
	/** W = width, H = Height */
	private static final int[] MENU_WH = {160,60};
	/** X = offsetX from left, Y = offsetY from bottom, W = width, H = height */
	private static final int[] TOUCHPAD_XYWH = {10,10,160,160};
	
	public GameUI(Game game) {
		this.game = game;
	}
	
	public void create() {
		width = Game.width;
		height = Game.height;
		createStage();
		font = new BitmapFont();
	}
	
	public void inizialize(Player player, SpriteBatch batch, Camera camera) {
		this.batch = batch;
		status.initialize(player);
		stage.getViewport().setCamera(camera);
	}
	
	public void update(float delta) {
		stage.act(delta);
		Game.gameInput.touchpadMov(touchpad.getMovVector());
	}
	
	public void draw() {
		stage.draw();
		if (Game.debug_fps) {
			batch.begin();
			font.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), 
					width-50, height-25);
			batch.end();
		}
	}

	private void createStage() {
		stage = new Stage();
		
		Skin skin = Game.assets.get("data/ui/skin/uiskin.json", Skin.class);
		/* Add the needed resources to the skin */
        skin.add("touchpad.bg", Game.assets.getTextureRegion("data/ui/ui.pack", "touchpad.bg"), TextureRegion.class);
        skin.add("touchpad.knob", Game.assets.getTextureRegion("data/ui/ui.pack", "touchpad.knob"), TextureRegion.class);
        skin.add("button.down", Game.assets.getTextureRegion("data/ui/ui.pack", "button.down"), TextureRegion.class);
        skin.add("button.up", Game.assets.getTextureRegion("data/ui/ui.pack", "button.up"), TextureRegion.class);
        skin.add("button.attack", Game.assets.getTextureRegion("data/ui/ui.pack", "button.attack"), TextureRegion.class);
        skin.add("button.block", Game.assets.getTextureRegion("data/ui/ui.pack", "button.block"), TextureRegion.class);
        
        /* Create the status panel */
        status = new StatusPanel();
        status.setPosition(STATUS_OFFSET_X, height-STATUS_OFFSET_Y);
        stage.addActor(status);
        
        /* Create the notice panel */
        notices = new NoticePanel(skin);
        notices.setSize(NOTICE_XYWH[2], NOTICE_XYWH[3]);
        notices.setTargetPosition(NOTICE_XYWH[0], status.getY()-status.getWidth()-NOTICE_XYWH[1]);
        stage.addActor(notices);
		
        /* Create custom button styles */
        ImageButtonStyle attackStyle = new ImageButtonStyle();
        attackStyle.up = skin.getDrawable("button.up");
        attackStyle.down = skin.getDrawable("button.down");
        attackStyle.imageUp = attackStyle.imageDown = skin.getDrawable("button.attack");
        ImageButtonStyle blockStyle = new ImageButtonStyle();
        blockStyle.up = skin.getDrawable("button.up");
        blockStyle.down = skin.getDrawable("button.down");
        blockStyle.imageUp = blockStyle.imageDown = skin.getDrawable("button.block");
        
        /* Create the buttons */
		attack = new ImageButton(attackStyle);
		block = new ImageButton(blockStyle);
		menu = new TextButton(Game.assets.getDialog(42), skin);
		
        attack.setSize(ATTACK_XYWH[2], ATTACK_XYWH[3]);
		attack.setPosition((width-ATTACK_XYWH[0]-ATTACK_XYWH[2]), ATTACK_XYWH[1]+BLOCK_XYWH[3]+BLOCK_XYWH[1]); 
		attack.addListener(new InputListener() {
            @Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	Game.gameInput.setAttacking(true);
                return true;
            }
            @Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	Game.gameInput.setAttacking(false);
            }
        });
		stage.addActor(attack);
        
        block.setSize(BLOCK_XYWH[2], BLOCK_XYWH[3]);
        block.setPosition((width-BLOCK_XYWH[0]-BLOCK_XYWH[2]), BLOCK_XYWH[1]);
		block.addListener(new InputListener() {
            @Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	Game.gameInput.setBlocking(true);
                return true;
            }
            @Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	Game.gameInput.setBlocking(false);
            }
        });
        stage.addActor(block);
        
        menu.setSize(MENU_WH[0], MENU_WH[1]);
        menu.setPosition((width-menu.getWidth())/2, (height-menu.getHeight())/2);
		menu.addListener(new InputListener() {
            @Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	game.openMenu();
            	game.renewWorld();
            }
        });
        stage.addActor(menu);
        
        /* Create the custom touchpad style */
        TouchpadStyle style = new TouchpadStyle();
        style.background = skin.getDrawable("touchpad.bg");
        style.knob = skin.getDrawable("touchpad.knob");
        
        /* Create the touchpad */
        touchpad = new MoveTouchpad(12, style);
		//touchpad.scaleBy(2);
        touchpad.setSize(TOUCHPAD_XYWH[2], TOUCHPAD_XYWH[3]);
        touchpad.setPosition(TOUCHPAD_XYWH[0], TOUCHPAD_XYWH[1]);
        stage.addActor(touchpad);
	}
		
	public void dispose() {
		stage.dispose();
	}
	
	public InputProcessor getInputProcessor() {
		return stage;
	}
	
	public void setMode(GameUImode mode) {
		if (mode == this.mode)
			return;
		
		this.mode = mode;
		
		switch (mode){
			case DESKTOP:
				status.setVisible(true);
				attack.setVisible(false);
				block.setVisible(false);
				menu.setVisible(false);
				touchpad.setVisible(false);
				//chat box (true)
				break;	
			case TOUCHSCREEN:
				status.setVisible(true);
				attack.setVisible(true);
				block.setVisible(true);
				menu.setVisible(false);
				touchpad.setVisible(true);
				//chat box (true)
				break;
			case OFF:
				status.setVisible(false);
				attack.setVisible(false);
				block.setVisible(false);
				menu.setVisible(false);
				touchpad.setVisible(false);
				//chat box (false)
				break;	
			case GAMEOVER:
				status.setVisible(false);
				attack.setVisible(false);
				block.setVisible(false);
				menu.setVisible(true);
				touchpad.setVisible(false);
				//chat box (false)
				break;
		}
	}
	
	@Override
	public void addNotice(String text) {
		notices.addNotice(text);
	}

	public void closeGameChat() {

	}

	public void initialize(Interaction interaction) {		

	}
	
	
	/**
	 * @author Andrea
	 */
	public enum GameUImode {
		
		DESKTOP, 
		TOUCHSCREEN,
		OFF,
		GAMEOVER;
		
	}

}
