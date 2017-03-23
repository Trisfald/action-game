package action.android;

import android.view.View;

import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

/**
 * Resolution strategy that keeps the native aspect ratio but enforces a fixed height
 * 
 * @author Andrea
 */
public class MinimalHAspectRatio implements ResolutionStrategy {
	
	public final int desiredHeight;
	
	public MinimalHAspectRatio(int desiredHeight) {
		this.desiredHeight = desiredHeight;
	}

	@Override
	public MeasuredDimension calcMeasures(int widthMeasureSpec, int heightMeasureSpec) {
		final int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		final int specHeight = View.MeasureSpec.getSize(heightMeasureSpec);
		
		final float realRatio = (float)specWidth / specHeight;

		int height = Math.min(desiredHeight, specHeight);
		int width = Math.round(height * realRatio);

		return new MeasuredDimension(width, height);
	}

}
