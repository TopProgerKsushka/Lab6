import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ServerOperations {
    public static class DBException extends Exception {
        public enum Cause {
            BADNAME("Guest must have exactly one name\n"),
            BADPLACE("Guest can't be in more than one place\n"),
            BADTIME("Guest can't be born twise\n"),
            BADSIZE("Guest can't have two sizes\n");
            String msg;

            Cause(String s) {
                msg = s;
            }
        }

        int place;
        Cause type;

        @Override
        public String getMessage() {
            return "Guest " + place + " : " + type.msg;
        }

        public DBException(Cause type, int place) {
            this.place = place;
            this.type = type;
        }
    }

    public static class PException extends Exception {
        public String message;

        PException(String message) {
            this.message = message;
        }
    }

    public static Date date = new Date();
    static Gson gson = new Gson();

    /****
     * Перебирает коллекцию и показвает информацию о каждом элементе
     * @param collection
     */

    public static void show(HashSet<Guest> collection, ObjectOutputStream clientOutputStream) throws IOException {
        /*List sortedList = new ArrayList(collection);
        Collections.sort(sortedList);
        clientOutputStream.writeObject(sortedList);*/
        clientOutputStream.writeObject(new ArrayList<Guest>(collection.stream().sorted().collect(Collectors.toList())));
    }

    /***
     * Выводит информацию о коллекции
     * @param collection
     */

    public static void info(HashSet<Guest> collection, ObjectOutputStream clientOutputStream) throws IOException {
        clientOutputStream.writeObject(Response.MESSAGE);
        clientOutputStream.writeObject("Collection's type : HashSet; Object's type : Guest \n" + "initialized " + date +
                "\n" + "Consist " + collection.size() + " elements");
    }

    /**
     * Удаляет элемент из коллекции
     *
     * @param collection
     * @param guest
     * @return collection Возвращает коллекцию с добавленным элементом
     */

    public static HashSet<Guest> remove(HashSet<Guest> collection, Guest guest) throws PException {
        if (collection.contains(guest))
            collection.remove(guest);
        else
            throw new PException("No such element here");
        return collection;
    }

    /**
     * Удаляет элемент из коллекции, если он меньше остальных
     *
     * @param collection
     * @param guest
     * @return collection Возвращает коллекцию с добавленным элементом
     */

    public static HashSet<Guest> removeLower(HashSet<Guest> collection, Guest guest) {
        return (collection = new HashSet(collection.stream().filter(x -> (x.compareTo(guest) >= 0)).collect(Collectors.toSet())));
    }

    /**
     * Добавляет элемент в коллекцию
     *
     * @param collection
     * @param guest
     * @return collection Возвращает коллекцию с добавленным элементом
     */

    public static HashSet<Guest> addElement(HashSet<Guest> collection, Guest guest) throws PException {
        if (collection.contains(guest)) {
            throw new PException("This element is already here");
        }
        collection.add(guest);
        return collection;
    }

    /**
     * Добавляет элемент, если он меньше всех остальных, в коллекцию
     *
     * @param collection
     * @param guest
     * @return collection Возвращает коллекцию с добавленным элементом
     */

    public static HashSet<Guest> addIfMin(HashSet<Guest> collection, Guest guest) {
        Guest min = null;
        for (Guest g : collection) {
            if (min == null || g.getName().compareTo(min.getName()) < 0) {
                min = g;
            }
        }
        if (min == null || guest.getName().compareTo(min.getName()) < 0)
            collection.add(guest);
        return collection;
    }

    /**
     * Добавляет элемент, если он больше всех остальных, в коллекцию
     *
     * @param collection
     * @param guest
     * @return collection Возвращает коллекцию с добавленным элементом
     */

    public static HashSet<Guest> addIfMax(HashSet<Guest> collection, Guest guest) {
        Guest max = null;
        for (Guest g : collection) {
            if (max == null || g.getName().compareTo(max.getName()) > 0) {
                max = g;
            }
        }

        if (max == null || guest.getName().compareTo(max.getName()) > 0) {
            collection.add(guest);
        }
        return collection;
    }

    /***
     *
     * @param collection
     * @param doc
     */

    public static void load(HashSet<Guest> collection, Document doc) throws DBException, PException {
        Node root = doc.getDocumentElement();
        NodeList docfoos = ((Element) root).getElementsByTagName("Guest");
        System.out.println("Found " + docfoos.getLength() + " Guests");
        for (int i = 0; i < docfoos.getLength(); i++) {
            Element docfoo = (Element) docfoos.item(i);
            NodeList names = docfoo.getElementsByTagName("name");
            NodeList places = docfoo.getElementsByTagName("place");
            NodeList times = docfoo.getElementsByTagName("time");
            NodeList sizes = docfoo.getElementsByTagName("size");
            if (names.getLength() != 1)
                throw new DBException(DBException.Cause.BADNAME, i);
            String json = "{\"name\":\"" + names.item(0).getTextContent() + "\"";
            if (places.getLength() > 1)
                throw new DBException(DBException.Cause.BADPLACE, i);
            if (places.getLength() == 1)
                json = json + ", \"place\":\"" + places.item(0).getTextContent() + "\"";
            if (times.getLength() > 1)
                throw new DBException(DBException.Cause.BADTIME, i);
            if (times.getLength() == 1)
                json = json + ", \"timeOfBirth\":" + times.item(0).getTextContent();
            if (sizes.getLength() > 1)
                throw new DBException(DBException.Cause.BADSIZE, i);
            if (sizes.getLength() == 1)
                json = json + ", \"size\":" + sizes.item(0).getTextContent();
            json += "}";
            addElement(collection, gson.fromJson(json, Guest.class));
        }
        System.out.println("...Done!");
    }

    /********
     * Выходит из программы и сохраняет коллекцию в файл
     * @param collection
     * @param fileName
     */

    public static void save(HashSet<Guest> collection, String fileName) {
        System.out.println("Saving to " + fileName + "...");
        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("<Guestcollection>");
            for (Guest g : collection) {
                bw.newLine();
                bw.write("<Guest>\n");
                bw.write("<name>" + g.getName() + "</name>\n");
                if (g.place != null)
                    bw.write("<place>" + g.getPlace() + "</place>\n");
                bw.write("<size>" + g.getSize() + "</size>\n");
                bw.write("<time>" + g.getTimeOfBirth() + "</time>\n");
                bw.write("</Guest>");
            }
            bw.newLine();
            bw.write("</Guestcollection>");
            System.out.println("...Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
