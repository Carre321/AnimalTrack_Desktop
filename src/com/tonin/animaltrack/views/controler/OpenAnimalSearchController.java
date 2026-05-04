package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.AnimalContainerView;

public class OpenAnimalSearchController extends AbstractAction {

	public OpenAnimalSearchController() {
	}

	public void doAction() {
		AnimalContainerView animalContainerView = new AnimalContainerView();
		MainWindow.getInstance().setView(animalContainerView);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		doAction();
		
	}


	
}
