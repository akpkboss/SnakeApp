package com.abhinavpola.snakegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    TextView greetings;
    TextView highScorelabel;
    public static int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        greetings = (TextView) findViewById(R.id.textView);
        greetings.setText("Hello, " + MainActivity.account.getDisplayName() + "!");
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
