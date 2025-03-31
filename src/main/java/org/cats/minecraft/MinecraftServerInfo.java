package org.cats.minecraft;

public class MinecraftServerInfo {
    public int port;
    public String description;
    public int onlinePlayers;
    public String address;

    public MinecraftServerInfo(int port, String description, int onlinePlayers, String address) {
        this.port = port;
        this.description = description;
        this.onlinePlayers = onlinePlayers;
        this.address = address;
    }

    @Override
    public String toString() {
        return "\nПорт: " + port +
                "\nОписание: " + description +
                "\nОнлайн: " + onlinePlayers +
                "\nАдрес: " + address;
    }
}
