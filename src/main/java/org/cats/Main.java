package org.cats;

import org.cats.ActionListeners.ButtonListener;
import org.cats.ui.Elements;

import javax.swing.*;

import static org.cats.ui.Elements.frame;

public class Main {
    public static void main(String[] args) {
        Elements.init();
        ButtonListener.init();

        JOptionPane.showMessageDialog(frame, "Убедитесь, что приложение открыто через терминал!");

        frame.setVisible(true);

    }
}