package lewis.problemset4;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * Lewis Zwart
 * 10251057
 *
 * Shows an overview of the current TO DO lists. Lists can be added or removed, and by clicking
 * a list, the user is directed to the activity ShowList where the specific list is shown.
 */
public class MainActivity extends AppCompatActivity {

    // define global variables
    AppState currentState;
    Button addListButton;
    EditText newListEditText;
    ListView toDoListsView;
    RowAdapter adapter;

    /*
     * Restores the previous app state if there was one, and shows a list of current TO DO lists.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // link layout elements to variables
        toDoListsView = (ListView) findViewById(R.id.listsListView);
        addListButton = (Button) findViewById(R.id.addToDoListButton);
        newListEditText = (EditText) findViewById(R.id.newToDoEditText);

        // get current state
        loadAppState();

        // if application was left while viewing a specific list, show that list in ShowList
        if (currentState.getPosition() != -1) {
            Intent intent = new Intent(this, ShowList.class);
            startActivity(intent);
        }

        // show list of all TO DO lists
        adapter = new RowAdapter(this, currentState.getCurrentToDoList());
        toDoListsView.setAdapter(adapter);

        setListeners();
    }

    /*
     * Sets the click listeners on the listview elements and the add button.
     */
    public void setListeners() {

        // when the add button is clicked, create new TO DO list with the name given in the textfield
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listName = newListEditText.getText().toString();

                // if nonempty name is given, add a TO DO list with that name
                if (!listName.equals("")) {

                    // add list to state
                    currentState.addToState(listName);
                    currentState.saveState(getApplicationContext());

                    // adjust information on screen
                    newListEditText.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // when a TO DO list is clicked, go to that list in the ShowList activity
        toDoListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // save the new position
                currentState.setPosition(position);
                currentState.saveState(getApplicationContext());

                // show list in ShowList activity
                Intent intent = new Intent(getApplicationContext(), ShowList.class);
                startActivity(intent);
            }
        });

        // when a TO DO list is long-pressed, remove it from list after confirmation of user
        toDoListsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String item = currentState.getCurrentToDoList().get(position);

                // create alert dialog to get confirmation from user
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.sureList) + item + getString(R.string.fromList));

                // if yes is clicked, remove item from list
                builder.setPositiveButton(R.string.yesSure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // adjust state
                        currentState.removeItem(position);
                        currentState.saveState(MainActivity.this);

                        // remove list from listview
                        adapter.notifyDataSetChanged();
                    }
                });

                // if no is clicked, do nothing
                builder.setNegativeButton(R.string.noNotSure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int it) {
                    }
                });

                // show alert dialog
                AlertDialog confirmDelete = builder.create();
                confirmDelete.show();

                return true;
            }
        });
    }

    /*
    * Open former TO DO list state from toDoApp.data; if file not found, use empty AppState.
    */
    public void loadAppState(){

        try{
            // open file
            FileInputStream inputStream = openFileInput("toDoApp.data");
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            // get state
            currentState = (AppState) objectStream.readObject();

            // close file
            objectStream.close();
            inputStream.close();

        }
        // on first usage, file does not exist, so don't show error to user if file not found
        catch (ClassNotFoundException e) {
            currentState = new AppState();
            e.printStackTrace();
        }
        catch (IOException e) {
            currentState = new AppState();
            e.printStackTrace();
        }
    }

    /*
     * Exits application if back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
