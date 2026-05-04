package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.VeterinarioContainerView;

public class OpenVeterinarioSearchController extends AbstractAction {

	public OpenVeterinarioSearchController() {
	}

	public void doAction() {
		VeterinarioContainerView veterinarioContainerView = new VeterinarioContainerView();
		MainWindow.getInstance().setView(veterinarioContainerView);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doAction();
	}
}
