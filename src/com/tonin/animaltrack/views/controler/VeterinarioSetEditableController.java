package com.tonin.animaltrack.views.controler;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.tonin.animaltrack.views.VeterinarioCreateView;

public class VeterinarioSetEditableController extends Controller {

    private VeterinarioCreateView view;

    public VeterinarioSetEditableController(VeterinarioCreateView view) {
        super(view, "Editar",
                new ImageIcon(VeterinarioSetEditableController.class
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
        view.setAgreeController(new VeterinarioUpdateController(view));
    }
}
