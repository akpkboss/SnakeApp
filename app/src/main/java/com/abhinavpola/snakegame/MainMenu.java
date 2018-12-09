package com.abhinavpola.snakegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenu extends AppCompatActivity {

    TextView greetings;
    TextView highScorelabel;
    public static int highScore;
    public static Integer currentScore;
    String name;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference root = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        name = MainActivity.account.getDisplayName();
        greetings = (TextView) findViewById(R.id.textView);
        greetings.setText("Hello, " + name + "!" + "\n" + MainActivity.account.getEmail());
        highScorelabel = (TextView) findViewById(R.id.textView2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        highScorelabel.setText("Most recent highscore: " + highScore);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentScore = dataSnapshot.child(name).getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error", "The read failed: " + databaseError.getCode());
            }
        });
    }

    public void playClick(View view) {
        Intent i=new Intent(MainMenu.this,
                Player.class);
        startActivity(i);
    }

    public void leaderClick(View view) {
        Intent i = new Intent(MainMenu.this,
                Leaderboard.class);
        startActivity(i);
    }

    public void settingsClick(View view) {
        Intent i = new Intent(MainMenu.this,
                Settings.class);
        startActivity(i);
    }

}
