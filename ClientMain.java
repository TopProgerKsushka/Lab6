import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.System.exit;

public class ClientMain {
    static Gson gson = new Gson();

    static int init(String host, int port) throws IOException, ClassNotFoundException, ParserConfigurationException {
        Socket server = new Socket(host, port);
        ObjectOutputStream serverOutput = new ObjectOutputStream(server.getOutputStream());
        serverOutput.writeObject(Request.START);
        ObjectInputStream serverInput = new ObjectInputStream(server.getInputStream());
        Response response = (Response) serverInput.readObject();
        if (response == Response.SESSION)
        {
            return (int) serverInput.readObject();
        }
        System.out.println((String) serverInput.readObject());
        exit(0);
        return 0;
    }

    static void sendStop(Socket server, int ssid) throws Exception {
        ObjectOutputStream serverOutput = new ObjectOutputStream(server.getOutputStream());
        serverOutput.writeObject(Request.EXIT);
        serverOutput.writeObject(ssid);
        ObjectInputStream serverInput = new ObjectInputStream(server.getInputStream());
        serverInput.readObject();
    }

    public static void main(String args[]) throws Exception, ParserConfigurationException {
        if (args.length != 3) {
            System.out.println("Requiers 3 arguments: <adress> <port> <filename>");
            return;
        }
        DocumentBuilder xmlparser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String host = args[0];
        int port = Integer.valueOf(args[1]);
        int ssid = init(host, port);
        System.out.println("Session started ssid:" + ssid);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    sendStop(new Socket(host, port), ssid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean first = true;
        while (true) {
            String req[] = new String[2];
            if (first) {
                req[0] = "load";
                req[1] = args[2];
                first = false;
            }
            else {
                req = reader.readLine().split(" ", 2);
            }
            boolean hasarg = false;
            boolean valid = true;
            Request request = Request.INFO;
            System.out.println(req[0].toLowerCase());
            switch (req[0].toLowerCase()) {
                case "add":
                    request = Request.ADD;
                    hasarg = true;
                    break;
                case "add_if_min":
                    request = Request.ADD_IF_MIN;
                    hasarg = true;
                    break;
                case "add_if_max":
                    request = Request.ADD_IF_MAX;
                    hasarg = true;
                    break;
                case "remove":
                    request = Request.REMOVE;
                    hasarg = true;
                    break;
                case "remove_lower":
                    request = Request.REMOVE_LOWER;
                    hasarg = true;
                    break;
                case "show":
                    request = Request.SHOW;
                    hasarg = false;
                    break;
                case "info":
                    request = Request.INFO;
                    hasarg = false;
                    break;
                case "import":
                    request = Request.IMPORT;
                    hasarg = false;
                    break;
                case "load":
                    request = Request.LOAD;
                    hasarg = false;
                    break;
                case "save":
                    request = Request.SAVE;
                    hasarg = false;
                    break;
                default:
                    valid = false;
                    break;

            }
            if (valid && (!(hasarg || request == Request.IMPORT || request == Request.LOAD) || req.length >= 2)) {
                try {
                    Guest guest = null;
                    Document doc = null;
                    String filename = null;
                    if (request == Request.IMPORT)
                        doc = xmlparser.parse(req[1]);
                    if (request == Request.LOAD)
                        filename = req[1];
                    if (hasarg)
                        guest = gson.fromJson(req[1], Guest.class);
                    Socket server = new Socket(args[0], Integer.valueOf(args[1]));
                    ObjectOutputStream serverOutput = new ObjectOutputStream(server.getOutputStream());
                    serverOutput.writeObject(request);
                    serverOutput.writeObject(ssid);
                    if (request == Request.IMPORT)
                        serverOutput.writeObject(doc);
                    if (hasarg)
                        serverOutput.writeObject(guest);
                    if (request == Request.LOAD)
                        serverOutput.writeObject(filename);
                    ObjectInputStream serverInput = new ObjectInputStream(server.getInputStream());
                    Response response = (Response) serverInput.readObject();
                    switch (response) {
                        case SUCCESS:
                            System.out.println("Done!");
                            break;
                        case MESSAGE:
                            System.out.println((String) serverInput.readObject());
                            break;
                        case OBJECT:
                            ArrayList<Guest> collection = (ArrayList<Guest>) serverInput.readObject();
                            for (Guest g : collection)
                                System.out.println(gson.toJson(g));
                            break;
                        default:
                            System.out.println("Invalid response");
                    }
                } catch (JsonSyntaxException | SAXException e) {
                    System.out.println("Syntax error");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IO Error");
                }
            }
            else {
                System.out.println("Invalid command");
            }
        }
    }
}
