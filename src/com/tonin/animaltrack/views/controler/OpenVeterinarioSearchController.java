package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.VeterinarioContainerView;

public class OpenVeterinarioSearchController extends AbstractAction {

	public OpenVeterinarioSearchController() {
	}

	public void doAction() {
        if (!MainWindow.getInstance().getPermissions().canOpenVeterinarios()) {
            JOptionPane.showMessageDialog(null, "No tienes permisos para abrir Veterinarios.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
		VeterinarioContainerView veterinarioContainerView = new VeterinarioContainerView();
		MainWindow.getInstance().setView(veterinarioContainerView);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doAction();
	}
}
