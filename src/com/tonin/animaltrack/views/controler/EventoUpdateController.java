package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.EventoCreateView;

public class EventoUpdateController extends Controller {

    private EventoCreateView view;

    public EventoUpdateController(EventoCreateView view) {
        super(view, "Actualizar",
                new ImageIcon(EventoUpdateController.class
                        .getResource("/animaltrack/icons/32/save.png")));
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
