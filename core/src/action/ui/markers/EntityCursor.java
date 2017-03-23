package action.ui.markers;

import action.core.Game;
import action.utility.TextureRRef;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Cursor for highlighting entities
 * 
 * @author Gibsy, Andrea
 *
 */
public class EntityCursor {
	
    private TextureRegion image;
    private boolean enabled;
    private int width;
    private int height;
    
    public static final TextureRRef DEFAULT_CURSOR = new TextureRRef("UI", "entitycursor");
		
    public EntityCursor(TextureRegion image, int width, int height) {
    	this.image = image;
    	this.width = width;
    	this.height = height;
    }
    
    /**
     * Creates a cursor with the image native size
     */
    public EntityCursor(TextureRegion image) {
    	this.image = image;
    	width = image.getRegionWidth();
    	height = image.getRegionHeight();
    }
    
    /**
     * Creates a cursor with the default appearance
     */
    public EntityCursor(int width, int height) {
    	this(Game.assets.getTextureRegion(DEFAULT_CURSOR), width, height);
    }
    
    /**
     * Creates a cursor with the default appearance and image native size
     */
    public EntityCursor() {
    	this(Game.assets.getTextureRegion(DEFAULT_CURSOR));
    }
    
    /**
     * Draw the cursor at the given coordinates
     * @param x
     * @param y
     */
    public void draw(SpriteBatch batch, float x, float y) {
        if (enabled)
        	batch.draw(image, x, y, width, height);
    }

    /**
     * @return True if the cursor is currently enabled
     */
    public boolean isEnabled(){
    	return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }
	

}
