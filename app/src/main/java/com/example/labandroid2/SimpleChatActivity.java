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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

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
                MqttMessage message = new MqttMessage(messageInput.getText().toString().getBytes());
                message.setQos(2);
                try {
                    //http://test.mosquitto.org/ -> strange topic name
                    sampleClient.publish("/asia/chat/dziwny/" + nick, message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

//        uruchamiamy MQTT w tle
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT(nick, ip);
            }
        }).start();
    }

    MqttClient sampleClient = null;

    private void startMQTT(final String nick, String ip) {
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://" + ip + ":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String[] splittedChannel = s.split("/");
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", splittedChannel[splittedChannel.length - 1]);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("/asia/chat/dziwny/#");
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
