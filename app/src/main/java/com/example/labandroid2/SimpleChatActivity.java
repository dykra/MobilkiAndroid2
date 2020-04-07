package com.example.labandroid2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SimpleChatActivity extends AppCompatActivity {

    private static class MyHandler extends Handler {
        private final WeakReference<SimpleChatActivity> sActivity;

        MyHandler(SimpleChatActivity activity) {
            sActivity = new WeakReference<SimpleChatActivity>(activity);
        }

        public void handleMessage(Message msg) {
            SimpleChatActivity activity = sActivity.get();
            activity.listItems.add("[" + msg.getData().getString("NICK") + "]" +
                    msg.getData().getString("MSG"));
            activity.adapter.notifyDataSetChanged();
            activity.chatListView.setSelection(activity.listItems.size() - 1);
        }
    }
    Handler myHandler = new MyHandler(this);


    private ListView chatListView;
    private EditText messageInput;
    private Button sendButton;


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat);

        final String nick = getIntent().getStringExtra(MainActivity.NICK);
        final String ip = getIntent().getStringExtra(MainActivity.IP);

        chatListView = (ListView) findViewById(R.id.listView);
        messageInput = (EditText) findViewById(R.id.messageInput);
        sendButton = (Button) findViewById(R.id.sendButton);

        //w metodzie onCreate obslugujemy dodwanie wiadomosci do listy
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        chatListView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", "JA");
                    b.putString("MSG", messageInput.getText().toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);

                // TODO send mqqt message here
            }
        });
    }
}
