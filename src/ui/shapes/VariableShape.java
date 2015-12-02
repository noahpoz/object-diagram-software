package ui.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import dataset.VirtualClass;
import ui.EditorPane;
import ui.UI;

public class VariableShape extends Shape {

	public static double SIDE = 22;
	public boolean _hovering;
	public ObjectShape _reference;

	public VariableShape(double x, double y, EditorPane parent) {
		super(x, y, parent);
		_hovering = false;
	}

	//override draw
	@Override
	public void draw(Graphics2D g2, int state, boolean preliminary) {
		super.draw(g2, state, preliminary);
		g2.draw(new Rectangle2D.Double(_x - (SIDE * _parent.getScaling()) / 2, _y - (SIDE * _parent.getScaling()) / 2, 
				(SIDE * _parent.getScaling()), (SIDE * _parent.getScaling())));

		//shape objects must clean up after themselves
		g2.setComposite(UI.generateAlpha(1.0f));
	}

	public void drawName(Graphics2D g2) {
		float x = (float) _x;
		float y = (float) _y - ((float) (SIDE * _parent.getScaling()) / 2 + 7);
		super.drawName(g2, x, y);
	}

	@Override
	public double distanceFromCenter(double x, double y) {
		return Math.sqrt((double) (x - _x) * (x - _x) + (y - _y) * (y - _y));
	}

	@Override
	public boolean contains(double x, double y) {
		boolean goodX = x >= (_x - (SIDE * _parent.getScaling()) / 2) && x <= (_x + (SIDE * _parent.getScaling()) / 2);
		boolean goodY = y >= (_y - (SIDE * _parent.getScaling()) / 2) && y <= (_y + (SIDE * _parent.getScaling()) / 2);

		return goodX && goodY;
	}

	@Override
	public double getX() {
		return _x;
	}

	@Override
	public double getY() {
		return _y;
	}

	@Override
	public void setX(double x) {
		_x = x;	
	}

	@Override
	public void setY(double y) {
		_y = y;
	}

	@Override
	public String getType() {
		return "Variable";
	}

	@Override
	public void dispose() {
		_defunct = true;
	}

	@Override
	public boolean isDefunct() {
		return _defunct;
	}

	public ObjectShape getReference() {
		return _reference;
	}
}
