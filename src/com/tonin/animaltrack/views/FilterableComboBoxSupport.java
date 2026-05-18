package com.tonin.animaltrack.views;

import java.awt.Component;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

public final class FilterableComboBoxSupport {

    private FilterableComboBoxSupport() {
    }

    public static <T> void decorate(JComboBox<T> combo) {
        combo.setEditable(true);

        GenericToStringConverter converter = new GenericToStringConverter();
        List<T> allItems = new ArrayList<T>();
        boolean[] filtering = new boolean[] { false };
        boolean[] applyingText = new boolean[] { false };

        loadItems(combo, allItems);
        combo.addPropertyChangeListener("model", e -> {
            if (!filtering[0]) {
                loadItems(combo, allItems);
            }
        });
        combo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                filterModel(combo, allItems, converter, filtering, applyingText, false);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        Component editorComponent = combo.getEditor().getEditorComponent();
        if (!(editorComponent instanceof JTextComponent)) {
            return;
        }

        JTextComponent textComponent = (JTextComponent) editorComponent;
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleFilter();
            }

            private void scheduleFilter() {
                if (applyingText[0] || filtering[0]) {
                    return;
                }
                SwingUtilities.invokeLater(() -> filterModel(combo, allItems, converter, filtering, applyingText, true));
            }
        });
    }

    public static Object getSelectedItem(JComboBox<?> combo) {
        if (combo == null) {
            return null;
        }
        Object selectedItem = combo.getSelectedItem();
        if (!combo.isEditable()) {
            return selectedItem;
        }

        Object editorItem = combo.getEditor() == null ? null : combo.getEditor().getItem();
        String editorText = trimToNull(editorItem == null ? null : editorItem.toString());
        if (editorText == null) {
            return null;
        }

        if (selectedItem != null && editorText.equals(selectedItem.toString())) {
            return selectedItem;
        }

        ComboBoxModel<?> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Object item = model.getElementAt(i);
            if (item != null && editorText.equals(item.toString())) {
                combo.setSelectedIndex(i);
                return item;
            }
        }
        return null;
    }

    private static <T> void loadItems(JComboBox<T> combo, List<T> allItems) {
        allItems.clear();
        ComboBoxModel<T> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            allItems.add(model.getElementAt(i));
        }
    }

    private static <T> void filterModel(JComboBox<T> combo, List<T> allItems, GenericToStringConverter converter,
            boolean[] filtering, boolean[] applyingText, boolean updatePopupVisibility) {
        Object editorItem = combo.getEditor().getItem();
        String text = editorItem == null ? "" : editorItem.toString();
        Object selectedItem = combo.getSelectedItem();
        String normalizedText = shouldIgnoreAsFilter(selectedItem, text, converter) ? "" : normalize(text);

        DefaultComboBoxModel<T> filteredModel = buildPrioritizedModel(allItems, converter, normalizedText);

        filtering[0] = true;
        applyingText[0] = true;
        try {
            combo.setModel(filteredModel);
            if (containsItem(filteredModel, selectedItem)
                    && converter.getPreferredStringForItem(selectedItem).equals(text)) {
                combo.setSelectedItem(selectedItem);
            } else {
                combo.getEditor().setItem(text);
            }
            if (updatePopupVisibility && combo.isShowing()) {
                boolean userIsEditing = combo.hasFocus() || combo.getEditor().getEditorComponent().hasFocus();
                boolean hasTypedFilter = !normalize(text).isEmpty() && !shouldIgnoreAsFilter(selectedItem, text, converter);
                if (userIsEditing && hasTypedFilter && filteredModel.getSize() > 0) {
                    combo.setPopupVisible(true);
                } else {
                    combo.setPopupVisible(false);
                }
            }
        } finally {
            applyingText[0] = false;
            filtering[0] = false;
        }
    }

    private static <T> DefaultComboBoxModel<T> buildPrioritizedModel(List<T> allItems,
            GenericToStringConverter converter, String normalizedText) {
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<T>();
        if (normalizedText.isEmpty()) {
            for (T item : allItems) {
                model.addElement(item);
            }
            return model;
        }

        List<T> fixedItems = new ArrayList<T>();
        List<T> startsWithMatches = new ArrayList<T>();
        List<T> containsMatches = new ArrayList<T>();
        List<T> otherItems = new ArrayList<T>();

        for (T item : allItems) {
            String itemText = normalize(converter.getPreferredStringForItem(item));
            if (isFixedOption(item, itemText)) {
                fixedItems.add(item);
            } else if (itemText.startsWith(normalizedText)) {
                startsWithMatches.add(item);
            } else if (itemText.contains(normalizedText)) {
                containsMatches.add(item);
            } else {
                otherItems.add(item);
            }
        }

        addAll(model, fixedItems);
        addAll(model, startsWithMatches);
        addAll(model, containsMatches);
        addAll(model, otherItems);
        return model;
    }

    private static <T> void addAll(DefaultComboBoxModel<T> model, List<T> items) {
        for (T item : items) {
            model.addElement(item);
        }
    }

    private static <T> boolean containsItem(DefaultComboBoxModel<T> model, Object selectedItem) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i) == selectedItem) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldIgnoreAsFilter(Object selectedItem, String text, GenericToStringConverter converter) {
        String normalizedText = normalize(text);
        if ("todos".equals(normalizedText) || "todas".equals(normalizedText)
                || "sin seleccionar".equals(normalizedText)) {
            return true;
        }
        if (selectedItem == null) {
            return false;
        }
        String selectedText = converter.getPreferredStringForItem(selectedItem);
        if (!selectedText.equals(text)) {
            return false;
        }
        if (hasNullValue(selectedItem)) {
            return true;
        }
        return false;
    }

    private static boolean isFixedOption(Object item, String normalizedText) {
        return hasNullValue(item) || "todos".equals(normalizedText) || "todas".equals(normalizedText)
                || "sin seleccionar".equals(normalizedText);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean hasNullValue(Object item) {
        try {
            java.lang.reflect.Method method = item.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return method.invoke(item) == null;
        } catch (Exception ex) {
            return false;
        }
    }
}
