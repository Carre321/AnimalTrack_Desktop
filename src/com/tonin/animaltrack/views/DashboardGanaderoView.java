package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import com.tonin.animaltrack.dao.criteria.EventoCriteria;
import com.tonin.animaltrack.model.dto.EventoDTO;
import com.tonin.animaltrack.service.EventoService;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.ui.MainWindow;

public class DashboardGanaderoView extends AbstractView implements FarmFilterAware {

    private static final int MESES_PRENEZ = 9;
    private static final int DIAS_PROXIMOS = 15;
    private static final String TIPO_INSEMINACION_ARTIFICIAL = "Inseminacion Artificial";
    private static final String TIPO_INSEMINACION_NATURAL = "Inseminacion Natural";
    private static final String TIPO_DIAGNOSTICO_PRENEZ = "Diagnostico Prenez";
    private static final String TIPO_REVISION_VETERINARIO = "Revision Veterinario";
    private static final String TIPO_PARTO = "Parto";

    private final EventoService eventoService;
    private final JLabel totalLabel;
    private final JLabel mientenLabel;
    private final JLabel proximasLabel;
    private final JLabel granjaLabel;
    private final JXTable table;

    private List<ProximoPartoRow> rows = Collections.emptyList();

    public DashboardGanaderoView() {
        this.eventoService = new EventoServiceImpl();
        this.totalLabel = new JLabel("0");
        this.mientenLabel = new JLabel("0");
        this.proximasLabel = new JLabel("0");
        this.granjaLabel = new JLabel();
        this.table = new JXTable();
        initialize();
        refreshForSelectedFarm();
    }

