package com.abhinavpola.snakegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainMenu extends AppCompatActivity {

    TextView greetings;
    TextView highScorelabel;
    public static int highScore;

    static DatabaseReference root = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        greetings = (TextView) findViewById(R.id.textView);
        greetings.setText("Hello, " + MainActivity.account.getDisplayName() + "!" + "\n" + MainActivity.account.getEmail());
        highScorelabel = (TextView) findViewById(R.id.textView2);

    }

    @Override
    protected void onStart() {
        super.onStart();
        highScorelabel.setText("Highscore: " + highScore);
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
