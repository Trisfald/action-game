package action.desktop;

import action.core.Game;
import action.input.DesktopInput;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.foregroundFPS = 0;
		//config.vSyncEnabled = false;
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new Game(), config);
		Game.gameInput = new DesktopInput();
	}
}
