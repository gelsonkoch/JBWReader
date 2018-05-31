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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.edu.mca.serialviterbi.ui.Viterbi;

public class MovimentoActivity extends Activity {

    private NumberPicker npAtual;
    private NumberPicker npOrigem;
    private NumberPicker npDestino;
    private TextView txtCelula;

    private NumberPicker npObstaculo1;
    private NumberPicker npObstaculo2;
    private NumberPicker npObstaculo3;

    private Button btnPrincipal;
    private Button btnMontaMatriz;
    private Button btnIniciarMovimento;

    // adaptadores e wifi - precisam estar ativos
    private WifiManager wifiManager;

    private long TimeExecucao = 0;
    private MotorPulso motorCarrinho;
    private String MsgControle = "Célula: ";
    private String Direcao;

    private String[] bssids = new String[]{"C4:E9:84:A6:DE:BE", "30:B5:C2:DE:36:E2"}; // meus apps
    //private String[] bssids = new String[]{"F0:3E:90:76:5C:98", "F0:3E:90:76:7B:68"};//lab vermelho
    private boolean wirelessRunning = false;
    private List<ScanResult> wifiList;
    private long startTime = 0;
    private BroadcastReceiver wifiReceiver;
    private final String WIRELESS_TAG = "BWReader - WIRELESS";
    private final String WIRELESS_FILENAME = "wireless_android.csv";
    private final String INFERIDAS_FILENAME = "Celulas_Inferidas.csv";
    private final String lEITURAS_FILENAME = "Leituras_Movimento.csv";
    private int INTERACOES = 10;
    private float[][] rssis;
    private int interacao = 1;
    private Viterbi viterbi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimento);

        btnPrincipal = (Button) findViewById(R.id.btnPrincipal);

        npAtual = (NumberPicker) findViewById(R.id.npAtual);
        npOrigem = (NumberPicker) findViewById(R.id.npOrigem);
        npDestino = (NumberPicker) findViewById(R.id.npDestino);

        npObstaculo1 = (NumberPicker) findViewById(R.id.npObstaculo1);
        npObstaculo2 = (NumberPicker) findViewById(R.id.npObstaculo2);
        npObstaculo3 = (NumberPicker) findViewById(R.id.npObstaculo3);

        txtCelula = (TextView) findViewById(R.id.txtCelula);

        npAtual.setMinValue(20);
        npAtual.setMaxValue(100);

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

        // verificando estado da Wifi e solicitado habilitar se estiver inativa
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        viterbi = new Viterbi(450, 2, getResources().openRawResource(R.raw.tp450v543), getResources().openRawResource(R.raw.medias), getResources().openRawResource(R.raw.sigmas));
        Toast.makeText(getApplicationContext(), "viterbi Acabou de Carregar" , Toast.LENGTH_SHORT).show();

        INTERACOES = npAtual.getValue();
        wireless();
        Mover_carrinho();
    }

    public void Principal(View view) {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }


    public void iniciaMovimento(View view) {
        INTERACOES = npAtual.getValue();
        wireless();
    }

    /**
     * metodo wifi
     */
    private void wireless() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                registerWireless();
                int i = npAtual.getMinValue();
                while (i <= INTERACOES) {
                    if (!wirelessRunning) {
                        wirelessRunning = true;
                        wifiManager.startScan();
                        startTime = System.currentTimeMillis();
                        i++;
                        // espera o tempo especificado, se o receiver retornar
                        // coleta os dados ...
                        while (((System.currentTimeMillis() - startTime) / 1000) <= 5) ;
                        viterbi();
                        wirelessRunning = false;
                    }
                }
                 Toast.makeText(getApplicationContext(), "Fim das Interacões" , Toast.LENGTH_SHORT).show();
                wirelessRunning = false;
                unregisterWireless();
            }
        }).start();

    }


    public void Mover_carrinho() {

        Direcao = "moveFrente";
        TimeExecucao = 18700; // 360 Graus
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


    public void viterbi() {
        final int[] celulas = viterbi.viterbi(rssis);
        //gerar o log com o resultado das inferencias

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FileUtil.writeToSD("Célula Inferida = " + celulas[celulas.length - 1], INFERIDAS_FILENAME);
                txtCelula.setText("Célula Inferida =" + celulas[celulas.length - 1]);
            }
        });
    }


    /**
     * wifi discover method
     */
    private void registerWireless() {
        wifiReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent) {
                if (wirelessRunning) {
                    wifiList = wifiManager.getScanResults();
                    Log.d("MOVIMENTO ACTIVITY", wifiList.toString());
                    if (rssis == null) rssis = new float[1][2];
                    else {
                        float[][] rssisBkp = rssis;
                        rssis = new float[++interacao][2];
                        System.arraycopy(rssisBkp, 0, rssis, 0, rssisBkp.length);
                    }
                    for (int i = 0; i < bssids.length; i++) {
                        for (ScanResult result : wifiList) {
                            if (result.BSSID.equalsIgnoreCase(bssids[i])) {
                                // achou o ssid procurado
                                rssis[interacao - 1][i] = result.level;
                                // gravar rssis no log
                                break;
                            }
                        }
                    }

                    for (int i = interacao - 1; i < rssis.length; i++) {

                        FileUtil.writeToSD("RSSI Lido = " + i + ": " + rssis[i][0] + " - " + rssis[i][1], lEITURAS_FILENAME);
                    }
                    wirelessRunning = false;
                }
            }
        };

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void unregisterWireless() {
        unregisterReceiver(wifiReceiver);
    }


}
