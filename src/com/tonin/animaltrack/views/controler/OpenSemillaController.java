package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.SemillaView;

public class OpenSemillaController extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public OpenSemillaController() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!MainWindow.getInstance().getPermissions().canOpenSemillas()) {
            JOptionPane.showMessageDialog(null, "No tienes permisos para abrir Semillas.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        MainWindow.getInstance().setView(new SemillaView());
    }
}
