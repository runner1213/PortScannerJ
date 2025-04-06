package org.cats.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

public class MinecraftServerChecker {
    private static final int[] PROTOCOLS = {0x47, 0x4D};

    public static MinecraftServerInfo checkMinecraftServer(String ip, int port) {
        for (int protocol : PROTOCOLS) {
            MinecraftServerInfo info = queryServer(ip, port, protocol);
            if (info != null) return info;
        }
        return new MinecraftServerInfo(port, "Нет ответа от сервера", 0, ip + ":" + port);
    }

    private static MinecraftServerInfo queryServer(String ip, int port, int protocol) {
        try (Socket socket = new Socket(ip, port)) {
            socket.setSoTimeout(3000);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            ByteArrayOutputStream handshake = new ByteArrayOutputStream();
            writeVarInt(handshake, 0x00);
            writeVarInt(handshake, protocol);
            writeVarInt(handshake, ip.length());
            handshake.write(ip.getBytes(StandardCharsets.UTF_8));
            handshake.write(new byte[]{(byte) (port >> 8), (byte) port, 0x01});
            ByteArrayOutputStream packet = new ByteArrayOutputStream();
            writeVarInt(packet, handshake.size());
            packet.write(handshake.toByteArray());
            out.write(packet.toByteArray());
            out.write(new byte[]{0x01, 0x00});
            out.flush();
            readVarInt(in);
            readVarInt(in);
            int jsonLength = readVarInt(in);
            byte[] jsonData = readFully(in, jsonLength);
            String response = new String(jsonData, StandardCharsets.UTF_8);
            MinecraftServerInfo serverInfo;
            try {
                JSONObject json = new JSONObject(response);
                String motd = "Нет описания";
                if (json.has("description")) {
                    Object desc = json.get("description");
                    if (desc instanceof JSONObject) {
                        motd = ((JSONObject) desc).optString("text", motd);
                    } else if (desc instanceof String) {
                        motd = (String) desc;
                    }
                }
                int online = 0;
                if (json.has("players")) {
                    JSONObject players = json.getJSONObject("players");
                    online = players.optInt("online", 0);
                }
                serverInfo = new MinecraftServerInfo(port, motd, online, ip + ":" + port);
            } catch (JSONException e) {
                serverInfo = new MinecraftServerInfo(port, "Неверный формат ответа", 0, ip + ":" + port);
            }
            return serverInfo;
        } catch (IOException | JSONException e) {
            return new MinecraftServerInfo(port, "Ошибка связи с сервером", 0, ip + ":" + port);
        } catch (Throwable t) {
            return new MinecraftServerInfo(port, "Непредвиденная ошибка", 0, ip + ":" + port);
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
        int result = 0, numRead = 0, read;
        do {
            read = in.read();
            if (read == -1) throw new EOFException();
            result |= (read & 0x7F) << (7 * numRead);
            numRead++;
            if (numRead > 5) throw new IOException("VarInt is too big");
        } while ((read & 0x80) != 0);
        return result;
    }

    private static byte[] readFully(InputStream in, int length) throws IOException {
        byte[] data = new byte[length];
        int totalRead = 0;
        while (totalRead < length) {
            int read = in.read(data, totalRead, length - totalRead);
            if (read == -1) throw new EOFException();
            totalRead += read;
        }
        return data;
    }
}
