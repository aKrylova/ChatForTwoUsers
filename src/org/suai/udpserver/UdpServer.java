/*
Задание 11. Чат для двух пользователей.

Написать текстовый чат для двух пользователей на сокетах. Чат должен быть реализован по принципу клиент-сервер.
Один пользователь находится на сервере, второй --- на клиенте.
Адреса и порты задаются через командную строку: клиенту --- куда соединяться, серверу --- на каком порту слушать.
При старте программы выводится текстовое приглашение, в котором можно ввести одну из следующих команд:
Задать имя пользователя (@name Vasya)
Послать текстовое сообщение (Hello)
Выход (@quit)
Принятые сообщения автоматически выводятся на экран. Программа работает по протоколу UDP.

 */

//доп.вар.7 добавить команду @dump filename для сохранения в текст.файл всей истории чата

package org.suai.udpserver;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpServer {

    public static void main(String[] ar){
        Logger logger = Logger.getLogger(UdpServer.class.getName());
        int port;
        List<String> messageHistory;
        messageHistory = new ArrayList<>(); // хранит все сообщения текущего пользователя
        DatagramPacket packetFromUser;
        DatagramPacket udpPacketOUT; // отправляется с сервера к клиенту
        String messageFromClient; // сообщение от клиента
        String messageToClient; //сообщение клиенту от сервера
        Scanner in = new Scanner(System.in);
        logger.log(Level.INFO, "Input port:");
        String pt = in.nextLine();
        port = Integer.parseInt(pt);
        String filename = "history"; // по умолчанию
        try(DatagramSocket udpServerSocket = new DatagramSocket(port)) { // подключение
            byte[] receiveData = new byte[256];
            byte[] sendData;

            logger.info("Server start...");
            do {
                // получение пакета от клиента
                packetFromUser = new DatagramPacket(receiveData, receiveData.length); // создали пакет
                udpServerSocket.receive(packetFromUser); // ожидание пакета от клиента
                messageFromClient = new String(packetFromUser.getData(), packetFromUser.getOffset(), packetFromUser.getLength()); // получили ответ и преобразуем в строку

                messageHistory.add(new java.util.Date().toString() + " Message from client: " + messageFromClient);
                // вывод сообщения
                logger.log(Level.INFO, "New request from {0}", packetFromUser.getSocketAddress().toString());
                logger.log(Level.INFO, " : {0} ", messageFromClient);
                //отправить ответ клиенту на его адрес и порт
                InetAddress adr = packetFromUser.getAddress(); // получаем адресс клиента
                port = packetFromUser.getPort(); // получаем порт клиента
                if(messageFromClient.contains("_")){
                    String [] tmp = messageFromClient.split("_");
                    messageFromClient = tmp[0];
                    filename = tmp[1];
                }
                switch (messageFromClient) {
                    case "hello":
                        messageToClient = "hello client!";
                        break;
                    case "say":
                        messageToClient = "UDP Server";
                        break;
                    case "help":
                        messageToClient = "@dump - save history in file";
                        break;
                    case "@dump":

                        if(messageHistory.isEmpty()){
                            logger.log(Level.WARNING, "Not new messages for write");
                            messageToClient = "";
                            break;
                        }
                        try(FileWriter fileMH = new FileWriter("/Programming/Projects/Java/ChatForTwoUsers/src/org/suai/udpserver/" + filename + ".log", true)){
                            for (String mes : messageHistory) {
                                fileMH.write(mes);
                                fileMH.append('\n');
                                fileMH.flush();
                            }
                            messageHistory.clear();
                        }
                        messageToClient = "History save in file "+ filename + ".log!";
                        break;
                    default:
                        messageToClient = "command not found";
                        break;
                }
                messageHistory.add(new java.util.Date().toString() + " Message to client: " + messageToClient);
                sendData = messageToClient.getBytes();
                udpPacketOUT = new DatagramPacket(sendData, sendData.length, adr, port);
                udpServerSocket.send(udpPacketOUT); // отправляем клиенту
            } while (true);
        }catch (IOException e ) {
            logger.log(Level.SEVERE, "Exception: ", e);
        }
    }

}
