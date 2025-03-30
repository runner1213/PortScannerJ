package org.cats.core;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PortScanner {
    public static void scan(String host, int threads, int startPort, int endPort) {
        try {
            String ip = InetAddress.getByName(host).getHostAddress();
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            List<Integer> openPorts = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);

            System.out.println("Сканирование портов " + startPort + "-" + endPort + " на " + ip + "...");

            for (int port = startPort; port <= endPort; port++) {
                final int finalPort = port;
                executor.execute(() -> {
                    boolean isOpen = scanPort(ip, finalPort);
                    System.out.println("Порт " + finalPort + " " + (isOpen ? "открыт" : "закрыт"));

                    if (isOpen) {
                        synchronized (openPorts) {
                            openPorts.add(finalPort);
                        }
                    }
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
                System.err.println("Предупреждение: Сканирование заняло слишком много времени и было прервано.");
            }

            System.out.println("Сканирование завершено.");
            if (openPorts.isEmpty()) {
                System.out.println("Открытые порты не найдены.");
            } else {
                System.out.println("Открытые порты: " + openPorts);
                System.out.println("Открытых портов: " + openPorts.size());
            }
            scanner.nextLine();
        } catch (UnknownHostException e) {
            System.err.println("Ошибка: Не удалось определить IP!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Сканирование прервано!");
        }
    }

    private static boolean scanPort(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 200);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
