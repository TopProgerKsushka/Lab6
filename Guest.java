import java.util.ArrayList;

public class Guest extends Character {
    private ArrayList<Food> favoriteFood = new ArrayList<>();

    public Guest(String name) {
        super(name);
        System.out.println("Новый гость " + name + " успешно добавлен" + " в сказку");
    }

    public int compareTo(Character guest) {
        int minLength = guest.getName().length();
        if (name.length() < minLength) {
            minLength = name.length();
        }
        for (int i = 0; i < minLength; i++) {
            if (guest.getName().charAt(i) == name.charAt(i)) continue;
            if (guest.getName().charAt(i) < name.charAt(i)) {
                return 1;
            } else return -1;
        }
        if (guest.getName().length() == name.length()) return 0;
        return ((minLength == name.length()) ? 1 : -1);
    }

    /*public void comeTo(Owner owner) {
        try {
            if (owner.getProperty() == null) {
                throw new LogicalException(owner.getName() + " не имеет собственности => " + getName() + " не может войти");
            }
            System.out.print(getName() + "вошла" + " к персонажу с именем " + owner.getName());
            place = owner.getProperty();
            System.out.println(" в " + owner.getProperty());

        } catch(LogicalException e) {
            System.out.println(e.getTrouble());
        }
    }*/
    public void addFavoriteFood(Food food) {
        favoriteFood.add(food);
    }

    public boolean isFavoriteFood(Food food) {
        return favoriteFood.contains(food);
    }

    public void announceFavoriteFood() {
        System.out.print(getName() + ": Я люблю ");
        for (int i = 0; i < favoriteFood.size(); i++) {
            System.out.print(favoriteFood.get(i).getName());
            if (i != favoriteFood.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    /*public void saw(Owner owner) {
        System.out.println(getName() + " увидел " + owner.getProperty() + " и побежал туда со всех ног");
        Location.change(this, 100, 100);
    }*/

    public String getPlace() {
        return place;
    }
}
