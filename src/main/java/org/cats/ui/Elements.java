package org.cats.ui;

import javax.swing.*;
import java.awt.*;

import static org.cats.Main.*;

public class Elements {
    public static void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", new Color(20, 20, 20));
            UIManager.put("OptionPane.background", new Color(20, 20, 20));
            UIManager.put("OptionPane.messageForeground", Color.LIGHT_GRAY);
            UIManager.put("Button.background", new Color(60, 60, 60));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("TextField.background", new Color(50, 50, 50));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextField.caretForeground", Color.WHITE);
            UIManager.put("Spinner.background", new Color(50, 50, 50));
            UIManager.put("Spinner.foreground", Color.WHITE);
            UIManager.put("Spinner.border", BorderFactory.createLineBorder(Color.GRAY));
        } catch (Exception ignored) {}

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        mainfillerLabel.setBounds(230, 50, 330, 51);
        mainfillerLabel.setFont(new Font("Arial", Font.BOLD, 42));

        enterDomainlabel.setBounds(40, 250, 251, 21);
        enterDomainlabel.setFont(new Font("Arial", Font.PLAIN, 14));

        domainField.setBounds(40, 280, 181, 31);
        domainField.setFont(new Font("Arial", Font.PLAIN, 12));

        Font portlabelFont = new Font("Arial", Font.PLAIN, 12);
        startPortLabel.setBounds(360, 250, 131, 21);
        startPortLabel.setFont(portlabelFont);

        SpinnerNumberModel startPortModel = new SpinnerNumberModel(1, 1, 65535, 1);
        startportSpinner.setModel(startPortModel);
        startportSpinner.setBounds(500, 250, 111, 31);
        startportSpinner.setFont(portlabelFont);

        endPortLabel.setBounds(370, 330, 121, 21);
        endPortLabel.setFont(portlabelFont);

        SpinnerNumberModel endPortModel = new SpinnerNumberModel(65535, 1, 65535, 1);
        endportSpinner.setModel(endPortModel);
        endportSpinner.setBounds(500, 330, 111, 31);
        endportSpinner.setFont(portlabelFont);

        startportSpinner.addChangeListener(e -> {
            int startPort = (int) startportSpinner.getValue();
            int endPort = (int) endportSpinner.getValue();
            if (startPort > endPort) {
                endportSpinner.setValue(startPort);
            }
        });

        endportSpinner.addChangeListener(e -> {
            int startPort = (int) startportSpinner.getValue();
            int endPort = (int) endportSpinner.getValue();
            if (endPort < startPort) {
                startportSpinner.setValue(endPort);
            }
        });

        startScanBtn.setBounds(180, 440, 381, 61);

        Font domainFonts = new Font("Arial", Font.PLAIN, 12);
        locationLabel.setBounds(40, 370, 400, 28);
        locationLabel.setFont(domainFonts);

        ipofDomainLabel.setBounds(40, 346, 161, 20);
        ipofDomainLabel.setFont(domainFonts);

        checkButton.setBounds(120, 320, 89, 27);
        checkButton.setFont(new Font("Arial", Font.PLAIN, 10));

        //enterIpQBtn.setBounds(40, 210, 150, 25); // 40, 250, 231, 21
        //enterIpQBtn.setFont(new Font("Arial", Font.PLAIN, 12));

        frame.add(mainfillerLabel);
        frame.add(domainField);
        frame.add(enterDomainlabel);
        frame.add(startScanBtn);
        frame.add(startportSpinner);
        frame.add(endportSpinner);
        frame.add(locationLabel);
        frame.add(checkButton);
        frame.add(ipofDomainLabel);
        frame.add(startPortLabel);
        frame.add(endPortLabel);
        //frame.add(enterIpQBtn);

        SwingUtilities.updateComponentTreeUI(frame); // Применение UI
    }
}
