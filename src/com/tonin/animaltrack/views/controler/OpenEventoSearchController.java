package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.EventoContainerView;

public class OpenEventoSearchController extends AbstractAction {

	public OpenEventoSearchController() {
	}

	public void doAction() {
        if (!MainWindow.getInstance().getPermissions().canOpenEvents()) {
            JOptionPane.showMessageDialog(null, "No tienes permisos para abrir Eventos.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
		EventoContainerView eventoContainerView = new EventoContainerView();
		MainWindow.getInstance().setView(eventoContainerView);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doAction();
	}
}
