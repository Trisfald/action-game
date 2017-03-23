package action.utility.form;

import action.combat.HitLevel;
import action.utility.geom.Circle;
import action.utility.geom.Shape;

import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Form based on a circle
 * 
 * @author Andrea
 *
 */
public class CircleForm implements Form {

	/** Circle that represents the form */
	private Circle shape;
    private HitLevel hitLevel;
	
	public CircleForm(CircleFormLoader loader) {
		shape = new Circle(0, 0, loader.radius);
		this.hitLevel = loader.hitLevel;
	}
	
	@Override
	public FormType getType() {
		return FormType.CIRCLE;
	}
	
	@Override
	public float getWidth() {
		return shape.getWidth();
	}

	@Override
	public float getHeight() {
		return shape.getHeight();
	}

	@Override
	public boolean isHittable(HitLevel level) {
		return (hitLevel.value() <= level.value());
	}

	@Override
	public float getPerspectiveRatio() {
		return 0;
	}

	@Override
	public float getPerspectiveHeight() {
		return getHeight();
	}

	@Override
	public Shape getPerspectiveShape(float x, float y) {
		return getShape(x, y);
	}

	@Override
	public Shape getShape(float x, float y) {
		shape.setLocation(x, y);
		return shape;
	}

	@Override
	public float getCenterX(float x) {
		shape.setX(x);
		return shape.getCenterX();
	}

	@Override
	public float getCenterY(float y) {
		shape.setY(y);
		return shape.getCenterY();
	}

	@Override
	public float getPerspectiveCenterY(float y) {
		return getCenterY(y);
	}

	@Override
	public float getSize() {
		return shape.getRadius() * 2;
	}
	
	@Override
	public void setSize(float n) {
		shape.setRadius(n / 2);
	}

	@Override
	public float getAverageRadius() {
		return shape.getRadius();
	}

	@Override
	public void rotate(Float angle) {
		
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class CircleFormLoader extends FormLoader {

		public final float radius;
		
		public CircleFormLoader(FormType type, HitLevel hitLevel, float radius) {
			super(type, hitLevel);
			this.radius = radius;
		}

		@Override
		public float getWidth() {
			return radius * 2;
		}

		@Override
		public float getHeight() {
			return radius * 2;
		}
		
		@Override
		public Form load() {
			return new CircleForm(this);
		}
		
		public static FormLoader loadFromXML(Element element) {
			return new CircleFormLoader(
					FormType.valueOf(element.getAttribute("type")),
					HitLevel.valueOf(element.getAttribute("hitlevel")),
					element.getFloatAttribute("radius")
					);
		}
		
	}

}
