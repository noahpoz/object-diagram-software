package ui;

import java.awt.*;
import java.awt.geom.Line2D;

import ui.shapes.*;

public class Reference {

	private static double PI = 3.14159265359;
	
	private Graphics2D _g2;
	private double _arrowSize;
	private float _thickness;
	private EditorPane _parent;

	public Reference(Graphics2D g2, double arrowSize, float thickness, EditorPane parent) {
		_g2 = g2;
		_parent = parent;
		_arrowSize = arrowSize * _parent.getScaling();
		_thickness = thickness * _parent.getScaling();
	}

	public void drawArrow(VariableShape origin, ObjectShape target, boolean targeting) {
		UI.enableAntiAliasing(_g2);

		_g2.setColor(Color.BLACK);
		BasicStroke solid = new BasicStroke(_thickness);
		_g2.setStroke(solid);

		double deltaX = (origin.getX() - target.getX());
		double deltaY = -(origin.getY() - target.getY());

		double angleCompletion = 0;
		if (deltaX > 0) {
			angleCompletion = PI;
		}

		double angle = Math.atan(deltaY / deltaX) + angleCompletion;

		double targetX;
		double targetY; 

		if (!targeting) {
			targetX = (Math.cos(PI - angle) * ObjectShape.RADIUS * _parent.getScaling()) + target.getX();
			targetY = (Math.sin(PI - angle) * ObjectShape.RADIUS * _parent.getScaling()) + target.getY();
		} else {
			targetX = target.getX();
			targetY = target.getY();
		}

		//draw arrow shaft
		_g2.draw(new Line2D.Double(origin.getX(), origin.getY(), 
				targetX, targetY));

		//draw right arrow head
		double rotatedEndPointX = (Math.cos(PI - angle - (PI / 6))) * _arrowSize;
		double rotatedEndPointY = (Math.sin(PI - angle - (PI / 6))) * _arrowSize;
		_g2.draw(new Line2D.Double(targetX, targetY, rotatedEndPointX + targetX, rotatedEndPointY + targetY));

		//draw left arrow head
		rotatedEndPointX = (Math.cos(PI - angle + (PI / 6))) * _arrowSize;
		rotatedEndPointY = (Math.sin(PI - angle + (PI / 6))) * _arrowSize;
		_g2.draw(new Line2D.Double(targetX, targetY, rotatedEndPointX + targetX, rotatedEndPointY + targetY));
	}
}
