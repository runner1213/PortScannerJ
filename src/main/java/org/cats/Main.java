package org.cats;

import org.cats.ActionListeners.ButtonListener;
import org.cats.ui.Elements;

import javax.swing.*;

public class Main {
    public static JFrame frame = new JFrame("PortScanner");
    public static JLabel mainfillerLabel = new JLabel("Сканер портов");
    public static JTextField domainField = new JTextField();
    public static JLabel enterDomainlabel = new JLabel("Введите домен или айпи для скана");
    public static JButton startScanBtn = new JButton("Начать сканирование");
    public static JSpinner startportSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535, 1));
    public static JLabel startPortLabel = new JLabel("Начальный порт");
    public static JSpinner endportSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535, 1));
    public static JLabel endPortLabel = new JLabel("Конечный порт");
    public static JLabel locationLabel = new JLabel("Локация: ");
    public static JLabel ipofDomainLabel = new JLabel("Айпи: ");
    public static JButton checkButton = new JButton("Проверить");

    public static void main(String[] args) {
        Elements.init();
        ButtonListener.init();

        JOptionPane.showMessageDialog(frame, "Убедитесь, что приложение открыто через терминал!");

        frame.setVisible(true);
    }
}