package action.core;

import action.ui.filledbar.FilledBar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

/**
 * Loading screen
 * 
 * @author Andrea
 */
public class InLoading implements Screen {

	Game game;
	private TextureAtlas uiTexture;
	private Texture background;
	private FilledBar bar;
	private SpriteBatch batch;
	
	/** How much is the bar wide (in relation to the total screen width) */
	private static final float BAR_RELATIVE_WIDTH = 0.8f;
	private static final float BAR_HEIGHT = 8;
	private static final float BAR_Y_POSITION = 0.1f;
	
	public InLoading(Game game) {
		this.game = game;
		batch = game.getBatch();
	}
	
	public void load() {
		loadEssential();
		Game.assets.load();
	}
	
	private void loadEssential() {
		/* Force the loading of the UI essentials */
		Game.assets.load("data/ui/ui.pack", TextureAtlas.class);
		Game.assets.finishLoading();
		background = new Texture("data/splash/loading.jpg");
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		uiTexture = Game.assets.get("data/ui/ui.pack", TextureAtlas.class);
		
		bar = new FilledBar((int) (Gdx.graphics.getWidth() * BAR_RELATIVE_WIDTH), (int) BAR_HEIGHT, 
				uiTexture.findRegion("filledbar.black"), uiTexture.findRegion("filledbar.red"), false);
	}

	@Override
	public void render(float delta) {
		if (Game.assets.update()) {
			game.initAfterLoading();
			game.openMenu();
		}

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    /* Display loading information */
		bar.setFilled(Interpolation.linear.apply(bar.getFilledRatio(), Game.assets.getProgress(), 0.1f));
		bar.draw(batch, (Gdx.graphics.getWidth() - bar.getWidth())/2, Gdx.graphics.getHeight() * BAR_Y_POSITION);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		background.dispose();
	}

}
