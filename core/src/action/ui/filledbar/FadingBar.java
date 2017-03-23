package action.ui.filledbar;

import action.utility.timer.Timer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A filledbar that fades after a certain amount of inactive time
 * 
 * @author Andrea
 */
public class FadingBar extends FilledBar {

	/** Timer for how much the bar stay active after an update */
	private Timer activeTimer;
	/** Timer for how much times it takes to fade away */
	private Timer fadeTimer;
	private boolean fading = false;

	public FadingBar(int width, int height, FilledBarMargin margin, TextureRegion empty, TextureRegion fill, boolean vertical, 
			float activeTime, float fadeTime) {
		super(width, height, margin, empty, fill, vertical);
		activeTimer = new Timer(activeTime);
		fadeTimer = new Timer(fadeTime);
	}
	
	public FadingBar(int width, int height, TextureRegion empty, TextureRegion fill, boolean vertical, float activeTime, float fadeTime) {
		this(width, height, new FilledBarMargin(), empty, fill, vertical, activeTime, fadeTime);
	}
	
	/**
	 * Initialize the bar to a given fill ratio
	 */
	public void init(float ratio) {
		setFilled(ratio);
	}
	
	public void update(float ratio, float delta) {
		/* First update px filled (only with a valid ratio) */
		if (ratio >= 0 && ratio <= 1)
	        if (setFilled(ratio)) {
	        	/* Reset everything */
	            setVisible(true);
	            activeTimer.restart();
	            fading = false;
	            fadeTimer.restart();
	            setAlpha(1f);
	            return;
	        }
        
        /* No changes in the px filled */
		if (isVisible()) {
			if (fading) {
				fadeTimer.update(delta);
				if (fadeTimer.expired())
					setVisible(false);
			}
			else {
				activeTimer.update(delta);
	            if (activeTimer.expired())
	                fading = true;
			}
		}
	}
	
    @Override
	public void draw(Batch batch, float x, float y) {
        if (isVisible()) {
        	/* First set alpha, then draw */
        	if (fading)
        		setAlpha(1 - fadeTimer.getProgress());

        	drawBar(batch, x, y);
        }
    }

}
