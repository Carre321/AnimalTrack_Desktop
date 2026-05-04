package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.AnimalCreateView;

public class AnimalUpdateController extends Controller {

    private AnimalCreateView view;

    public AnimalUpdateController(AnimalCreateView view) {
        super(view, "Actualizar",
                new ImageIcon(AnimalUpdateController.class
                        .getResource("/nuvola/32x32/1847_save_guardar_disk.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        if (view.updateAnimal()) {
            view.setEditable(false);
            view.setAgreeController(new AnimalSetEditableController(view));
        }
    }
}
