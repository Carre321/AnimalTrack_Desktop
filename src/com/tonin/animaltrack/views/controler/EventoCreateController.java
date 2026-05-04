package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.EventoCreateView;

public class EventoCreateController extends Controller {

    private EventoCreateView view;

    public EventoCreateController(EventoCreateView view) {
        super(view, "Guardar",
                new ImageIcon(EventoCreateController.class
                        .getResource("/nuvola/32x32/1847_save_guardar_disk.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        view.createEvento();
    }
}
