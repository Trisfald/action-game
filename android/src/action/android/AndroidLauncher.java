package action.android;

import action.core.Game;
import action.input.TouchInput;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
	    config.useAccelerometer = false;
	    config.useCompass = false;
	    config.resolutionStrategy = new FillResolutionStrategy();
		initialize(new Game(), config);
		Game.gameInput = new TouchInput();
	}
}
