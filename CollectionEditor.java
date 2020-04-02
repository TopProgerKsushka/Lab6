/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

class CollectionEditor {
    HashSet<Guest> collection;
    String file;

    CollectionEditor(HashSet<Guest> initialCollection, String file) {
        collection = initialCollection;
        this.file = file;
    }

    void run() throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Operations.exit(collection, file);
            }

            ;
        });

        System.out.println("Dear user, please, enter your command");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String result = new String();
        String[] splitedLine;
        do {
            String line = br.readLine();
            splitedLine = line.split(" ", 2);
            result = splitedLine[0];

            switch (result) {
                case ("add"): {
                    try {
                        collection = Operations.addElement(collection, splitedLine[1]);
                    } catch (Exception e) {
                        System.out.println("This data is not enough for me");
                    }
                }
                break;
                case ("exit"): {
                    if (!(collection.isEmpty())) {
                    */
                      //  Operations.exit(collection, file/*"db.xml"*/);
                    /*}
                }
                break;
                case ("info"): {
                    if (!(collection.isEmpty())) Operations.info(collection);
                    else System.out.println("Collection is empty");
                }
                break;
                case ("remove_lower"): {
                    if (!(collection.isEmpty())) Operations.removeLower(collection, splitedLine[1]);
                    else System.out.println("Collection is empty");
                }
                break;
                case ("show"): {
                    if (!(collection.isEmpty())) Operations.show(collection);
                    else System.out.println("Collection is empty");
                }
                break;
                case ("add_if_max"): {
                    try {
                        collection = Operations.addIfMax(collection, splitedLine[1]);
                    } catch (Exception e) {
                        System.out.println("This data is not enough for me");
                    }
                }
                break;
                case ("add_if_min"): {
                    try {
                        collection = Operations.addIfMin(collection, splitedLine[1]);
                    } catch (Exception e) {
                        System.out.println("This data is not enough for me");
                    }
                }
                break;

                case ("remove"): {
                    if (!(collection.isEmpty())) Operations.remove(collection, splitedLine[1]);
                    else System.out.println("Collection is empty");
                }
                break;


                default:
                    System.out.println("I don't understand");
            }
        } while (!result.equals("exit"));
    }
}*/