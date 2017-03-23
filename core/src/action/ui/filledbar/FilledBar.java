package action.ui.filledbar;

import action.utility.ExtraMath;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * An utility to manage fillable bars
 *
 * @author Andrea
 */
public class FilledBar {

	private float x;
	private float y;
    protected TextureRegion empty;
    protected TextureRegion fill;
    /** Original alpha */
    protected float emptyAlpha;
    /** Original alpha */
    protected float fillAlpha;
    protected int pxFilled = 0;
    protected int height;
    protected int width;
    protected int filledHeight;
    protected int filledWidth;
    protected FilledBarMargin margin;
    protected boolean visible = true;
    protected boolean vertical;
    
    
    public FilledBar(int width, int height, FilledBarMargin margin, TextureRegion empty, TextureRegion fill, boolean vertical) {
        this.height = height;
        this.width = width;
        this.empty = empty;
        this.margin = margin;
        this.fill = fill;
        this.emptyAlpha = 1;
        this.fillAlpha = 1;
        this.vertical = vertical;
        this.filledWidth = width - margin.left - margin.right;
        this.filledHeight = height - margin.top - margin.bottom;
    }
    
    /**
     * Create a filled bar without inner margin
     */
    public FilledBar(int width, int height, TextureRegion empty, TextureRegion fill, boolean vertical) {
    	this(width, height, new FilledBarMargin(), empty, fill, vertical);
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }

    public boolean isVertical() {
    	return vertical;
    }
    
    public void setPosition(float x, float y) {
    	this.x = x;
    	this.y = y;
    }
    
    /**
     * @param ratio The new filled ratio
     * 
     * @return True if the pixel filled have been updated to a new value
     */             
    public boolean setFilled(float ratio) {
    	int tempFilled;
    	
    	ratio = ExtraMath.fitFloat(ratio, 0, 1);
    	
    	if (vertical)	
    		tempFilled = (int)Math.ceil(filledHeight * ratio);
    	else
    		tempFilled = (int)Math.ceil(filledWidth * ratio);
    	
        if (tempFilled != pxFilled) {
            pxFilled = tempFilled;
            return true;
        }
        return false;
    }
    
    public float getFilledRatio() {
    	if (vertical)	
    		return ((float) pxFilled / (float) filledHeight);
    	return ((float) pxFilled / (float) filledWidth);
    }

    /**
     * Draw the bar at the given coords
     */
    public void draw(Batch batch, float x, float y) {
        if (visible)
        	drawBar(batch, x, y);
    }
    
    public void draw(Batch batch) {
    	draw(batch, x, y);
    }
    
    protected void drawBar(Batch batch, float x, float y) {
    	if (vertical)
    		drawBarV(batch, x, y);
    	else
    		drawBarH(batch, x, y);
    }
    
    protected void drawBarV(Batch batch, float x, float y) {
    	Color tint = batch.getColor();
    	batch.setColor(1, 1, 1, emptyAlpha);
    	batch.draw(empty, x, y, width, height);
        /* Draw only a section of the image, up to the pixel filled */
    	batch.setColor(1, 1, 1, fillAlpha);
        batch.draw(fill, x + margin.left, y + margin.bottom, filledWidth, pxFilled);
        batch.setColor(tint);
    }
    
    protected void drawBarH(Batch batch, float x, float y) {
    	Color tint = batch.getColor();
    	batch.setColor(1, 1, 1, emptyAlpha);
    	batch.draw(empty, x, y, width, height);
        /* Draw only a section of the image, up to the pixel filled */
    	batch.setColor(1, 1, 1, fillAlpha);
        batch.draw(fill, x + margin.left, y + margin.bottom, pxFilled, filledHeight);
        batch.setColor(tint);
    }
    
    /**
	 * Set the alpha value of the bar
     */
    public void setAlpha(float a) {
    	emptyAlpha = a;
    	fillAlpha = a;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getPxFilled() {
        return pxFilled;
    }

    public void setPxFilled(int pxFilled) {
        this.pxFilled = pxFilled;
    }
    
    
    /**
     * @author Andrea
     */
    public static class FilledBarStyle {
    	
    	public final String empty;
    	public final String fill;
    	public final int width;
    	public final int height;
    	public final FilledBarMargin margin;
    	
		public FilledBarStyle(String empty, String fill, int width, int height, FilledBarMargin margin) {
			this.empty = empty;
			this.fill = fill;
			this.width = width;
			this.height = height;
			this.margin = margin;
		}
    	
    }
    
    
    /**
     * Inner margin between the empty texture and the fill texture in a filled bar
     * 
     * @author Andrea
     */
    public static class FilledBarMargin {
    	
    	public int top;
    	public int bottom;
    	public int left;
    	public int right;
    	
    	/**
    	 * Zero margin
    	 */
    	public FilledBarMargin() {
    		this.top = this.bottom = this.left = this.right = 0;
    	}
    	
    	public FilledBarMargin(int margin) {
    		this.top = this.bottom = this.left = this.right = margin;
    	}
    	
    	public FilledBarMargin(int vertical, int horizontal) {
    		this.top = this.bottom = vertical;
    		this.left = this.right = horizontal;
    	}
    	
    	public FilledBarMargin(int top, int bottom, int left, int right) {
    		this.top = top;
    		this.bottom = bottom;
    		this.left = left;
    		this.right = right;
    	}	
    	
    }
    
}
