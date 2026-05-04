package com.tonin.animaltrack.views;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.data.general.DefaultPieDataset;

import com.toedter.calendar.JDateChooser;

public class VacasPorRazaChart extends View {


	public VacasPorRazaChart() {
		initialize();	
	}

		private void initialize() {
			setLayout(new BorderLayout(0, 0));
			
			JPanel contentPanel = new JPanel();
			add(contentPanel);
			contentPanel.setLayout(new BorderLayout(0, 0));
			
			JPanel chartPanel = new JPanel();
			contentPanel.add(chartPanel, BorderLayout.CENTER);
			chartPanel.setLayout(new BorderLayout(0, 0));
			
			JPanel filterPanel = new JPanel();
			contentPanel.add(filterPanel, BorderLayout.NORTH);
			GridBagLayout gbl_filterPanel = new GridBagLayout();
			gbl_filterPanel.columnWidths = new int[]{0, 0};
			gbl_filterPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
			gbl_filterPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_filterPanel.rowWeights = new double[]{1.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
			filterPanel.setLayout(gbl_filterPanel);
			
			JLabel fechaDesdeLabel = new JLabel("Desde:");
			GridBagConstraints gbc_fechaDesdeLabel = new GridBagConstraints();
			gbc_fechaDesdeLabel.insets = new Insets(0, 0, 5, 0);
			gbc_fechaDesdeLabel.gridx = 0;
			gbc_fechaDesdeLabel.gridy = 0;
			filterPanel.add(fechaDesdeLabel, gbc_fechaDesdeLabel);
			
			JDateChooser fechaDesdeDateChooser = new JDateChooser();
			GridBagConstraints gbc_fechaDesdeDateChooser = new GridBagConstraints();
			gbc_fechaDesdeDateChooser.insets = new Insets(0, 0, 5, 0);
			gbc_fechaDesdeDateChooser.fill = GridBagConstraints.BOTH;
			gbc_fechaDesdeDateChooser.gridx = 0;
			gbc_fechaDesdeDateChooser.gridy = 1;
			filterPanel.add(fechaDesdeDateChooser, gbc_fechaDesdeDateChooser);
			
			JLabel fechaHastaLabel = new JLabel("Hasta:");
			GridBagConstraints gbc_fechaHastaLabel = new GridBagConstraints();
			gbc_fechaHastaLabel.insets = new Insets(0, 0, 5, 0);
			gbc_fechaHastaLabel.gridx = 0;
			gbc_fechaHastaLabel.gridy = 2;
			filterPanel.add(fechaHastaLabel, gbc_fechaHastaLabel);
			
			JDateChooser fechaHastaDateChooser = new JDateChooser();
			GridBagConstraints gbc_fechaHastaDateChooser = new GridBagConstraints();
			gbc_fechaHastaDateChooser.insets = new Insets(0, 0, 5, 0);
			gbc_fechaHastaDateChooser.fill = GridBagConstraints.BOTH;
			gbc_fechaHastaDateChooser.gridx = 0;
			gbc_fechaHastaDateChooser.gridy = 3;
			filterPanel.add(fechaHastaDateChooser, gbc_fechaHastaDateChooser);
			
			JButton ejecutarButton = new JButton("Buscar");
			ejecutarButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					Date fechaDesde = fechaDesdeDateChooser.getDate();
					Date fechaHasta = fechaHastaDateChooser.getDate();
					
					DefaultPieDataset dataset = new DefaultPieDataset();
					
				}
			});
			GridBagConstraints gbc_ejecutarButton = new GridBagConstraints();
			gbc_ejecutarButton.gridx = 0;
			gbc_ejecutarButton.gridy = 4;
			filterPanel.add(ejecutarButton, gbc_ejecutarButton);
		}

	}


