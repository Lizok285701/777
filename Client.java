import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) throws IOException {

        SocketChannel outcoming = SocketChannel.open();
        try {
            outcoming.connect(new InetSocketAddress("127.0.0.1" , 6666));
            System.out.println("ServerSide.Server reached");
            outcoming.socket().setSoTimeout(10000);
            try (ObjectOutputStream SendtoServer = new ObjectOutputStream(outcoming.socket().getOutputStream());
                 ObjectInputStream GetfromServer = new ObjectInputStream(outcoming.socket().getInputStream())
            ) {
                System.out.println((String)GetfromServer.readObject());
                signin(SendtoServer, GetfromServer);

                ClientCommandReader clientreader = new ClientCommandReader(GetfromServer, SendtoServer);
                System.out.println(">Start command reading");
                clientreader.start_reading(new HashSet<String>(), "");
                System.out.println(">Closing socket and terminating programm. Boooom");
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }  catch (IOException e) {
            System.out.println(">Could not connect. ServerSide.Server caught a corona.");
            System.out.println(e.getMessage());
        }
    }

    public static void signin(ObjectOutputStream SendtoServer, ObjectInputStream GetfromServer) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        System.out.println("Добро пожаловать в приложение! Для работы с коллекцией необходимо авторизироваться.\n" +
                "Для этого введите в консоль log, если у вас уже есть аккаунт, или reg для регистрации");
        Scanner in = new Scanner(System.in);
        boolean flag = true;
        while(flag)
        {
            String input = in.nextLine();


            switch (input) {
                case ("log"): {
                    while(true) {
                        System.out.println("Введите ваш логин");
                        String login = in.nextLine();
                        System.out.println("Введите ваш пароль");
                        String password = in.nextLine();

                        SendtoServer.writeObject("login");
                        SendtoServer.writeObject(login);
                        byte[] a = MessageDigest.getInstance("SHA-384").digest(password.getBytes());
                        SendtoServer.writeObject(new String(a));
                        String response = (String) GetfromServer.readObject();
                        if (response.equals("Ok")) {
                            System.out.println("Вы успешно вошли. Для получения справки по доступным командам введите help");
                            flag = false;
                            break;
                        } else
                            System.out.println(response);
                    }
                }

                case ("reg"): {
                    while(true) {
                        System.out.println("Введите ваш логин");
                        String login = in.nextLine();
                        System.out.println("Введите ваш пароль");
                        String password = in.nextLine();

                        SendtoServer.writeObject("reg");
                        SendtoServer.writeObject(login);
                        byte[] a = MessageDigest.getInstance("SHA-384").digest(password.getBytes());
                        SendtoServer.writeObject(new String(a));
                        String response = (String) GetfromServer.readObject();
                        if (response.equals("Ok")) {
                            System.out.println("Вы успешно вошли. Для получения справки по доступным командам введите help");
                            flag = false;
                            break;
                        } else
                            System.out.println(response);
                    }
                }

                default:
                    break;

            }

        }

    }
}