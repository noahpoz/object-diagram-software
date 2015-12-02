package ui.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.UIManager;

import ui.EditorPane;
import ui.UI;

public abstract class Shape {

	protected boolean _defunct;

	protected double _x;
	protected double _y;

	protected int _alphaOscillation;
	protected int _alphaDirection;

	protected String _name;
	
	protected EditorPane _parent;

	public Shape(double x, double y, EditorPane parent) {
		_x = x;
		_y = y;

		_alphaOscillation = 0;
		_alphaDirection = 2;

		_name = "untitled";
		_parent = parent;
	}

	public void draw(Graphics2D g2, int state, boolean preliminary) {

		UI.enableAntiAliasing(g2);

		if (state == 0) {
			g2.setColor(Color.BLACK); //inactive
		} else if (state == 1) {
			g2.setColor(Color.GREEN);   //active
		} else if (state ==2) {
			g2.setColor(new Color(148,0,211));  //targeted [purple]
		} else if (state ==3) {
			g2.setColor(Color.RED); //superimposed
		}

		if (preliminary) {
			//set translucency level
			//http://www.javaworld.com/article/2076733/java-se/antialiasing--images--and-alpha-compositing-in-java-2d.html?page=2
			g2.setComposite(UI.generateAlpha(_alphaOscillation / 100.0f));
			_alphaOscillation += _alphaDirection;
			if (_alphaOscillation > 98) {
				_alphaDirection = -2;
			} else if (_alphaOscillation < 2) {
				_alphaDirection = 2;
			}
		}

		BasicStroke solid = new BasicStroke(2.5f * _parent.getScaling());
		g2.setStroke(solid);
	}

	public void drawName(Graphics2D g2, float x, float y) {

		//http://stackoverflow.com/questions/6416201/how-to-draw-string-with-background-on-graphics
		g2.setFont(new Font("Sans Serif", Font.PLAIN, (int) (16 * _parent.getScaling())));
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(_name, g2);

		g2.setColor(UIManager.getColor("Panel.background"));
		g2.fillRect((int) x, (int) ((y - fm.getAscent())), 
				(int) (rect.getWidth()), (int) (rect.getHeight()));
		
		g2.setColor(Color.BLUE);
		g2.drawString(_name, x, y);
	}

	public boolean isTouching(Shape s) {

		if (!this.equals(s)) {

			//distance between centers of any two shapes can be used universally
			double deltaX = (this.getX() - s.getX());
			double deltaY = (this.getY() - s.getY());
			double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

			if (this.getType().equals("Object") && s.getType().equals("Object")) {
				if (distance <= ObjectShape.RADIUS * 2 * _parent.getScaling()) {
					return true;
				}
			}

			if (!this.getType().equals(s.getType())) {
				if (distance <= (ObjectShape.RADIUS * _parent.getScaling() + 
						(VariableShape.SIDE * _parent.getScaling()) / 2)) {
					return true;
				}
			}

			if (this.getType().equals("Variable") && s.getType().equals("Variable")) {
				boolean xInRange = (Math.abs((double) (this.getX() - s.getX())) < (VariableShape.SIDE * _parent.getScaling()));
				boolean yInRange = (Math.abs((double) (this.getY() - s.getY())) < (VariableShape.SIDE * _parent.getScaling()));
				if (xInRange && yInRange) {
					return true;
				}
			}
		}
		return false;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public abstract double distanceFromCenter(double x, double y);
	public abstract boolean contains(double x, double y);

	public abstract double getX();
	public abstract double getY();

	public abstract void setX(double x);
	public abstract void setY(double y);

	public abstract String getType();

	public abstract void dispose();

	public boolean isDefunct() {
		return _defunct;
	}
}
