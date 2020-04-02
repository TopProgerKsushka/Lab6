import java.io.Serializable;

public enum Request implements Serializable {
    START, REMOVE, INFO, REMOVE_LOWER, ADD, ADD_IF_MIN, ADD_IF_MAX, SHOW, EXIT, IMPORT, LOAD, SAVE
}
