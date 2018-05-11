package br.edu.mca.jbwreader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.mca.serialviterbi.ui.Viterbi;


public class PrincipalActivity extends Activity implements OnClickListener {

    private Button btnSair;
    private Button btnColetar;
    private RadioGroup rgTipo;
    private NumberPicker npTempo;
    private NumberPicker npCelula;
    private NumberPicker npInteracao;
    private TextView lblConexao;
    private TextView lblStatus;
    private TextView lblValorRSSI;


    private List<ScanResult> wifiList;
    private long startTime = 0;

    // adaptadores e wifi - precisam estar ativos
    private WifiManager wifiManager;

    private final String WIRELESS_TAG = "BWReader - WIRELESS";
    private final String WIRELESS_FILENAME = "wireless_android.csv";
    private BroadcastReceiver wifiReceiver;

    private boolean wirelessRunning = false;
    private Integer valorRSSI = null;

    // relação de ssids de aps wifi a serem utilizados, eh preciso
    // inicializar(linksys, dlink, lcad, sala16)

    private String[] bssids = new String[]{"C4:E9:84:A6:DE:BE", "30:B5:C2:DE:36:E2"}; // Gelson Casa
//      private String[] bssids = new String[] {"FO:3E:90:36:56:9C", "F0:3E:30:36:32:B8"}; // unoesc
//        private String[] bssids = new String[] {"5A:9F:FA:A2:DB:83"};                      // haderoc

    // relacao de address de beacons a serem utilizados, eh preciso
    // inicializar
    private String[] addresses = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // vinculando views do layout
        btnSair = (Button) findViewById(R.id.btnSair);
        btnColetar = (Button) findViewById(R.id.btnColetar);
        rgTipo = (RadioGroup) findViewById(R.id.rdTipo);
        npTempo = (NumberPicker) findViewById(R.id.npTempo);
        npCelula = (NumberPicker) findViewById(R.id.npCelula);
        npInteracao = (NumberPicker) findViewById(R.id.npInteracao);
        lblConexao = (TextView) findViewById(R.id.lblConexao);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        lblValorRSSI = (TextView) findViewById(R.id.lblValorRSSI);

        // de 1 a 100 segundos de coleta
        npTempo.setMinValue(1);
        npTempo.setMaxValue(5);

        // de 1 a 100 interacões possíveis
        npInteracao.setMinValue(1);
        npInteracao.setMaxValue(500);

        // de 1 a 100 células
        npCelula.setMinValue(1);
        npCelula.setMaxValue(450);

        btnSair.setOnClickListener(this);
        btnColetar.setOnClickListener(this);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            lblStatus.setText(wifiManager.getWifiState());
        }

        // aqui vai o viterbi

    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btnSair:
                finish();
                System.exit(0);
                break;
            case R.id.btnColetar:
                if (rgTipo.getCheckedRadioButtonId() == R.id.rdWireless) {
                    wireless();
                }
        }
    }


    /**
     * metodo wifi
     */
    private void wireless() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    changeButton(false);
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                registerWireless();
                //	changeButton(false); roberson
                int i = 1;
                while (i <= npInteracao.getValue()) {
                    if (!wirelessRunning) {
                        wifiManager.startScan();
                        wirelessRunning = true;
                        startTime = System.currentTimeMillis();
                        i++;
                        // espera o tempo especificado, se o receiver retornar
                        // coleta os dados ...
                        while (((System.currentTimeMillis() - startTime) / 1000) <= npTempo.getValue());
                         wirelessRunning = false;
                    }
                }
                 wirelessRunning = false;
                unregisterWireless();
                changeButton(true);
                sound();
            }
        }).start();

    }

    /**
     * wifi discover method
     */
    private void registerWireless() {
        wifiReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent) {
                StringBuilder sb = new StringBuilder();
                if (wirelessRunning) {
                    wifiList = wifiManager.getScanResults();
                    for (int i = 0; i < bssids.length; i++) {
                        String rssi = "?,";
                        for (ScanResult result : wifiList) {
                            if (result.BSSID.equalsIgnoreCase(bssids[i])) {
                                // achou o ssid procurado
                                rssi = result.level + ",";
                                break;
                            }
                        }

                        lblStatus.setText("WIFI Ativa");  // Nosso código
                        sb.append(rssi);
                        lblValorRSSI.setText(sb.toString().substring(0, sb.length() -1));
                    }
                    Log.v(WIRELESS_TAG, "c" + npCelula.getValue() + "," + sb.toString().substring(0, sb.length() - 1));
                    FileUtil.writeToSD("c" + npCelula.getValue() + ","  + sb.toString().substring(0, sb.length() - 1), WIRELESS_FILENAME);
                    wirelessRunning = false;
                }
            }
        };

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void unregisterWireless() {
        unregisterReceiver(wifiReceiver);
    }

    private void changeButton(final boolean state) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnColetar.setEnabled(state);
            }
        });
    }

    private void sound() {
        int times = 0;
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            while (r.isPlaying() && times <= 3) {
                Thread.sleep(15000);
                times++;
            }
            r.stop();
        } catch (Exception e) {
        }
    }

    public void Movimento(View view) {
        Intent intent = new Intent(this, MovimentoActivity.class);
        startActivity(intent);
    }

}

