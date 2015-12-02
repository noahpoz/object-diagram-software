package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

public class CodeArea extends JPanel implements Observer {

	private static int SELECTOR_PANEL_HEIGHT = 30;

	JComboBox<String> _classFileSelector;
	JTextArea _codeView;
	
	ArrayList<String>  _files;

	UI _ui;	

	public CodeArea(UI ui) {
		super();
		setLayout(null);
		_ui = ui;

		// ***** SET UP CLASS FILE SELECTOR *****
		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new FlowLayout());
		UI.formatJComponent(selectorPanel, new Dimension(UI.WINDOW_WIDTH / 2 - UI.PADDING * 2, SELECTOR_PANEL_HEIGHT), 
				UI.PADDING, UI.PADDING / 2);
		add(selectorPanel);

		JLabel selectorLabel = new JLabel("Select class file to view: ");
		selectorPanel.add(selectorLabel);
		
		_classFileSelector = new JComboBox<String>();
		_classFileSelector.setPreferredSize(new Dimension(200, SELECTOR_PANEL_HEIGHT));

		selectorPanel.add(_classFileSelector);

		// ***** SET UP CODE VIEWER *****
		_codeView = new JTextArea();
		_codeView.setEditable(false);
		_codeView.setFont(new Font("Courier", Font.PLAIN, 15));
		
		JScrollPane scrollPane = new JScrollPane(_codeView);
		UI.formatJComponent(scrollPane, new Dimension(UI.WINDOW_WIDTH / 2 - UI.PADDING * 2, 
				_ui.getPaneHeight() - SELECTOR_PANEL_HEIGHT - UI.PADDING * 3), 
				UI.PADDING, SELECTOR_PANEL_HEIGHT + UI.PADDING * 2);
		add(scrollPane);
		
		//so far, no class files have been provided by the model yet
		_files = new ArrayList<String>();
		
		_classFileSelector.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				@SuppressWarnings("unchecked")
				JComboBox<String> jcb = (JComboBox<String>) e.getSource();
				
				int index = jcb.getSelectedIndex();
				_codeView.setText(_files.get(index));
			}
		});
		
		update(null, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		
		// ***** Update the JComboBox to reflect the generated code *****
		_files = _ui.getModel().getCode();

		String[] fileSwitcher = new String[_files.size()];
		for(int i = 0; i < _files.size(); i++){
			if(i == _files.size()-1) {
				fileSwitcher[i] = "Driver";
			}
			else{
				for(int y = 0; y< _files.get(i).length(); y ++){
					if(_files.get(i).charAt(y) == '{'){
						fileSwitcher[i] = _files.get(i).substring(13, y);
						break;
					}
				}
			}
		}
		_classFileSelector.setModel(new DefaultComboBoxModel<String>(fileSwitcher));
		_classFileSelector.setSelectedIndex(0);
	}
}
