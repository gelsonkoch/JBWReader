package br.edu.mca.jbwreader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;


import java.util.List;
import java.util.Scanner;

import br.edu.mca.serialviterbi.ui.Viterbi;

public class MovimentoActivity extends Activity {

    private NumberPicker npOrigem;
    private NumberPicker npDestino;

    private NumberPicker npObstaculo1;
    private NumberPicker npObstaculo2;
    private NumberPicker npObstaculo3;

    private Button btnPrincipal;
    private Button btnMontaMatriz;
    private Button btnIniciarMovimento;

    private long TimeExecucao = 0;
    private MotorPulso motorCarrinho;
    private String MsgControle;
    private String Direcao;
    private WifiManager wifiManager;

    private final String WIRELESS_TAG = "BWReader - WIRELESS";
    private final String WIRELESS_FILENAME = "wireless_android.csv";
    private BroadcastReceiver wifiReceiver;
    private boolean wirelessRunning = false;
    private String[] bssids = new String[]{"C4:E9:84:A6:DE:BE", "30:B5:C2:DE:36:E2"}; // Gelson Casa
    private List<ScanResult> wifiList;
    private final Integer Leituras = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento);

        btnPrincipal = (Button) findViewById(R.id.btnPrincipal);

        npOrigem = (NumberPicker) findViewById(R.id.npOrigem);
        npDestino = (NumberPicker) findViewById(R.id.npDestino);

        npObstaculo1 = (NumberPicker) findViewById(R.id.npObstaculo1);
        npObstaculo2 = (NumberPicker) findViewById(R.id.npObstaculo2);
        npObstaculo3 = (NumberPicker) findViewById(R.id.npObstaculo3);

        // Podemos sair da primeira célua 1 e ir até a última 450
        npOrigem.setMinValue(1);
        npOrigem.setMaxValue(450);

        npDestino.setMinValue(1);
        npDestino.setMaxValue(450);

        // aqui definimos o 1º Obstáculo entre as células 1 a 500
        npObstaculo1.setMinValue(1);
        npObstaculo1.setMaxValue(450);
        // aqui definimos o 1º Obstáculo entre as células 1 a 500
        npObstaculo2.setMinValue(1);
        npObstaculo2.setMaxValue(450);

        // aqui definimos o 1º Obstáculo entre as células 1 a 500
        npObstaculo3.setMinValue(1);
        npObstaculo3.setMaxValue(450);
    }

    public void Principal(View view) {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }


    public void MontaMatriz(View view) {
//     Intent in = new Intent(this, MatrizActivity.class);
//     startActivity(in);

        int cont = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 30; j++) {
                cont = cont + 1;
            }
        }
        Toast toast = Toast.makeText(getApplicationContext(), "Tamanho =" + cont, Toast.LENGTH_SHORT);
        toast.show();


    }

    public void Mover_carrinho(View view) {

        Direcao = "moveDireita";
        TimeExecucao = 2120; // 360 Graus
        // TimeExecucao = 1870; // 360 Graus
        // Variáves de Giro
        // 2120 milis  360 Graus
        // 1060 milis  180 Graus
        //  530 milis   90 Graus
        //  1870 milis   1 Metro para traz ou frente

        Intent intent = new Intent(this, MovimentoActivity.class);
        startActivity(intent);
        motorCarrinho = new MotorPulso();
        motorCarrinho.definePinosMotor();


        MsgControle = "Motor Ligado ->";

        new Thread(new Runnable() {

            @Override
            public void run() {

                MsgControle = MsgControle + motorCarrinho.MotorMover(Direcao);


                try {
                    Thread.sleep(TimeExecucao);
                    // 1985 milisegundos  Giro 360 Graus
                    // 1870 milisegundos  Anda   1 Metro para traz ou frente
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                motorCarrinho.Break();

            }

        }).start();
        Toast.makeText(getApplicationContext(), MsgControle + " Fim Movimento ", Toast.LENGTH_SHORT).show();
    }


    public void Viterbi(View view) {

            Toast.makeText(getApplicationContext(), MsgControle+ " Viterbi ", Toast.LENGTH_SHORT).show();

       Viterbi v = new Viterbi(450, 2, 3);
        int[] celulas = v.viterbi(new float[][]{{45, 60}});
        for (int i = 0; i < celulas.length; i++) {
            System.out.println(celulas[i]);
        }  

    }




}
