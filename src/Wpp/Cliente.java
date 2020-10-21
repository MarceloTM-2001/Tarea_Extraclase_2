/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Wpp;



import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;


/**
 *
 * @author Lenovo
 */

public class Cliente {
    public static void main (String[] args) throws IOException { ;
        MarcoCliente mimarco=new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
class MarcoCliente extends JFrame{
    public MarcoCliente() throws IOException {
        setBounds(600,300,280,350);

        LaminaMarcoCliente milamina = new LaminaMarcoCliente();

        add(milamina);
        setVisible(true);
    }
}

class LaminaMarcoCliente extends JPanel implements Runnable{
    private Logger LOG = Logger.getLogger(LaminaMarcoCliente.class.getName());



    public LaminaMarcoCliente() throws IOException {
        FileHandler fileXml = new FileHandler("Logging.xml");
        LOG.addHandler(fileXml);
        Nombre= new JTextField("Nombre:",5);
        add(Nombre);


        JLabel texto = new JLabel("Chat");
        add(texto);

        IP = new JTextField("Dir. IP",8);
        add(IP);

        Chat= new JTextArea(12,20);
        add(Chat);


        Message=new JTextField(20);
        add(Message);

        miboton=new JButton("Enviar");
        EnviarTexto mievento=new EnviarTexto();
        miboton.addActionListener(mievento);

        add(miboton);

        Thread mihilo = new Thread(this);
        mihilo.start();
    }

    @Override
    public void run() {
        try{
            ServerSocket servercliente = new ServerSocket(5001);
            LOG.info("Se conectó con el servidor");
            Socket cliente;

            PaqueteEnvio paqueterecibido;

            while(true){

                cliente=servercliente.accept();
                ObjectInputStream paqueteentrada = new ObjectInputStream(cliente.getInputStream());
                paqueterecibido= (PaqueteEnvio) paqueteentrada.readObject();
                Chat.append("\n" + paqueterecibido.getNombre()+": "+paqueterecibido.getMensaje());
            }


        }catch (Exception e){
            LOG.severe(e.getMessage());
        }
    }

    private class EnviarTexto implements ActionListener{
        private Logger LOG = Logger.getLogger(EnviarTexto.class.getName());


        @Override
        public void actionPerformed(ActionEvent e){
            FileHandler fileXml = null;
            try {
                fileXml = new FileHandler("Logging.xml");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            LOG.addHandler(fileXml);
            if(IP.getText()!=""||Nombre.getText()!="") {
                try {

                    Socket misocket = new Socket("192.168.1.76", 5000);
                    LOG.info("Se conectó con un servidor");

                    PaqueteEnvio datos = new PaqueteEnvio();

                    datos.setIp(IP.getText());
                    datos.setNombre(Nombre.getText());
                    datos.setMensaje(Message.getText());

                    Chat.append("\n" + "Yo: " + Message.getText());

                    ObjectOutputStream PaqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
                    PaqueteDatos.writeObject(datos);
                    misocket.close();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    LOG.severe(e1.getMessage());
                }
            }else if(Nombre.getText()==""){
                LOG.severe("No hay nombre al que asociar el mensaje");
            }else if (IP.getText()==""){
                LOG.severe("No hay IP a la cual enviar el mensaje");
            }
        }
    }
    private JTextField Message,Nombre,IP;
    private JButton miboton;
    private JTextArea Chat;
}
class PaqueteEnvio implements Serializable{
    private String nombre,Ip,Mensaje;
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getIp(){
        return Ip;
    }
    public void setIp(String Ip) {
        this.Ip = Ip;
    }
    public String getMensaje(){
        return Mensaje;
    }
    public void setMensaje(String Mensaje) {
        this.Mensaje = Mensaje;
    }
}
