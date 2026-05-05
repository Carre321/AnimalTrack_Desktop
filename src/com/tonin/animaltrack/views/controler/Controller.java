package com.tonin.animaltrack.views.controler;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.tonin.animaltrack.views.AbstractView;

/*
 * Interface comun para os controladores, que são responsáveis por realizar as ações dos botões e outros componentes da interface gráfica.
 */

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
