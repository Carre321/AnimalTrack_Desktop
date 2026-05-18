package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.AnimalCreateView;

public class AnimalSetEditableController extends Controller {

    private AnimalCreateView view;

    public AnimalSetEditableController(AnimalCreateView view) {
        super(view, "Editar",
                new ImageIcon(AnimalSetEditableController.class
                        .getResource("/animaltrack/icons/32/edit.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        if (!MainWindow.getInstance().getPermissions().canEditAnimal()) {
            JOptionPane.showMessageDialog(view, "No tienes permisos para editar animales.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        view.setEditable(true);
        view.setAgreeController(new AnimalUpdateController(view));
    }
}
