package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class NameEditor extends JFrame {
	
	private EnterListener _enterListener;
	private String _contents;
	private NameEditor _self;
	
	public NameEditor(String type, EnterListener e) {
		super();
		_self = this;
		_enterListener = e;
		
		setTitle("Set " + type + " name:");
		setSize(WIDTH, HEIGHT);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		setContentPane(panel);
		
		JTextField field = new JTextField();
		panel.add(field);
		field.setPreferredSize(new Dimension(200, 50));
		
		pack();
		
		field.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				//[enter] = 10
				if (e.getKeyCode() == 10) {
					_enterListener.enterPressed(field.getText());
					_self.dispose();
				}
			}
		});
	}
	
	public String getContents() {
		return _contents;
	}
}
