package com.example.myapplicationserver;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//https://www.youtube.com/watch?v=s-cpqfXtzTI
public class MainActivityServer extends AppCompatActivity {

    private TextView tvServerName,tvServerPort, tvStatus;
    private int serverPort = 2222;
    private String serverIP = "192.168.1.24";

    private ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_server);

        tvServerName = findViewById(R.id.tvServerIP);
        tvServerPort = findViewById(R.id.tvServerPort);
        tvStatus = findViewById(R.id.tvStatus);
//        tvServerName = findViewById(R.id.tvServerText);
//        tvServerName = findViewById(R.id.tvServerText);
//
        tvServerName.setText(serverIP.toString());
        tvServerPort.setText(String.valueOf(serverPort));

    }//onCreate


    public  void btnStartServer(View view){

        serverThread = new ServerThread();
        serverThread.startSerever();

    } //btnStartServer

    public  void btnStopServer(View view){
        serverThread.stopSerever();
    } //btnStopServer

        class ServerThread extends  Thread implements Runnable{
            private boolean serverIsRunning;
            private ServerSocket serverSocket;
            private int connectCount;

            public void connectServer(){

                serverIsRunning  = true;
                start();
            }

            @Override
            public void run() {

                try {
                    serverSocket = new ServerSocket(MainActivityServer.this.serverPort);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText("Waiting for Clients");
                        }
                    });

                    while(serverIsRunning){

                        Socket socket = serverSocket.accept();
                        connectCount++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStatus.setText("Conected to: " + socket.getInetAddress() + ":" + socket.getLocalPort());
                            }
                        });

                        PrintWriter output_Server = new PrintWriter(socket.getOutputStream());

                        output_Server.write("Welcome to Server: " + connectCount);
                        output_Server.flush();
                        socket.close();

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }//run()

            public  void stopSerever(){
                serverIsRunning = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(serverSocket != null) {
                            try {
                                serverSocket.close();
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        tvStatus.setText("Server Stopped");
                                    }
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }).start();
            }//stopServer()


            public  void startSerever(){
                serverIsRunning = true;
                start();
            }//startServer()

        }//ServerThread

}//main