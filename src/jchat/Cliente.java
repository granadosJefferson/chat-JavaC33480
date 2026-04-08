package jchat;

/**
 *
 * @author pablonoguera
 */
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

public class Cliente extends Frame implements ActionListener {

    static Socket sfd = null;
    static DataInputStream EntradaSocket;
    static DataOutputStream SalidaSocket;
    static TextField salida;
    static TextArea entrada;
    String texto;

    public Cliente() {
        setTitle("Chat");
        setSize(350, 200);

        salida = new TextField(30);
        salida.addActionListener(this);

        entrada = new TextArea();
        entrada.setEditable(false);

        add("South", salida);
        add("Center", entrada);
        setVisible(true);
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        try {
            sfd = new Socket("localhost", 8000);
            EntradaSocket = new DataInputStream(new BufferedInputStream(sfd.getInputStream()));
            SalidaSocket = new DataOutputStream(new BufferedOutputStream(sfd.getOutputStream()));

            //Se pide el nombre del cliente y lo envia primero antes de dejar escribir mensajes
            String nombre = JOptionPane.showInputDialog("Ingrese su nombre: ");

            if (nombre == null || nombre.trim().isEmpty()) {
                nombre = "";
            }

            SalidaSocket.writeUTF(nombre);
            SalidaSocket.flush();
            
            
        } catch (UnknownHostException uhe) {
            System.out.println("No se puede acceder al servidor.");
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println("Comunicación rechazada.");
            System.exit(1);
        }
      while (true) {
    try {
        String linea = EntradaSocket.readUTF();

        if (linea.startsWith("__NAME_TAKEN__:")) {
            String mensaje = linea.substring("__NAME_TAKEN__:".length());

            String nuevoNombre = JOptionPane.showInputDialog(cliente, mensaje, "Nombre en uso", JOptionPane.WARNING_MESSAGE);

            if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                nuevoNombre = "";
            }

            SalidaSocket.writeUTF(nuevoNombre);
            SalidaSocket.flush();
        }
        else if (linea.startsWith("__USERLIST__:")) {
            String listaUsuarios = linea.substring("__USERLIST__:".length());
            entrada.append("[Usuarios conectados]: " + listaUsuarios + " \n");
            // luego esto se puede mostrar visualmente
        }
        else {
            entrada.append(linea + "\n");
        }

    } catch (IOException ioe) {
        System.exit(1);
    }
}
    }

    public void actionPerformed(ActionEvent e) {
        texto = salida.getText();
        salida.setText("");
        try {
            SalidaSocket.writeUTF(texto);
            SalidaSocket.flush();
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        }
    }

    public boolean handleEvent(Event e) {
        if ((e.target == this) && (e.id == Event.WINDOW_DESTROY)) {
            if (sfd != null) {
                try {
                    sfd.close();
                } catch (IOException ioe) {
                    System.out.println("Error: " + ioe);
                }
                this.dispose();
            }
        }
        return true;
    }
}
