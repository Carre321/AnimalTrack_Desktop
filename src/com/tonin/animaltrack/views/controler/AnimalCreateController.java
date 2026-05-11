package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.AnimalCreateView;

public class AnimalCreateController extends Controller {

    private AnimalCreateView view;

    public AnimalCreateController(AnimalCreateView view) {
        super(view, "Guardar",
                new ImageIcon(AnimalCreateController.class
                        .getResource("/animaltrack/icons/32/save.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        view.createAnimal();
    }
}
