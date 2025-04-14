/*
 * Copyright (c) 2023  Marco Marini, marco.marini@mmarini.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *    END OF TERMS AND CONDITIONS
 *
 */

package org.mmarini.swing;


import io.reactivex.rxjava3.schedulers.Schedulers;
import org.mmarini.qucomp.swing.Messages;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Swing utility functions
 */
public interface SwingUtils {
    /**
     * Returns the initialized button
     *
     * @param key the message key
     */
    static JButton createButton(String key) {
        JButton button = new JButton(Messages.getString(key + ".name"));

        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(button::setIcon);
        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(button::setMnemonic);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(button::setToolTipText);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(button::setSelectedIcon);
        return button;
    }

    /**
     * Center the window on screen
     *
     * @param window the window
     */
    static void centerOnScreen(Window window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - window.getWidth()) / 2;
        int y = (screen.height - window.getHeight()) / 2;
        window.setLocation(x, y);
    }

    /**
     * Returns the initialized checkbox menu item
     *
     * @param key the message key
     */
    static JCheckBoxMenuItem createCheckBoxMenuItem(String key) {
        String name = Messages.getString(key + ".name");
        JCheckBoxMenuItem menu = new JCheckBoxMenuItem(name);
        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(menu::setMnemonic);
        Messages.getStringOpt(key + ".accelerator")
                .map(KeyStroke::getKeyStroke)
                .ifPresent(menu::setAccelerator);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(menu::setToolTipText);
        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(menu::setIcon);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(menu::setSelectedIcon);
        //menu::setSelectedIcon);
        return menu;
    }

    /**
     * Returns the initialized menu item
     *
     * @param key the message key
     */
    static JMenu createMenu(String key) {
        String name = Messages.getString(key + ".name");
        JMenu menu = new JMenu(name);
        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(menu::setMnemonic);
        Messages.getStringOpt(key + ".accelerator")
                .map(KeyStroke::getKeyStroke)
                .ifPresent(menu::setAccelerator);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(menu::setToolTipText);
        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(menu::setIcon);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(menu::setSelectedIcon);
        return menu;
    }

    /**
     * Returns the initialized menu item
     *
     * @param key the message key
     */
    static JMenuItem createMenuItem(String key) {
        String name = Messages.getString(key + ".name");
        JMenuItem menu = new JMenuItem(name);
        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(menu::setMnemonic);
        Messages.getStringOpt(key + ".accelerator")
                .map(KeyStroke::getKeyStroke)
                .ifPresent(menu::setAccelerator);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(menu::setToolTipText);
        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(menu::setIcon);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(menu::setSelectedIcon);
        return menu;
    }

    /**
     * Returns the initialized toolbar button
     *
     * @param key the message key
     */
    static JButton createToolBarButton(String key) {
        JButton button = new JButton();
        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresentOrElse(button::setIcon,
                        () -> button.setText(Messages.getString(key + ".name")));

        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(button::setMnemonic);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(button::setToolTipText);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(button::setSelectedIcon);
        return button;
    }

    /**
     * Returns the initialized toolbar toggle button
     *
     * @param key the message key
     */
    static JToggleButton createToolBarToggleButton(String key) {
        JToggleButton button = new JToggleButton();
        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresentOrElse(button::setIcon,
                        () -> button.setText(Messages.getString(key + ".name")));

        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(button::setMnemonic);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(button::setToolTipText);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(button::setSelectedIcon);
        return button;
    }

    /**
     * Returns a checkbox
     *
     * @param key the key
     */
    static JCheckBox createCheckBox(String key) {
        JCheckBox button = new JCheckBox(Messages.getString(key + ".name"));

        Messages.getStringOpt(key + ".icon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(button::setIcon);
        Messages.getStringOpt(key + ".mnemonic")
                .map(s -> s.charAt(0))
                .ifPresent(button::setMnemonic);
        Messages.getStringOpt(key + ".tip")
                .ifPresent(button::setToolTipText);
        Messages.getStringOpt(key + ".selectedIcon")
                .flatMap(s -> Optional.ofNullable(SwingUtils.class.getResource(s)))
                .map(ImageIcon::new)
                .ifPresent(button::setSelectedIcon);
        return button;
    }

    /**
     * Returns true if the confirm dialogue has been closed by confirmation (OK button)
     *
     * @param titleKey the title key message
     * @param content  the content
     */
    static boolean showConfirmDialog(String titleKey, Object content) {
        int selectedValue = JOptionPane.showConfirmDialog(null, content, Messages.getString(titleKey),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return selectedValue == JOptionPane.OK_OPTION;
    }

    /**
     * Show the modal dialogue while running computation
     */
    static JDialog showDialogMessage(String titleKey, String formatKey, Object... params) {
        JDialog dialogMessage = new JDialog();
        dialogMessage.setTitle(Messages.getString(titleKey));
        dialogMessage.setModal(true);
        dialogMessage.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialogMessage.setSize(400, 100);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString(Messages.format(formatKey, params));
        progressBar.setIndeterminate(true);

        new GridLayoutHelper<>(dialogMessage.getContentPane())
                .modify("insets,10 center vw,1 hw,1 hfill")
                .modify("at,0,0").add(progressBar);

        centerOnScreen(dialogMessage);

        Schedulers.io().scheduleDirect(() ->
                dialogMessage.setVisible(true));
        return dialogMessage;
    }

    /**
     * Shows an error message dialogue
     *
     * @param titleKey  the title key message
     * @param formatKey the format key
     * @param params    the message parameters
     */
    static void showErrorKey(String titleKey, String formatKey, Object... params) {
        JOptionPane.showMessageDialog(null, format(Messages.getString(formatKey), params), Messages.getString(titleKey),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an error message dialogue
     *
     * @param titleKey  the title key message
     * @param exception the exception
     */
    static void showErrorKey(String titleKey, Throwable exception) {
        JTextArea text = new JTextArea();
        text.setLineWrap(true);
        text.setEditable(false);
        text.setColumns(50);
        text.setRows(10);
        text.setText(exception.getMessage());
        JOptionPane.showMessageDialog(null, new JScrollPane(text), Messages.getString(titleKey),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a message dialogue
     *
     * @param titleKey the title key message
     * @param content  the content
     */
    static void showMessageKey(String titleKey, Object content) {
        JOptionPane.showMessageDialog(null, content, Messages.getString(titleKey),
                JOptionPane.PLAIN_MESSAGE);
    }
}
