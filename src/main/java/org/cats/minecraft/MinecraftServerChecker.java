package org.cats.minecraft;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class MinecraftServerChecker {
    private static final int[] PROTOCOLS = {0x47, 0x4D};

    public static MinecraftServerInfo checkMinecraftServer(String ip, int port) {
        for (int protocol : PROTOCOLS) {
            MinecraftServerInfo info = queryServer(ip, port, protocol);
            if (info != null) return info;
        }
        return null;
    }

    private static MinecraftServerInfo queryServer(String ip, int port, int protocol) {
        try (Socket socket = new Socket(ip, port)) {
            socket.setSoTimeout(3000);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            byte[] addressBytes = ip.getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream handshake = new ByteArrayOutputStream();
            handshake.write(0x00);
            handshake.write(protocol);
            handshake.write(addressBytes.length);
            handshake.write(addressBytes);
            handshake.write((port >> 8) & 0xFF);
            handshake.write(port & 0xFF);
            handshake.write(0x01);

            writeVarInt(out, handshake.size());
            out.write(handshake.toByteArray());

            out.write(0x01);
            out.write(0x00);
            out.flush();

            int packetLength = readVarInt(in);
            if (packetLength < 10) return null;

            int packetId = readVarInt(in);
            int jsonLength = readVarInt(in);
            if (jsonLength <= 0 || jsonLength > 32767) return null;

            byte[] jsonData = new byte[jsonLength];
            if (in.read(jsonData) < jsonLength) return null;

            JSONObject json = new JSONObject(new String(jsonData, StandardCharsets.UTF_8));
            return new MinecraftServerInfo(
                    port,
                    json.optJSONObject("description").optString("text", "Нет описания"),
                    json.optJSONObject("players").optInt("online", 0),
                    ip + ":" + port
            );
        } catch (IOException e) {
            return null;
        }
    }

    private static void writeVarInt(OutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }

    private static int readVarInt(InputStream in) throws IOException {
        int numRead = 0, result = 0;
        byte read;
        do {
            read = (byte) in.read();
            if (read == -1) throw new IOException();
            int value = (read & 0x7F);
            result |= (value << (7 * numRead));
            numRead++;
            if (numRead > 5) throw new IOException();
        } while ((read & 0x80) != 0);
        return result;
    }
}
