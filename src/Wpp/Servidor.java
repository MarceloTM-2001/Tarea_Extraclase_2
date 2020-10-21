package Wpp;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class Servidor {
    public static void main(String[] args){
        MarcoServidor mimarco = new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
class MarcoServidor extends JFrame implements Runnable{
    private Logger log = Logger.getLogger(MarcoServidor.class.getName());
    public MarcoServidor(){
        setBounds(1200,300,280,350);

        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());

        areatexto=new JTextArea();

        milamina.add(areatexto,BorderLayout.CENTER);
        add(milamina);

        setVisible(true);

        Thread mihilo = new Thread(this);
        mihilo.start();
    }
    @Override
    public void run() {
        try {
            ServerSocket servidor = new ServerSocket(5000);

            String nick,ip,mensaje;

            PaqueteEnvio paqueterecibido;

            while(true) {
                Socket misocket = servidor.accept();

                ObjectInputStream paquetedatos= new ObjectInputStream(misocket.getInputStream());

                paqueterecibido= (PaqueteEnvio) paquetedatos.readObject();

                nick=paqueterecibido.getNombre();
                ip=paqueterecibido.getIp();
                mensaje=paqueterecibido.getMensaje();

                areatexto.append("\n"+nick+": "+mensaje+" para "+ip);

                Socket enviarDestino= new Socket(ip,5001);
                log.info("Conectó con el servidor");
                ObjectOutputStream paquetedestino = new ObjectOutputStream(enviarDestino.getOutputStream());
                paquetedestino.writeObject(paqueterecibido);
                enviarDestino.close();

                paquetedestino.close();
                misocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.severe(e.getMessage());
        }

    }
    private JTextArea areatexto;
}
