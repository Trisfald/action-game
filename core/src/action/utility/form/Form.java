package action.utility.form;

import action.combat.HitLevel;
import action.utility.form.CircleForm.CircleFormLoader;
import action.utility.form.RectangleForm.RectangleFormLoader;
import action.utility.geom.Shape;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Interface that defines the form of an entity useful for collisions
 * 
 * @author Andrea
 *
 */
public interface Form {
	
	public FormType getType();
	
	public float getWidth();

	public float getHeight();

	public boolean isHittable(HitLevel level);

	public float getPerspectiveRatio();

	public float getPerspectiveHeight();

	public Shape getPerspectiveShape(float x, float y);

	public Shape getShape(float x, float y);

	public float getCenterX(float x);

	public float getCenterY(float y);

	public float getPerspectiveCenterY(float y);

	public float getSize();
	
	/**
	 * Set the size of this form
	 */
	public void setSize(float n);

	/**
	 * @return The value of radius that in average represents this entity
	 */
	public float getAverageRadius();

	public void rotate(Float angle);
	
	
	/**
	 * @author Andrea
	 */
	public static abstract class FormLoader {
		
		public final FormType type;
		public final HitLevel hitLevel;

		public FormLoader(FormType type, HitLevel hitLevel) {
			this.type = type;
			this.hitLevel = hitLevel;
		}
		
		public abstract float getWidth();
		
		public abstract float getHeight(); 
		
		/**
		 * Instantiate and return a form created by this loader
		 */
		public abstract Form load();
		
		public static FormLoader loadFromXml(Element element) {
			switch(FormType.valueOf(element.getAttribute("type"))) {
				case RECTANGLE:
					return RectangleFormLoader.loadFromXML(element);
				case CIRCLE:
					return CircleFormLoader.loadFromXML(element);
				default:
					return null;
			}
		}
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public enum FormType {
		
		RECTANGLE,
		CIRCLE;
		
	}
	

}
