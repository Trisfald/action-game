package action.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.MathUtils;

/**
 * An approximated circonference composed by squares
 * 
 * @author Andrea
 */
public class SquaredCirconference {

	private int centerX;
	private int centerY;
	private int radius;
	private int step;
	private List<Vector2i> circonference;
	
	public SquaredCirconference(int centerX, int centerY, int radius) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.step = 1;
		circonference = computeCirconference();
	}
	
	public SquaredCirconference(int centerX, int centerY, float radius, int squareSize) {
		/* Approximate radius to the bigger integer */
		this(centerX, centerY, (int) Math.ceil(radius/squareSize));
	}
	
	private ArrayList<Vector2i> computeCirconference() {
		Set<Vector2i> points = new HashSet<Vector2i>();
		int pointX, pointY;
		float distance;
		
		if (radius >= 1) {		
			for (int angle = 0; angle < 360; angle += step) {
				pointX = ((int) Math.round((MathUtils.cosDeg(angle) * radius))) + centerX;
				pointY = ((int) Math.round((MathUtils.sinDeg(angle) * radius))) + centerY;
				distance = Vector2i.dst(pointX, pointY, centerX, centerY);
				if ((distance > radius - 0.5) && (distance < radius + 0.5)) {
					points.add(new Vector2i(pointX, pointY));
				}
			}
		}
		return new ArrayList<Vector2i>(points);
	}

	/**
	 * @return A list containing all circonference's points
	 */
	public List<Vector2i> getCirconference() {
		return circonference;
	}
	
	@Override
	public String toString() {
		String str = new String();
		for (Vector2i v:circonference) {
			str = str.concat(v.toString() + ", ");
		}
		return str;
	}
	
	/**
	 * @return A list containing all circonference's points sorted by distance to a given point (nearest to farthest)
	 */
	public List<Vector2i> getCirconferenceByNearest(int x, int y) {
		Collections.sort(circonference, new Vector2iComparator(x, y));
		return circonference;
	}
	
	/**
	 * @return A list containing all circonference's points sorted by distance to a given point (farthest to nearest)
	 */
	public List<Vector2i> getCirconferenceByFarthest(int x, int y) {
		Collections.sort(circonference, Collections.reverseOrder(new Vector2iComparator(x, y)));
		return circonference;
	}

	
    /**
     * Utility to compare the distance of a point to another
     *
     * @author Andrea
     */
    public class Vector2iComparator implements Comparator<Vector2i> {

    	private int targetX;
    	private int targetY;
    	
    	public Vector2iComparator(int targetX, int targetY) {
    		this.targetX = targetX;
    		this.targetY = targetY;
    	}
    	
        @Override
        public int compare(Vector2i obj1, Vector2i obj2) {
            if (obj1 != null && obj2 != null){
                if (obj1.getDistance(targetX, targetY) > obj2.getDistance(targetX, targetY))
                    return 1;
                else
                    if (obj1.getDistance(targetX, targetY) == obj2.getDistance(targetX, targetY))
                        return 0;
            }
            return -1;
        }
    }
}