package com.tonin.animaltrack.views;

import org.jdesktop.swingx.JXTitledPanel;

public abstract class AbstractView extends JXTitledPanel implements View {

    private String name = null;

    public AbstractView() {
    }

    public AbstractView(String name) {
        setName(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setTitle(name);
    }
}
