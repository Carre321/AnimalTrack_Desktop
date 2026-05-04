package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.AnimalCreateView;

public class AnimalSetEditableController extends Controller {

    private AnimalCreateView view;

    public AnimalSetEditableController(AnimalCreateView view) {
        super(view, "Editar",
                new ImageIcon(AnimalSetEditableController.class
                        .getResource("/nuvola/32x32/1819_pencil_pencil.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        view.setEditable(true);
        view.setAgreeController(new AnimalUpdateController(view));
    }
}
