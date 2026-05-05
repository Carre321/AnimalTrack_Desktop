package com.tonin.animaltrack.views.controler;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.tonin.animaltrack.views.AbstractView;

public abstract class Controller extends AbstractAction {
	
	private AbstractView view = null;
	
	public Controller(AbstractView view) {
		this(view, null);
	}

	public Controller(AbstractView view, String name) {
		this(view, name, null);
	}

	public Controller(AbstractView view, String name, Icon icon) {
		super(name, icon);
		this.view = view;
	}

	public AbstractView getView() {
		return this.view;
	}
	
	public abstract void doAction();
	
	

}
