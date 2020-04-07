package com.example.labandroid2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static String IP = "ip";
    public static String NICK = "nick";

    private EditText iPAddressInput;
    private EditText nickNameInput;
    private Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iPAddressInput = (EditText) findViewById(R.id.IPAddressInput);
        nickNameInput = (EditText) findViewById(R.id.nickNameInput);
        startButton = (Button) findViewById(R.id.startButton);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SimpleChatActivity.class);
//                intent.putExtra(IP, iPAddressInput.getText().toString());
//                intent.putExtra(NICK, nickNameInput.getText().toString());
//                startActivity(intent);
            }
        });

    }
}
