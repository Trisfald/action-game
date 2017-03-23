package action.core;

import action.world.map.Transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

/**
 * Screen for main menu
 * 
 * @author Andrea
 *
 */
public class InMenu implements Screen {
	
	Game game;
	private Texture background;
	private SpriteBatch batch;
	private Stage stage;

	public InMenu(Game game) {
		this.game = game;
		batch = game.getBatch();
		Game.assets.load("data/ui/skin/uiskin.json", Skin.class);
		Game.assets.finishLoading();
		background = new Texture("data/splash/loading.jpg");
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Skin skin = Game.assets.get("data/ui/skin/uiskin.json", Skin.class);
		stage = new Stage(
				new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()),
				batch);
		
		/* Build the stage */
		Table table = new Table();
		Button start = new TextButton("Start", skin);
		Button exit = new TextButton("Exit", skin);
		
		table.defaults().width(150);
		table.defaults().height(60);
		table.defaults().spaceBottom(20);
		
		table.add(start);
		table.row();
		table.add(exit);
		table.setPosition((Gdx.graphics.getWidth()-table.getWidth())/2, (Gdx.graphics.getHeight()-table.getHeight())/2);
		stage.addActor(table);

		start.addListener(new InputListener() {
            @Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	launchWorld();
            }
        });
		
		exit.addListener(new InputListener() {
            @Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            	Gdx.app.exit();
                return true;
            }
        });

	}
	
	private void launchWorld() {
		game.openSubWorld(new Transition(0, 768, 400));
		Game.diary.takeNewQuest(0);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(100f / 255f, 100f / 255f, 250f / 255f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
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
		stage.dispose();
	}

}
