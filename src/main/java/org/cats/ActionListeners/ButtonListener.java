package org.cats.ActionListeners;

import org.cats.core.PortScanner;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import static org.cats.ui.Elements.*;

public class ButtonListener {
    public static void init() {
        startScanBtn.addActionListener(e -> {
            if (domainField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите домен для сканирования!");
            } else {
                String ip = null;
                try {
                    ip = InetAddress.getByName(domainField.getText()).getHostAddress();
                } catch (UnknownHostException ex) {
                    String input = domainField.getText().trim();
                    JOptionPane.showMessageDialog(frame, "Не удалось получить IP-адрес для " + input, "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                ipofDomainLabel.setText("Айпи: " + ip);
                getIPLocation(ip);
                try {
                    String input = JOptionPane.showInputDialog(frame, "Введите количество потоков", "50");
                    int threads = Integer.parseInt(input.trim());
                    int startPort = (int) startportSpinner.getValue();
                    int endPort = (int) endportSpinner.getValue();
                    PortScanner.scan(ip, threads, startPort, endPort);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Введите корректное число потоков!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        checkButton.addActionListener(e -> {
            String input = domainField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите домен или IP!");
                return;
            }
            try {
                String ip = InetAddress.getByName(input).getHostAddress();
                ipofDomainLabel.setText("Айпи: " + ip);
                getIPLocation(ip);
            } catch (UnknownHostException ex) {
                JOptionPane.showMessageDialog(frame, "Не удалось получить IP-адрес для: " + input, "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void getIPLocation(String ip) {
        String urlString = "http://ip-api.com/json/" + ip + "?fields=status,country,regionName,city,message";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());

            if ("success".equals(json.getString("status"))) {
                String location = json.getString("country") + ", " + json.getString("regionName") + ", " + json.getString("city");
                locationLabel.setText("Локация: " + location);
            } else {
                JOptionPane.showMessageDialog(frame, "Не удалось определить местоположение IP: " + ip + "\nОшибка: " + json.getString("message"), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Ошибка при запросе к API: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
