package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.VeterinarioCreateView;

public class VeterinarioUpdateController extends Controller {

    private VeterinarioCreateView view;

    public VeterinarioUpdateController(VeterinarioCreateView view) {
        super(view, "Actualizar",
                new ImageIcon(VeterinarioUpdateController.class
                        .getResource("/animaltrack/icons/32/save.png")));
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doAction();
    }

    @Override
    public void doAction() {
        if (view.updateVeterinario()) {
            view.setEditable(false);
            view.setAgreeController(new VeterinarioSetEditableController(view));
        }
    }
}
