package action.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import action.world.map.Transition;
import action.world.subworld.SubWorld.SubWorldInfo;
import action.world.subworld.SubWorldLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/**
 * Manages the transition between different parts of the world
 *
 * @author Andrea
 */
public class InTransition implements Screen {
	
	Game game;
	private Callable<SubWorldInfo> loader;
	private FutureTask<SubWorldInfo> task;
	
	/** True if the thread with the future task has started */
	private boolean started;

	public InTransition(Game game) {
		this.game = game;
	}
	
	@Override
	public void render(float delta) {
		/* First load all resources */
		if (Game.assets.update()) {
			/* Build the subtworld loader */
			startThread();
			
			if (task != null && task.isDone())
				try {
					game.getWorld().loadSubWorld(task.get());
					/* Clear task and transition */
					task = null;
					started = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
		}
		
		Gdx.gl.glClearColor(0f / 255f, 0f / 255f, 0f / 255f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	private void startThread() {
		if (started)
			return;
		
		Thread thread = new Thread(task);
		thread.start();
		started = true;
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

	}
	
	/**
	 * Prepare for a transition
	 */
	public void preEnter(Transition transition) {
		loader = new SubWorldLoader(game.getWorld(), transition);
		((SubWorldLoader) loader).requestAssets();
		task = new FutureTask<SubWorldInfo>(loader);
	}

}
