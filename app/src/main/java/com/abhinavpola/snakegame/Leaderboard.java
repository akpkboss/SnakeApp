package com.abhinavpola.snakegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Leaderboard extends AppCompatActivity {
    TextView leaders;
    String text = "";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    Map<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        leaders = findViewById(R.id.textView4);
    }

    @Override
    protected void onStart() {
        super.onStart();
        root.limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                map = new HashMap<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    map.put(String.valueOf(dsp.getKey()), dsp.getValue());
                }
                displayMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error", "The read failed: " + databaseError.getCode());
            }
        });

    }
    public void displayMap() {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();//i think this is the wrong query
            text += "\n" + key + ": " + value + "\n";
        }
        leaders.setText(text);
    }


}
