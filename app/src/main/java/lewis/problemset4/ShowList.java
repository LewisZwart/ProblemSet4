package lewis.problemset4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ShowList extends AppCompatActivity {

    // global variables
    AppState currentState;
    Button addButton;
    EditText newToDoEditText;
    ListView toDoListView;
    RowAdapter adapter;

    /*
     * Loads specific TO DO list from memory and allows user to add/remove items from it.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        // assign variables to layout elements
        addButton = (Button) findViewById(R.id.AddButton);
        toDoListView = (ListView) findViewById(R.id.ToDoListView);
        newToDoEditText = (EditText) findViewById(R.id.NewToDoEditText);
        TextView nameView = (TextView) findViewById(R.id.listNameView);

        // load current TO DO list
        loadToDoList();
        nameView.setText(currentState.getCurrentName());

        // show current TO DO list
        adapter = new RowAdapter(this, currentState.getCurrentToDoList());
        toDoListView.setAdapter(adapter);

        setListener();
    }

    /*
     * When the add button is clicked, adds the typed task to the current TO DO list.
     */
    private void setListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newItem = newToDoEditText.getText().toString();

                // if nonempty task is typed, add to TO DO list
                if (!newItem.equals("")) {

                    // add list to state
                    currentState.addToState(newItem);
                    currentState.saveState(getApplicationContext());

                    // edit screen content
                    adapter.notifyDataSetChanged();
                    newToDoEditText.setText("");
                }
            }
        });

        // when a TO DO list is pressed for a long time, remove from list after confirmation of user
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String item = currentState.getCurrentToDoList().get(position);

                // create alert dialog to get confirmation from user
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowList.this);
                builder.setMessage(getString(R.string.sure) + item + getString(R.string.fromToDo));

                // if yes is clicked, remove item from list
                builder.setPositiveButton(R.string.yesSure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentState.removeItem(position);
                        currentState.saveState(ShowList.this);
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
     * Open former TO DO list state from toDoApp.data; if file not found, use empty TO DO.
     */
    public void loadToDoList(){

        try{
            // open file
            FileInputStream inputStream = openFileInput("toDoApp.data");
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            // save state
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
     * Returns to (and reloads) main activity when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // edit state
        currentState.setPosition(-1);
        currentState.saveState(getApplicationContext());

        // go to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

