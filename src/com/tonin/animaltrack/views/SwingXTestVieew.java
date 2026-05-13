package com.tonin.animaltrack.views;

import java.awt.EventQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JFrame;

public class SwingXTestVieew {

	private static Logger logger = LogManager.getLogger(SwingXTestVieew.class.getName());

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingXTestVieew window = new SwingXTestVieew();
					window.frame.setVisible(true);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SwingXTestVieew() {
		initialize();
		postInitialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void postInitialize() {
		frame.add(new SwingXTestView());
	}

}
