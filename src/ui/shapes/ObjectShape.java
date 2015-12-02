package ui.shapes;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

import ui.EditorPane;
import ui.UI;

public class ObjectShape extends Shape {

	public static int RADIUS = 58;
	private ArrayList<VariableShape> _instanceVariables;
	
	public ObjectShape(double x, double y, EditorPane parent) {
		super(x, y, parent);
		_instanceVariables = new ArrayList<VariableShape>();
	}

	public void addInstanceVariable(VariableShape s) {
		if (! _instanceVariables.contains(s)) {
			_instanceVariables.add(s);
		}
	}
	
	public boolean isParentTo(VariableShape v) {
		return _instanceVariables.contains(v);
	}
	
	public void showChildren() {
		System.out.println(_instanceVariables);
	}
	
	//remove all defunct variables
	public void garbageCollect() {
		
		Iterator<VariableShape> i = _instanceVariables.iterator();
		while (i.hasNext()) {
			
			VariableShape v = i.next();
			if (v.isDefunct()) {
				i.remove();
			}
		}
	}
	
	//override draw
	@Override
	public void draw(Graphics2D g2, int state, boolean preliminary) {
		super.draw(g2, state, preliminary);
		g2.draw(new Ellipse2D.Double(_x - RADIUS * _parent.getScaling(), _y - RADIUS * _parent.getScaling(), 
				RADIUS * 2 * _parent.getScaling(), RADIUS * 2 * _parent.getScaling()));
		
		//shape objects must clean up after themselves
		g2.setComposite(UI.generateAlpha(1.0f));
	}
	
	public void drawName(Graphics2D g2) {
		float x = (float) _x;
		float y = (float) _y - (RADIUS * _parent.getScaling() + 5);
		super.drawName(g2, x, y);
	}

	@Override
	public double distanceFromCenter(double x, double y) {
		return Math.sqrt((double) (x - _x) * (x - _x) + (y - _y) * (y - _y));
	}

	@Override
	public boolean contains(double x, double y) {
		return (distanceFromCenter(x, y) <= RADIUS * _parent.getScaling());
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
		double difference = x - _x;
		for (VariableShape v : _instanceVariables) {
			v.setX(v.getX() + difference);
		}
		_x = x;
	}

	@Override
	public void setY(double y) {
		double difference = y - _y;
		for (VariableShape v : _instanceVariables) {
			v.setY(v.getY() + difference);
		}
		_y = y;
	}

	@Override
	public String getType() {
		return "Object";
	}

	@Override
	public void dispose() {
		_defunct = true;
		for (VariableShape v : _instanceVariables) {
			v.dispose();
		}
	}

	@Override
	public boolean isDefunct() {
		return _defunct;
	}
	
	public ArrayList<VariableShape> getInstanceVariables(){
		return _instanceVariables;
	}
}
