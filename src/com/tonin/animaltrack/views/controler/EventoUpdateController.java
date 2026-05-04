package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.EventoCreateView;

public class EventoUpdateController extends Controller {

    private EventoCreateView view;

    public EventoUpdateController(EventoCreateView view) {
        super(view, "Actualizar",
                new ImageIcon(EventoUpdateController.class
                        .getResource("/nuvola/32x32/1847_save_guardar_disk.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        if (view.updateEvento()) {
            view.setEditable(false);
            view.setAgreeController(new EventoSetEditableController(view));
        }
    }
}
