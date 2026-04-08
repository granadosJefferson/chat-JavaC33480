package jchat;

import java.net.*;
import java.io.*;
import java.util.*;

public class Flujo extends Thread {

    static int contadorClientes = 1;

    Socket nsfd;
    DataInputStream FlujoLectura;
    DataOutputStream FlujoEscritura;
    String username;
    boolean conectado = false;

    public Flujo(Socket sfd) {
        nsfd = sfd;
        try {
            FlujoLectura = new DataInputStream(new BufferedInputStream(sfd.getInputStream()));
            FlujoEscritura = new DataOutputStream(new BufferedOutputStream(sfd.getOutputStream()));

            // Repetir hasta obtener un nombre válido
            while (true) {
                String nombreRecibido = FlujoLectura.readUTF().trim();

                synchronized (Servidor.usuarios) {
                    // Si viene vacío, asigna Cliente X automático
                    if (nombreRecibido == null || nombreRecibido.trim().isEmpty()) {
                        nombreRecibido = "Cliente " + contadorClientes++;
                    }

                    // Si el nombre ya existe, pedir otro
                    if (Servidor.usuarios.containsKey(nombreRecibido)) {
                        FlujoEscritura.writeUTF("__NAME_TAKEN__:El nombre '" + nombreRecibido + "' ya está en uso. Ingrese otro.");
                        FlujoEscritura.flush();
                    } else {
                        username = nombreRecibido.trim();
                        Servidor.usuarios.put(username, this);
                        conectado = true;
                        break;
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("IOException(Flujo): " + ioe);
        }
    }

    public void run() {
        if (!conectado) {
            return;
        }

        broadcast(username + " se ha conectado");
        broadcastUserList();

        while (true) {
            try {
                String linea = FlujoLectura.readUTF();

                if (!linea.trim().equals("")) {

                    if (linea.startsWith("@") && linea.contains(":")) {
                        int indice = linea.indexOf(":");
                        String destino = linea.substring(1, indice).trim();
                        String mensaje = linea.substring(indice + 1).trim();

                        Flujo receptor = Servidor.usuarios.get(destino);

                        if (receptor != null) {
                            receptor.FlujoEscritura.writeUTF("(Privado) " + username + ": " + mensaje);
                            receptor.FlujoEscritura.flush();

                            FlujoEscritura.writeUTF("(Privado a " + destino + "): " + mensaje);
                            FlujoEscritura.flush();
                        } else {
                            FlujoEscritura.writeUTF("Usuario no encontrado: " + destino);
                            FlujoEscritura.flush();
                        }

                    } else {
                        linea = username + "> " + linea;
                        broadcast(linea);
                    }
                }
            } catch (IOException ioe) {
                synchronized (Servidor.usuarios) {
                    Servidor.usuarios.remove(username);
                }

                broadcast(username + " se ha desconectado");
                broadcastUserList();
                break;
            }
        }
    }

    public void broadcast(String mensaje) {
        synchronized (Servidor.usuarios) {
            for (Flujo f : Servidor.usuarios.values()) {
                try {
                    synchronized (f.FlujoEscritura) {
                        f.FlujoEscritura.writeUTF(mensaje);
                        f.FlujoEscritura.flush();
                    }
                } catch (IOException ioe) {
                    System.out.println("Error: " + ioe);
                }
            }
        }
    }

    public void broadcastUserList() {
        StringBuilder lista = new StringBuilder("__USERLIST__:");

        synchronized (Servidor.usuarios) {
            boolean primero = true;

            for (String user : Servidor.usuarios.keySet()) {
                if (!primero) {
                    lista.append(",");
                }
                lista.append(user);
                primero = false;
            }

            for (Flujo f : Servidor.usuarios.values()) {
                try {
                    synchronized (f.FlujoEscritura) {
                        f.FlujoEscritura.writeUTF(lista.toString());
                        f.FlujoEscritura.flush();
                    }
                } catch (IOException ioe) {
                    System.out.println("Error enviando lista de usuarios: " + ioe);
                }
            }
        }
    }
}
