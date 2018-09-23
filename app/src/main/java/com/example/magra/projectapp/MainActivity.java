package com.example.magra.projectapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    final Context context = this;
    Gson gson = new Gson();
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<Boolean> boolList = new ArrayList<>();
    int waterAmt;
    int foodAmt;
    int defaultItems;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Use the chosen theme
        sharedPreferences = getSharedPreferences("com.example.magra.projectapp", MODE_PRIVATE);
        boolean useDarkTheme = sharedPreferences.getBoolean("darkTheme", false);

        if (useDarkTheme) {
            setTheme(R.style.MainTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultItems = 14;

        // Link num / denom TextViews for water and food:
        final TextView waterNum = findViewById(R.id.waterNum);
        final TextView foodNum = findViewById(R.id.foodNum);
        TextView waterDenom = findViewById(R.id.waterDenom);
        TextView foodDenom = findViewById(R.id.foodDenom);

        // Calculate water:
        int daysAbsent = sharedPreferences.getInt("daysAbsentAsInt", 0);
        int members = sharedPreferences.getInt("membersInFamilyAsInt", 0);
        int neededWater = (daysAbsent * members);
        waterAmt = sharedPreferences.getInt("waterAmt", 0);
        waterNum.setText(String.valueOf(sharedPreferences.getInt("waterAmt", 0)));
        waterDenom.setText(String.valueOf(neededWater));

        // Calculate food:
        int neededFood = (daysAbsent * members);
        foodAmt = sharedPreferences.getInt("foodAmt", 0);
        foodNum.setText(String.valueOf(sharedPreferences.getInt("foodAmt", 0)));
        foodDenom.setText(String.valueOf(neededFood));

        // Create and link buttons:
        Button resetButton = findViewById(R.id.resetButton);
        Button addItemButton = findViewById(R.id.addItemButton);

        // Create the main list view:
        final ListView myListView = findViewById(R.id.listView);

        // Either populate the arrayList or return the saved:
        if (sharedPreferences.getString("arrayList", null) == null) {
            populate();
        } else {
            String jsonArrayList = sharedPreferences.getString("arrayList", "");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            arrayList = gson.fromJson(jsonArrayList, type);

            String jsonBoolList = sharedPreferences.getString("boolList", "");
            Type type2 = new TypeToken<ArrayList<Boolean>>() {
            }.getType();
            boolList = gson.fromJson(jsonBoolList, type2);

        }

        // Create the Array Adapter:
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, arrayList);

        // Make list a checked list and set the adapter:
        myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myListView.setAdapter(arrayAdapter);

        // Get bool list and iterate through the listView to setItemChecked if bool value is true:
        for (int position = 0; position < boolList.size(); position++) {
            // if position of i is true / listView.setItem checked to true:
            if (boolList.get(position)) {
                myListView.setItemChecked(position, true);
            } else {
                myListView.setItemChecked(position, false);
            }
        }

        // When list item clicked set the boolean value to true or false:
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Check the value of the bool to determine if checked or unchecked:

                if (boolList.get(position)) {
                    myListView.setItemChecked(position, false);
                    boolList.add(position, false);

                } else {
                    myListView.setItemChecked(position, true);
                    boolList.add(position, true);

                }
            }
        });

        // Implement delete item when held down:
        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                if (position > defaultItems) {
                    Toast.makeText(getApplicationContext(), arrayList.get(position) + " deleted.", Toast.LENGTH_LONG).show();
                    //arrayAdapter.remove(arrayList.get(position));
                    removeItem(position);
                    arrayAdapter.notifyDataSetChanged();
                    return true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Cannot be deleted.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

        });

        // Implement the reset button:
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < myListView.getAdapter().getCount(); i++) {

                    myListView.setItemChecked(i, false);
                    boolList.add(i, false);
                }
                // set instance variables to zero, commit them to shared pref, and setText equal to new:
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                waterAmt = 0;
                foodAmt = 0;
                prefEditor.putInt("waterAmt", waterAmt);
                prefEditor.putInt("foodAmt", foodAmt);
                prefEditor.apply();
                waterNum.setText(String.valueOf(waterAmt));
                foodNum.setText(String.valueOf(foodAmt));
            }
        });

        // Implement the add item button:
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting prompts.xml layout:
                final ViewGroup parent = null;
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, parent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // Set prompts.xml to AlertDialog Builder:
                alertDialogBuilder.setView(promptsView);

                final EditText itemToAdd = promptsView.findViewById(R.id.editTextResult);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add item",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Add item to the arrayAdapter:
                                        addItem(itemToAdd.getText().toString().trim());
                                        arrayAdapter.notifyDataSetChanged();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                // Create alert dialog object:
                AlertDialog alertDialog = alertDialogBuilder.create();

                // Show Dialog box:
                alertDialog.show();

            }
        });
    }

    // Initialize the arrayList and boolList with default items:
    public void populate() {
        addItem("Battery Powered Radio");
        addItem("First-Aid Kit");
        addItem("Extra Batteries");
        addItem("Whistle");
        addItem("Dust-Mask");
        addItem("Plastic Sheeting");
        addItem("Duct Tape");
        addItem("Moist Towelettes");
        addItem("Garbage Bags");
        addItem("Wrench/Pliers");
        addItem("Manual Can Opener");
        addItem("Local Maps");
        addItem("Cellphone with charger");
        addItem("Matches/Lighter");
        addItem("Flashlight");

        // Total defaultItems:
        // This is used to guarantee the user can't delete defaulted items:
        defaultItems = 14;
    }

    // Add item to both boolList and ArrayList:
    public void addItem(String string) {
        arrayList.add(string);
        boolList.add(false);

        // After adding new item, update the SharedPreferences with new arrays:
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        String json = gson.toJson(boolList);
        prefEditor.putString("boolList", json);
        prefEditor.apply();
        
        json = gson.toJson(arrayList);
        prefEditor.putString("arrayList", json);
        prefEditor.apply();
    }

    // Remove item in both boolList and ArrayList:
    public void removeItem(int index) {
        arrayList.remove(index);
        boolList.remove(index);

        // After deleting, update the SharedPreferences with new arrays:
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        String json = gson.toJson(boolList);
        prefEditor.putString("boolList", json);
        prefEditor.apply();

        json = gson.toJson(arrayList);
        prefEditor.putString("arrayList", json);
        prefEditor.apply();
    }

    public void toMenu(View view) {
        // Save the lists using Gson:
        // boolList
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        String json = gson.toJson(boolList);
        prefEditor.putString("boolList", json);
        prefEditor.apply();

        // arrayList
        json = gson.toJson(arrayList);
        prefEditor.putString("arrayList", json);
        prefEditor.apply();

        // To Menu Activity:
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }

    // Increment water numerator:
    public void increaseWater(View view) {
        TextView waterNum = findViewById(R.id.waterNum);
        waterAmt++;
        waterNum.setText(String.valueOf(waterAmt));
        saveWater();
    }

    // Decrement water numerator:
    public void decreaseWater(View view) {
        TextView waterNum = findViewById(R.id.waterNum);
        if (waterAmt == 0) {
            waterNum.setText(String.valueOf(waterAmt));
        }
        else {
            waterAmt--;
            waterNum.setText(String.valueOf(waterAmt));
        }
        saveWater();
    }

    // Update sharedPref:
    public void saveWater() {
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt("waterAmt", waterAmt);
        prefEditor.apply();
    }

    // Increment food numerator:
    public void increaseFood(View view) {
        TextView foodNum = findViewById(R.id.foodNum);
        foodAmt++;
        foodNum.setText(String.valueOf(foodAmt));
        saveFood();
    }

    // Decrement food numerator:
    public void decreaseFood(View view) {
        TextView foodNum = findViewById(R.id.foodNum);
        if (foodAmt == 0) {
            foodNum.setText(String.valueOf(foodAmt));
        }
        else {
            foodAmt--;
            foodNum.setText(String.valueOf(foodAmt));
        }
        saveFood();
    }

    // Update sharedPref:
    public void saveFood() {
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt("foodAmt", foodAmt);
        prefEditor.apply();
    }
}

