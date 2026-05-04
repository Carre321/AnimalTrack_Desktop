package com.tonin.animaltrack.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXDatePicker;

public class SwingXTestView extends View {

	private static final long serialVersionUID = 1L;
	private JXComboBox razaCB;


	public SwingXTestView() {
		initialize();
		postInitialize();
	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		
		JXHyperlink hprlnkNuevo = new JXHyperlink();
		hprlnkNuevo.setText("Nuevo...");
		centerPanel.add(hprlnkNuevo);
		
		JXFindBar findBar = new JXFindBar();
		centerPanel.add(findBar);
		
		razaCB = new JXComboBox(new String[] {"Rubia Galega", 
											"Frisona", "Parda Alpina",
											"Bruna Alpina","Asturiana de los Valles"});
		centerPanel.add(razaCB);
		
		JXTaskPane taskPane = new JXTaskPane();
		centerPanel.add(taskPane);
		
		JXTitledPanel titledPanel = new JXTitledPanel();
		titledPanel.setTitle(" HOLA");
		centerPanel.add(titledPanel);
		
		JXButton button = new JXButton();
		centerPanel.add(button);
		
		JXMonthView monthView = new JXMonthView();
		centerPanel.add(monthView);
		
		JXDatePicker datePicker = new JXDatePicker();
		centerPanel.add(datePicker);
	}
	
	private void postInitialize() {
		FilterableComboBoxSupport.decorate(razaCB);
	}

}
