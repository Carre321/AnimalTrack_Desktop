package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.DashboardGanaderoView;

public class OpenInicioController extends AbstractAction {

    public OpenInicioController() {
    }

    public void doAction() {
        if (!MainWindow.getInstance().getPermissions().canOpenHome()) {
            JOptionPane.showMessageDialog(null, "No tienes permisos para abrir Inicio.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        MainWindow.getInstance().setView(new DashboardGanaderoView());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }
}
