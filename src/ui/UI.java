package ui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.*;

import model.Model;


public class UI implements Runnable {
	
	public static int WINDOW_WIDTH = 1200;
	public static int WINDOW_HEIGHT = 700;
	private int _paneHeight;
	public static int PADDING = 12;
	
	private JFrame _window;
	private JLayeredPane _mainContentPane;
	private CodeArea _codeView;
	private EditorPane _editPane;
	
	private Model _model;
	
	public static void formatJComponent(JComponent j, Dimension d, int x, int y) {
		j.setSize(d);
		j.setLocation(x, y);
	}
	
	public static AlphaComposite generateAlpha(float alpha) {
		//http://www.javaworld.com/article/2076733/java-se/antialiasing--images--and-alpha-compositing-in-java-2d.html?page=2
		AlphaComposite a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		return a;
	}
	
	public static void enableAntiAliasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	@Override
	public void run() {
		
		_model = new Model();
		
		//setting up main frame and main panel
		_window = new JFrame("Object Diagram Analysis");
		_mainContentPane = new JLayeredPane();
		_mainContentPane.setLayout(null);
		_window.setContentPane(_mainContentPane);
		_window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		_window.setResizable(false);
		_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_window.setVisible(true);
		_paneHeight = _window.getRootPane().getHeight();
		
		// *** SETTING UP CODE VIEWER
		_codeView = new CodeArea(this);
		UI.formatJComponent(_codeView, new Dimension(WINDOW_WIDTH / 2, WINDOW_HEIGHT), 0, 0);
		_mainContentPane.add(_codeView);
		
		// *** SETTING UP THE DIAGRAM EDITOR
		_editPane = new EditorPane(this);
		UI.formatJComponent(_editPane, new Dimension(WINDOW_WIDTH / 2 - PADDING * 2, _paneHeight - PADDING * 2), 
				WINDOW_WIDTH / 2 + PADDING, PADDING);
		_mainContentPane.add(_editPane);
		
		//add observers
		_model.addObserver(_codeView);
		
	}	
	
	public Model getModel() {
		return _model;
	}
	
	public int getPaneHeight() {
		return _paneHeight;
	}
	
	public Point getLocation() {
		return _window.getLocation();
	}
}
