package org.cats.minecraft;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONException;

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
            JSONObject json = new JSONObject(response);

            JSONObject description = json.optJSONObject("description");
            String motd = (description != null) ? description.optString("text", "Нет описания") : "Нет описания";
            JSONObject players = json.optJSONObject("players");
            int online = (players != null) ? players.optInt("online", 0) : 0;

            return new MinecraftServerInfo(port, motd, online, ip + ":" + port);
        } catch (IOException | JSONException e) {
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
        int result = 0, numRead = 0, read;
        while (true) {
            read = in.read();
            if (read == -1) throw new EOFException("End of stream reached.");
            result |= (read & 0x7F) << (7 * numRead++);
            if ((read & 0x80) == 0) break;
            if (numRead >= 5) throw new IOException("VarInt too long.");
        }
        return result;
    }

    private static byte[] readFully(InputStream in, int length) throws IOException {
        byte[] data = new byte[length];
        int read, totalRead = 0;
        while (totalRead < length && (read = in.read(data, totalRead, length - totalRead)) != -1) {
            totalRead += read;
        }
        if (totalRead < length) throw new EOFException("Unexpected end of stream.");
        return data;
    }
}
