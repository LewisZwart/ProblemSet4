package lewis.problemset4;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.SerializablePermission;
import java.util.ArrayList;
import java.util.List;

/*
 * Keeps track of all information in the TO DO app.
 */
public class AppState implements Serializable {

    // list of all TO DO lists
    private ArrayList<ArrayList<String>> toDoLists;

    // position indicating the currently viewed list (-1 for the list of lists)
    private int positionCurrentToDoList;

    // names of the TO DO lists
    private ArrayList<String> toDoListNames;

    /*
     * Creates empty list of empty TO DO lists.
     */
    public AppState() {
        toDoLists = new ArrayList<>();
        positionCurrentToDoList = -1;
        toDoListNames = new ArrayList<>();
    }

    /*
     * Adds the given item to the current list.
     */
    public void addToState(String item) {

        // if item is a list, add its name to list names and add empty arraylist to lists
        if (positionCurrentToDoList == -1) {
            ArrayList<String> newList = new ArrayList<>();
            toDoLists.add(newList);
            toDoListNames.add(item);
        }

        // else add item to current list
        else {
            ArrayList<String> newList = toDoLists.get(positionCurrentToDoList);
            newList.add(item);
            toDoLists.set(positionCurrentToDoList, newList);
        }
    }

    /*
     * Removes item at given position from list.
     */
    public void removeItem(int position) {

        // if position indicates a list, remove the list and its name from the state
        if (positionCurrentToDoList == -1) {
            toDoLists.remove(position);
            toDoListNames.remove(position);
        }

        // else get the current list and remove item from it
        else {
            ArrayList<String> newList = toDoLists.get(positionCurrentToDoList);
            newList.remove(position);
            toDoLists.set(positionCurrentToDoList, newList);
        }
    }

    /*
     * Sets position of the current TO DO list to the given integer (if valid).
     */
    public boolean setPosition(int position) {
        if (position >= -1) {
            positionCurrentToDoList = position;
            return true;
        }
        else {
            return false;
        }
    }

    /*
     * Returns position of the current TO DO list.
     */
    public int getPosition() {
        return positionCurrentToDoList;
    }

    /*
     * Returns the name of the current TO DO list.
     */
    public String getCurrentName() {
        return toDoListNames.get(positionCurrentToDoList);
    }


    /*
     * Returns the current TO DO list, or the list of names if the current list is the overview.
     */
    public ArrayList<String> getCurrentToDoList() {
        if (positionCurrentToDoList == -1) {
            return toDoListNames;
        }
        else {
            return toDoLists.get(positionCurrentToDoList);
        }
    }

    /*
     * Saves the current state to toDoApp.data.
     */
    public void saveState(Context context) {
        try {
            // open output stream
            FileOutputStream outputStream = context.openFileOutput
                    ("toDoApp.data", Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);

            // save state
            objectStream.writeObject(this);

            // close output stream
            objectStream.close();
            outputStream.close();
        }

        // if file not found, show error toast
        catch (IOException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context, context.getString(R.string.openError),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}