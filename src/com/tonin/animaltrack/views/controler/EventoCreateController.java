package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.EventoCreateView;

public class EventoCreateController extends Controller {

    private EventoCreateView view;

    public EventoCreateController(EventoCreateView view) {
        super(view, "Guardar",
                new ImageIcon(EventoCreateController.class
                        .getResource("/animaltrack/icons/32/save.png")));
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
