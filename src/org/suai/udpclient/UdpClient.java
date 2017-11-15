package org.suai.udpclient;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpClient {

    public static void main(String [] args) {
        Logger logger = Logger.getLogger(UdpClient.class.getName());
        String messageFromServer;
        String messageFromClient;
        String nameUser;
        InetAddress address; // адрес соединения с сервером
        int port;
        DatagramPacket sendPacket;
        DatagramPacket receivePacket;
        byte[] sendData;
        byte[] receiveData = new byte[256];
        Scanner in = new Scanner(System.in);

        try(DatagramSocket udpClientSocket = new DatagramSocket()) {
             // создание сокета для соединения клиент/сервер
            logger.log(Level.INFO, "Input IP server:\n");
            address = InetAddress.getByName(in.nextLine()); // чтение адреса

            logger.log(Level.INFO, "Input port:");
            String pt = in.nextLine();
            port = Integer.parseInt(pt);

            logger.log(Level.INFO, "Input your name:");
            nameUser = in.nextLine();
            logger.log(Level.INFO, "\nInput: 1 - send message, other - exit.\n");
            char ch = in.next().charAt(0);
            in.nextLine();
            while(ch == '1') {
                logger.log(Level.INFO, "Input your message " + nameUser);
                messageFromClient = in.nextLine(); // сообщение отправляемое серверу
                if(messageFromClient.equals("@dump")){
                    logger.log(Level.INFO, "Input the name of the file for write message history:");
                    StringBuilder filename = new StringBuilder("_");
                    filename.append(in.nextLine());
                    messageFromClient = messageFromClient.concat(filename.toString());
                }
                sendData = messageFromClient.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                udpClientSocket.send(sendPacket); // отправка

                receivePacket = new DatagramPacket(receiveData, receiveData.length); // для ответа от сервера
                udpClientSocket.receive(receivePacket); // получение

                messageFromServer = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                logger.log(Level.INFO, "From server: " + messageFromServer);
                logger.log(Level.INFO, "\nInput: 1 - send message, other - exit.\n");
                ch = in.next().charAt(0);
                in.nextLine();
            }
        }
        catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }
}
