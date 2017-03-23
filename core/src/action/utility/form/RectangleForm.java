package action.utility.form;

import action.combat.HitLevel;
import action.interfaces.GlobalVar;
import action.utility.enums.Direction;
import action.utility.geom.Rectangle;
import action.utility.geom.Shape;
import action.utility.geom.Transform;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Form based on a square
 * 
 * @author Andrea
 *
 */
public class RectangleForm implements Form {

	private float width;
	private float height;
    /** Starting X of the center */
    private float startCX;
    /** Starting Y of the center */
    private float startCY;
    /** General purpose collision shape */
    private Shape shape;
    /** Original shape in rectangle form */
    private Rectangle rShape;
    /** Collision shape used against tiles of the map */
    private Shape shapePerspective;
    /** Original perspective shape in rectangle form */
    private Rectangle rShapeP;
    /** Tells how much % of the total height is used for tiles collisions */
    private float perspectiveRatio;
    /** Angle of the shape */
    private float angle;
    private HitLevel hitLevel;

	public RectangleForm(RectangleFormLoader info) {
		this(info, Direction.DOWN.getShapeAngle());
	}
	
	/**
	 * Creates a rotated form
	 * @param angle Angle in degrees
	 */
	public RectangleForm(RectangleFormLoader info, float angle) {
		this.width = info.width;
		this.height = info.height;
		this.perspectiveRatio = info.perspectiveRatio;
		this.angle = angle;
		this.hitLevel = info.hitLevel;
		loadShapes();
		computeShapes();
	}
	
	private void loadShapes() {
		rShape = new Rectangle(0, 0, width, height);
		rShapeP = new Rectangle(0, 0 + height * perspectiveRatio, width, height * (1 - perspectiveRatio));
		startCX = rShape.getCenterX();
		startCY = rShape.getCenterY();
	}
	
	@Override
	public FormType getType() {
		return FormType.RECTANGLE;
	}
	
	@Override
	public float getWidth() {
        return width;
    }
    
	@Override
	public float getHeight() {
        return height;
    }
	
	@Override
	public boolean isHittable(HitLevel level) {
		return (hitLevel.value() <= level.value());
	}
	
	@Override
	public float getPerspectiveRatio() {
		return perspectiveRatio;
	}
	
	@Override
	public float getPerspectiveHeight() {
    	return height * perspectiveRatio;
    }
	
	@Override
	public Shape getPerspectiveShape(float x, float y) {
        shapePerspective.setLocation(x, y - (shape.getHeight()-shapePerspective.getHeight()));
        return shapePerspective;
    }
   
	@Override
	public Shape getShape(float x, float y) {
        shape.setLocation(x, y);
        return shape;
    }
	
	@Override
	public float getCenterX(float x) {
		/* For forms without rotation use a quick formula */
		if (angle == 0)
			return (x + width / 2);
		/* For other forms look in the rotated shape */
		shape.setX(x);
		return shape.getCenterX();
    }
    
	@Override
	public float getCenterY(float y) {
		/* For forms without rotation use a quick formula */
		if (angle == 0)
			return (y + height / 2);
		/* For other forms look in the rotated shape */
		shape.setY(y);
		return shape.getCenterY();
    }
	
	@Override
	public float getPerspectiveCenterY(float y) {
		shapePerspective.setY(y - (shape.getHeight()-shapePerspective.getHeight()));
		return shapePerspective.getCenterY();
    }
	
	@Override
	public void setSize(float n) {
		height = n;
		rShape.setHeight(n);
		rShapeP.setHeight(n);
		computeShapes();
	}

	private void computeShapes() {
		shape = rShape.transform(Transform.createRotateTransform(MathUtils.degreesToRadians*angle, startCX, startCY));
		shapePerspective = rShapeP.transform(Transform.createRotateTransform(MathUtils.degreesToRadians*angle, startCX, startCY));
	}
	
	/**
	 * @return The value of radius that in average represents this entity
	 */
	@Override
	public float getAverageRadius() {
		return (height + width) / 4;
	}
	
	@Override
	public void rotate(Float angle) {
		angle += GlobalVar.SHAPE_IMG_ANGLE_OFFSET;
		if (angle == this.angle)
			return;
		
		this.angle = angle;
		computeShapes();
	}
	
	@Override
	public float getSize() {
		return getHeight();
	}
	
	
	/**
	 * @author Andrea
	 */
	public static class RectangleFormLoader extends FormLoader {
		
		public final float width;
		public final float height;
		public final float perspectiveRatio;
		
		public RectangleFormLoader(FormType type, HitLevel hitLevel, float width, float height, float perspectiveRatio) {
			super(type, hitLevel);
			this.width = width;
			this.height = height;
			this.perspectiveRatio = perspectiveRatio;
		}

		@Override
		public float getWidth() {
			return width;
		}

		@Override
		public float getHeight() {
			return height;
		}
		
		@Override
		public Form load() {
			return new RectangleForm(this);
		}
		
		public static FormLoader loadFromXML(Element element) {
			return new RectangleFormLoader(
					FormType.valueOf(element.getAttribute("type")),
					HitLevel.valueOf(element.getAttribute("hitlevel")),
					element.getFloatAttribute("width"),
	    			element.getFloatAttribute("height"),
	    			element.getFloatAttribute("perspective", GlobalVar.DEFAULT_PERSPECTIVE_RATIO)
					);
		}
			
	}
	
}
