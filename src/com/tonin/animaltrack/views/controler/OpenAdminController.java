package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.AdminContainerView;

public class OpenAdminController extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public OpenAdminController() {
        super("Administración",
                new ImageIcon(OpenAdminController.class.getResource("/animaltrack/icons/32/admin.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!MainWindow.getInstance().getPermissions().canOpenAdmin()) {
            JOptionPane.showMessageDialog(null, "No tienes permisos para abrir Administración.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        MainWindow.getInstance().setView(new AdminContainerView());
    }
}
