package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.EventoCreateView;

public class EventoSetEditableController extends Controller {

    private EventoCreateView view;

    public EventoSetEditableController(EventoCreateView view) {
        super(view, "Editar",
                new ImageIcon(EventoSetEditableController.class
                        .getResource("/animaltrack/icons/32/edit.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        if (!MainWindow.getInstance().getPermissions().canEditEvento()) {
            JOptionPane.showMessageDialog(view, "No tienes permisos para editar eventos.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        view.setEditable(true);
        view.setAgreeController(new EventoUpdateController(view));
    }
}
