package lewis.problemset4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/*
 * Implements an adapter showing the given list.
 */
public class RowAdapter extends ArrayAdapter<String> {

    Context adapterContext;
    ArrayList<String> currentList;

    public RowAdapter(Context context, ArrayList<String> currentToDoList) {
        super(context, R.layout.layout_single_row, currentToDoList);

        adapterContext = context;
        currentList = currentToDoList;
    }

    /*
     * Shows items of the current TO DO list in a listview.
     */
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) adapterContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_single_row, parent, false);
        }

        // place items of TO DO list in textview
        TextView itemTextView = (TextView) view.findViewById(R.id.itemTextView);
        final String item = currentList.get(position);
        itemTextView.setText(item);

        return view;
    }
}

