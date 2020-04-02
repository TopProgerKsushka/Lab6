/*import com.google.gson.*;

import java.io.*;
import java.util.*;

import com.google.gson.JsonSyntaxException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Operations {

    public static class DBException extends Exception {
        public enum Cause {
            BADNAME("Guest must have exactly one name\n");
            String msg;

            Cause(String s) {
                msg = s;
            }
        }

        ;
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

    public static Date date = new Date();
    static Gson gson = new Gson();


    public static void show(HashSet<Guest> collection) {
        for (Guest g : collection) {
            System.out.println(g.toString());
        }
    }



    public static void info(HashSet<Guest> collection) {
        System.out.println("Collection's type : HashSet; Object's type : Guest \n" + "initialized " + date +
                "\n" + "Consist " + collection.size() + " elements");

    }


    public static HashSet<Guest> remove(HashSet<Guest> collection, String value) {
        Guest guest;
        try {
            guest = gson.fromJson(value, Guest.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON:\n" + e.getMessage());
            return collection;
        }
        if (collection.contains(guest)) {
            collection.remove(guest);
        } else {
            System.out.println("No such elements");
        }
        return collection;
    }


    public static HashSet<Guest> removeLower(HashSet<Guest> collection, String value) {
        Guest guest;
        try {
            guest = gson.fromJson(value, Guest.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON:\n" + e.getMessage());
            return collection;
        }

        Iterator<Guest> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (guest.getName().compareTo(iterator.next().getName()) > 0) {
                iterator.remove();
            }
        }
        return collection;
    }


    public static HashSet<Guest> addElement(HashSet<Guest> collection, String value) {
        Guest guest;
        try {
            guest = gson.fromJson(value, Guest.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON:\n" + e.getMessage());
            return collection;
        }
        String[] splitedLine = value.split("[}]\\s*", 2);
        try {
            if (collection.contains(guest)) {
                System.out.println("This element is already here");
                return collection;
            }
            collection.add(guest);
            System.out.println("New element was added");
        } catch (JsonSyntaxException mes) {
            System.out.println("Invalid data format");
        }
        return collection;
    }


    public static HashSet<Guest> addIfMin(HashSet<Guest> collection, String value) {
        Guest guest;
        try {
            guest = gson.fromJson(value, Guest.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON:\n" + e.getMessage());
            return collection;
        }
        Guest min = null;
        for (Guest g : collection) {
            if (min == null || g.getName().compareTo(min.getName()) < 0) {
                min = g;
            }
        }
        String[] splitedLine = value.split("[}]\\s*", 2);
        try {
            if (min == null || guest.getName().compareTo(min.getName()) < 0) {
                collection.add(guest);
                System.out.println("New element was added");
            } else System.out.println("Element is not added");
        } catch (JsonSyntaxException mes) {
            System.out.println("Invalid data format");
        }
        return collection;
    }

    public static HashSet<Guest> addIfMax(HashSet<Guest> collection, String value) {
        Guest guest;
        try {
            guest = gson.fromJson(value, Guest.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON:\n" + e.getMessage());
            return collection;
        }
        Guest max = null;
        for (Guest g : collection) {
            if (max == null || g.getName().compareTo(max.getName()) > 0) {
                max = g;
            }
        }

        try {
            if (max == null || guest.getName().compareTo(max.getName()) > 0) {
                collection.add(guest);
                System.out.println("New element was added");
            } else {
                System.out.println("Element isn't added");
            }
        } catch (JsonSyntaxException mes) {
            System.out.println("Invalid data format");
        }
        return collection;
    }


    public static void load(HashSet<Guest> collection, String file) {
        System.out.println("Loading collection from " + file + "...");
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(file);
            Node root = doc.getDocumentElement();
            NodeList docfoos = ((Element) root).getElementsByTagName("Guest");
            System.out.println("Found " + docfoos.getLength() + " Guests");
            for (int i = 0; i < docfoos.getLength(); i++) {
                Element docfoo = (Element) docfoos.item(i);
                NodeList names = docfoo.getElementsByTagName("name");
                if (names.getLength() != 1)
                    throw new DBException(DBException.Cause.BADNAME, i);
                String name = names.item(0).getTextContent();
                addElement(collection, "{\"name\":\"" + name + "\"}");
            }
        } catch (ParserConfigurationException e) {
            System.out.println("Failed to create new parser:\n" + e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Failed to open file:\n" + e.getMessage());
            System.exit(2);
        } catch (SAXException e) {
            System.out.println("Failed to parse XML file:\n" + e.getMessage());
            System.exit(3);
        } catch (DBException e) {
            System.out.println("Wrong Database format:\n" + e.getMessage());
            System.exit(4);
        }
        System.out.println("...Done!");
    }

    public static void exit(HashSet<Guest> collection, String fileName) {
        System.out.println("Saving to " + fileName + "...");
        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("<Guestcollection>");
            for (Guest g : collection) {
                bw.newLine();
                bw.write("    <Guest>");
                bw.newLine();
                bw.write("        <name>" + g.getName() + "</name>");
                bw.newLine();
                bw.write("    </Guest>");
            }
            bw.newLine();
            bw.write("</Guestcollection>");
            System.out.println("...Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

*/