    private void initialize() {
        setName("Inicio");
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 247, 248));

        JPanel rootPanel = new JPanel(new BorderLayout(0, 14));
        rootPanel.setBackground(new Color(245, 247, 248));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(rootPanel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Vacas próximas a cumplir preñez");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        titleLabel.setForeground(new Color(0, 83, 95));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Última inseminación + revisión/diagnóstico, sin parto registrado después.");
        subtitleLabel.setForeground(new Color(86, 99, 105));
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Actualizar");
        refreshButton.setIcon(new ImageIcon(DashboardGanaderoView.class.getResource("/animaltrack/icons/32/refresh.png")));
        refreshButton.addActionListener(e -> refreshForSelectedFarm());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        rootPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setOpaque(false);
        rootPanel.add(summaryPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 14, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.gridy = 0;

        gbc.gridx = 0;
        summaryPanel.add(createStatCard("En la lista", totalLabel, new Color(0, 83, 95)), gbc);

        gbc.gridx = 1;
        summaryPanel.add(createStatCard("Mienten", mientenLabel, new Color(171, 74, 0)), gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        summaryPanel.add(createStatCard("Próximos 15 días", proximasLabel, new Color(34, 125, 70)), gbc);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 226, 229)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel tableTitle = new JLabel("Seguimiento reproductivo");
        tableTitle.setFont(tableTitle.getFont().deriveFont(Font.BOLD, 15f));
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        granjaLabel.setForeground(new Color(86, 99, 105));
        tableHeaderPanel.add(granjaLabel, BorderLayout.EAST);
        tablePanel.add(tableHeaderPanel, BorderLayout.NORTH);

        table.setColumnControlVisible(true);
        table.setSortable(true);
        table.setDefaultRenderer(Object.class, new StatusTableRenderer());
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1;
        summaryPanel.add(tablePanel, gbc);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 226, 229)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(86, 99, 105));
        panel.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));
        valueLabel.setForeground(accentColor);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void refreshForSelectedFarm() {
        rows = buildRows();
        updateSummary();
        updateTable();
    }

    private List<ProximoPartoRow> buildRows() {
        EventoCriteria criteria = new EventoCriteria();
        criteria.setGranjaId(MainWindow.getInstance().getSelectedGranjaId());

        List<EventoDTO> eventos = eventoService.findByCriteria(criteria);
        if (eventos == null || eventos.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<EventoDTO>> eventosPorAnimal = new LinkedHashMap<Long, List<EventoDTO>>();
        for (EventoDTO evento : eventos) {
            if (evento.getAnimalId() == null || evento.getFechaHora() == null || evento.getTipoEventoNombre() == null) {
                continue;
            }
            eventosPorAnimal.computeIfAbsent(evento.getAnimalId(), k -> new ArrayList<EventoDTO>()).add(evento);
        }

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(DIAS_PROXIMOS);
        List<ProximoPartoRow> result = new ArrayList<ProximoPartoRow>();

        for (List<EventoDTO> animalEventos : eventosPorAnimal.values()) {
            animalEventos.sort(Comparator.comparing(EventoDTO::getFechaHora));
            EventoDTO inseminacion = findLastInseminacion(animalEventos);
            if (inseminacion == null) {
                continue;
            }
            if (!hasConfirmacionAfter(animalEventos, inseminacion.getFechaHora())) {
                continue;
            }
            if (hasPartoAfter(animalEventos, inseminacion.getFechaHora())) {
                continue;
            }

            LocalDate finPrenez = inseminacion.getFechaHora().toLocalDate().plusMonths(MESES_PRENEZ);
            if (finPrenez.isAfter(maxDate)) {
                continue;
            }

            result.add(new ProximoPartoRow(inseminacion, findLastConfirmacionAfter(animalEventos, inseminacion.getFechaHora()),
                    finPrenez, ChronoUnit.DAYS.between(today, finPrenez)));
        }

        result.sort(Comparator.comparing(ProximoPartoRow::getFinPrenez)
                .thenComparing(ProximoPartoRow::getAnimalCrotal, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    private EventoDTO findLastInseminacion(List<EventoDTO> eventos) {
        EventoDTO last = null;
        for (EventoDTO evento : eventos) {
            if (isTipo(evento, TIPO_INSEMINACION_ARTIFICIAL) || isTipo(evento, TIPO_INSEMINACION_NATURAL)) {
                last = evento;
            }
        }
        return last;
    }

    private EventoDTO findLastConfirmacionAfter(List<EventoDTO> eventos, LocalDateTime fechaInseminacion) {
        EventoDTO last = null;
        for (EventoDTO evento : eventos) {
            if (isConfirmacion(evento) && evento.getFechaHora().isAfter(fechaInseminacion)) {
                last = evento;
            }
        }
        return last;
    }

    private boolean hasConfirmacionAfter(List<EventoDTO> eventos, LocalDateTime fechaInseminacion) {
        return findLastConfirmacionAfter(eventos, fechaInseminacion) != null;
    }

    private boolean isConfirmacion(EventoDTO evento) {
        return isTipo(evento, TIPO_DIAGNOSTICO_PRENEZ) || isTipo(evento, TIPO_REVISION_VETERINARIO);
    }

    private boolean hasPartoAfter(List<EventoDTO> eventos, LocalDateTime fechaInseminacion) {
        for (EventoDTO evento : eventos) {
            if (isTipo(evento, TIPO_PARTO) && evento.getFechaHora().isAfter(fechaInseminacion)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTipo(EventoDTO evento, String tipo) {
        return tipo.equalsIgnoreCase(evento.getTipoEventoNombre());
    }

    private void updateSummary() {
        int mienten = 0;
        int proximas = 0;
        for (ProximoPartoRow row : rows) {
            if (row.getDiasRestantes() < 0) {
                mienten++;
            } else {
                proximas++;
            }
        }

        totalLabel.setText(String.valueOf(rows.size()));
        mientenLabel.setText(String.valueOf(mienten));
        proximasLabel.setText(String.valueOf(proximas));
        granjaLabel.setText(MainWindow.getInstance().getSelectedGranjaId() == null ? "Todas las granjas" : "Granja seleccionada");
    }

    private void updateTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Estado", "Crotal", "Animal", "Inseminación", "Confirmación", "Fin preñez", "Días" }, 0) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        if (rows.isEmpty()) {
            model.addRow(new Object[] { "Sin avisos", "", "No hay vacas que cumplan preñez en los próximos 15 días.", "",
                    "", "", "" });
        } else {
            for (ProximoPartoRow row : rows) {
                model.addRow(new Object[] { row.getEstado(), row.getAnimalCrotal(), row.getAnimalNombre(),
                        row.getFechaInseminacion(), row.getFechaDiagnostico(), row.getFinPrenez(), row.getDiasTexto() });
            }
        }

        table.setModel(model);
        table.getColumnExt(0).setPreferredWidth(140);
        table.getColumnExt(1).setPreferredWidth(120);
        table.getColumnExt(2).setPreferredWidth(140);
        table.getColumnExt(3).setPreferredWidth(120);
        table.getColumnExt(4).setPreferredWidth(120);
        table.getColumnExt(5).setPreferredWidth(120);
        table.getColumnExt(6).setPreferredWidth(90);
    }

    private static class ProximoPartoRow {
        private final EventoDTO inseminacion;
        private final EventoDTO diagnostico;
        private final LocalDate finPrenez;
        private final long diasRestantes;

        ProximoPartoRow(EventoDTO inseminacion, EventoDTO diagnostico, LocalDate finPrenez, long diasRestantes) {
            this.inseminacion = inseminacion;
            this.diagnostico = diagnostico;
            this.finPrenez = finPrenez;
            this.diasRestantes = diasRestantes;
        }

        String getEstado() {
            if (diasRestantes < 0) {
                return "Miente " + Math.abs(diasRestantes) + " días";
            }
            if (diasRestantes == 0) {
                return "Termina hoy";
            }
            return "Faltan " + diasRestantes + " días";
        }

        String getAnimalCrotal() {
            return inseminacion.getAnimalCrotal();
        }

        String getAnimalNombre() {
            return inseminacion.getAnimalNombre();
        }

        LocalDate getFechaInseminacion() {
            return inseminacion.getFechaHora().toLocalDate();
        }

        LocalDate getFechaDiagnostico() {
            return diagnostico == null ? null : diagnostico.getFechaHora().toLocalDate();
        }

        LocalDate getFinPrenez() {
            return finPrenez;
        }

        long getDiasRestantes() {
            return diasRestantes;
        }

        String getDiasTexto() {
            return diasRestantes < 0 ? "+" + Math.abs(diasRestantes) : String.valueOf(diasRestantes);
        }
    }

    private static class StatusTableRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                return component;
            }

            String status = String.valueOf(table.getValueAt(row, 0));
            if (status.startsWith("Miente")) {
                component.setBackground(new Color(255, 244, 232));
                component.setForeground(new Color(109, 50, 0));
            } else if ("Termina hoy".equals(status)) {
                component.setBackground(new Color(255, 250, 222));
                component.setForeground(new Color(89, 72, 0));
            } else {
                component.setBackground(Color.WHITE);
                component.setForeground(new Color(33, 43, 48));
            }
            return component;
        }
    }
}
