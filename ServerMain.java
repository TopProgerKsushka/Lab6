//import com.sun.security.ntlm.Server;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerMain {
    public static void main(String args[]) throws IOException, ParserConfigurationException {
        if (args.length == 0) {
            System.out.println("port required");
            return;
        }
        DocumentBuilder xmlparser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        ServerSocket socket = new ServerSocket(Integer.valueOf(args[0]));
        int maxSessions = (args.length > 1) ? Integer.valueOf(args[1]) : 5;
        Lock mutex[] = new ReentrantLock[maxSessions];
        HashSet<Guest> collections[] = new HashSet[maxSessions];
        String filenames[] = new String[maxSessions];
        boolean busy[] = new boolean[maxSessions];
        Lock busyMutex = new ReentrantLock();
        class Responder extends Thread {
            Socket client;

            public Responder() throws IOException {
                client = socket.accept();
                System.out.println("socket created");

            }

            public void run() {

                System.out.println("Thread started");
                try {
                    DataInputStream is = new DataInputStream(client.getInputStream());
                    System.out.println("input stream created");
                    ObjectInputStream clientInputStream = new ObjectInputStream(is);
                    System.out.println("object input stream created");
                    ObjectOutputStream clientOutputStream = new ObjectOutputStream(client.getOutputStream());
                    System.out.println("output stream created");
                    Request req = (Request) clientInputStream.readObject();
                    switch (req) {
                        case START:
                            System.out.println("Someone tries to start");
                            busyMutex.lock();
                            boolean found = false;
                            for (int i = 0; i < maxSessions; i++) {
                                if (busy[i]) {
                                    continue;
                                }
                                clientOutputStream.writeObject(Response.SESSION);
                                collections[i] = new HashSet<Guest>();
                                clientOutputStream.writeObject(i);
                                busy[i] = true;
                                mutex[i] = new ReentrantLock();
                                found = true;
                                break;
                            }
                            if (!found) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject("No available sessions");
                            }
                            busyMutex.unlock();
                            break;
                        case ADD: {
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            Guest guest = (Guest) clientInputStream.readObject();
                            try {
                                collections[ssid] = ServerOperations.addElement(collections[ssid], guest);
                                clientOutputStream.writeObject(Response.SUCCESS);
                            } catch (ServerOperations.PException e) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject(e.message);
                            }
                            mutex[ssid].unlock();
                            break;
                        }
                        case ADD_IF_MAX: {
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            Guest guest = (Guest) clientInputStream.readObject();
                            collections[ssid] = ServerOperations.addIfMax(collections[ssid], guest);
                            clientOutputStream.writeObject(Response.SUCCESS);
                            mutex[ssid].unlock();
                            break;
                        }
                        case ADD_IF_MIN: {
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            Guest guest = (Guest) clientInputStream.readObject();
                            collections[ssid] = ServerOperations.addIfMin(collections[ssid], guest);
                            clientOutputStream.writeObject(Response.SUCCESS);
                            mutex[ssid].unlock();
                            break;
                        }
                        case REMOVE_LOWER: {
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            Guest guest = (Guest) clientInputStream.readObject();
                            collections[ssid] = ServerOperations.removeLower(collections[ssid], guest);
                            clientOutputStream.writeObject(Response.SUCCESS);
                            mutex[ssid].unlock();
                            break;
                        }
                        case REMOVE: {
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            Guest guest = (Guest) clientInputStream.readObject();
                            try {
                                collections[ssid] = ServerOperations.remove(collections[ssid], guest);
                                clientOutputStream.writeObject(Response.SUCCESS);
                            } catch (ServerOperations.PException e) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject(e.message);
                            }
                            mutex[ssid].unlock();
                            break;
                        }
                        case SHOW: {
                            System.out.println("show");
                            int ssid = (int) clientInputStream.readObject();
                            System.out.println("on session" + ssid);
                            mutex[ssid].lock();
                            clientOutputStream.writeObject(Response.OBJECT);
                            ServerOperations.show(collections[ssid], clientOutputStream);
                            mutex[ssid].unlock();
                            break;
                        }
                        case INFO: {
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            ServerOperations.info(collections[ssid], clientOutputStream);
                            mutex[ssid].unlock();
                            break;
                        }
                        case SAVE: {
                            System.out.println("save");
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            ServerOperations.save(collections[ssid], filenames[ssid]);
                            clientOutputStream.writeObject(Response.SUCCESS);
                            mutex[ssid].unlock();
                            break;
                        }
                        case IMPORT: {
                            System.out.println("import");
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            collections[ssid] = new HashSet<>();
                            Document doc = (Document) clientInputStream.readObject();
                            try {
                                ServerOperations.load(collections[ssid], doc);
                            }
                            catch (ServerOperations.DBException | ServerOperations.PException e) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject("Wrong Database format:\n" + e.getMessage());
                            }
                            clientOutputStream.writeObject(Response.SUCCESS);
                            mutex[ssid].unlock();
                            break;
                        }
                        case LOAD: {
                            System.out.println("load");
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            collections[ssid] = new HashSet <>();
                            String filename = (String) clientInputStream.readObject();
                            filenames[ssid] = filename;
                            System.out.println("on " + filename);
                            try {
                                Document doc = xmlparser.parse(filename);
                                ServerOperations.load(collections[ssid], doc);
                            }
                            catch (IOException e) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject("Failed to open file:\n" + e.getMessage());
                            } catch (SAXException e) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject("Failed to parse XML file:\n" + e.getMessage());
                            } catch (ServerOperations.DBException e) {
                                clientOutputStream.writeObject(Response.MESSAGE);
                                clientOutputStream.writeObject("Wrong Database format:\n" + e.getMessage());
                            }
                            mutex[ssid].unlock();
                            clientOutputStream.writeObject(Response.SUCCESS);
                            break;
                        }
                        case EXIT: {
                            busyMutex.lock();
                            System.out.println("Exit");
                            int ssid = (int) clientInputStream.readObject();
                            mutex[ssid].lock();
                            ServerOperations.save(collections[ssid], filenames[ssid]);
                            busy[ssid] = false;
                            collections[ssid].clear();
                            clientOutputStream.writeObject(Response.SUCCESS);
                            mutex[ssid].unlock();
                            busyMutex.unlock();
                            break;
                        }
                        default:
                            break;
                    }
                    System.out.println("Done");
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Server Started on port " + socket.getLocalPort());
        while (true) {
            Responder responder = new Responder();
            System.out.println("responder created");
            responder.start();
        }
    }
}
