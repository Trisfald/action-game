package action.core;

import action.ui.GameUI;
import action.world.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

/**
 * Main In game screen
 * 
 * @author Andrea
 */
public class InGame implements Screen {
	
	Game game;
	GameUI ui;

	public InGame(Game game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Game.gameInput.update(delta);
		game.world.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		World.FBO = new FrameBuffer(Format.RGBA4444, width, height, false);
		Game.shader.begin();
		Game.shader.setUniformf("resolution", width, height);
		Game.shader.end();
	}

	@Override
	public void show() {
		Game.gameInput.init(game.world);
		Gdx.input.setInputProcessor(new InputMultiplexer(Game.gameInput, ui.getInputProcessor()));
		Game.sounds.music.playExploration();
	}

	@Override
	public void hide() {
		Game.sounds.music.pauseExploration();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		ui.dispose();
	}

}
