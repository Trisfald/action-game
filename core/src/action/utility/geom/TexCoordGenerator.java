package action.utility.geom;

import com.badlogic.gdx.math.Vector2;

/**
 * A class capable of generating texture coordiantes based on
 * rendering positions of verticies. This allows custom texturing
 * of geometric shapes
 * 
 * @author kevin
 */
public interface TexCoordGenerator {
	/**
	 * Get the texture coordinate for a given render position
	 * 
	 * @param x The x coordinate of the vertex being rendered
	 * @param y The y coordinate of the vertex being rendered
	 * @return The texture coordinate to apply
	 */
	public Vector2 getCoordFor(float x, float y);
}
