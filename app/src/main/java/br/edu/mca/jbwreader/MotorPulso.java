package br.edu.mca.jbwreader;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MotorPulso {

    private String[] bcms = {"BCM4", "BCM17", "BCM27", "BCM22"};
    private Gpio[] portas = new Gpio[bcms.length];
    private Gpio pino1;
    private Gpio pino2;
    private Gpio pino3;
    private Gpio pino4;


    public MotorPulso() {};


    public String MotorMover(String Move) {
        String Retorno = "";
        switch (Move) {
            case "moveFrente"   : { Retorno = moveFrente();   break; }
            case "moveTraz"     : { Retorno = moveTraz();     break; }
            case "moveDireita"  : { Retorno = moveDireita();  break; }
            case "moveEsquerda" : { Retorno = moveEsquerda(); break; }
        }
     return Retorno;
    }



    public String moveTraz() {
        String msg = new String();
        msg = " Carrinho movendo para Traz ";

        try {
            pino1.setActiveType(Gpio.ACTIVE_LOW);   // Motores lado Direito para Traz
            pino1.setValue(true);
            pino2.setActiveType(Gpio.ACTIVE_HIGH);
            pino2.setValue(true);

            pino3.setActiveType(Gpio.ACTIVE_HIGH);    // Motores lado Esquerdo Para Traz
            pino3.setValue(true);
            pino4.setActiveType(Gpio.ACTIVE_LOW);
            pino4.setValue(true);

        } catch (IOException e) {
            return e.getMessage() + " - " + msg;
        }
        return msg;
    }

    public String moveFrente() {
        String msg = new String();
        msg = " Carrinho movendo para Frente ";

        try {
            pino1.setActiveType(Gpio.ACTIVE_HIGH);   // Motores lado Direito para Frente
            pino1.setValue(true);
            pino2.setActiveType(Gpio.ACTIVE_LOW);
            pino2.setValue(true);

            pino3.setActiveType(Gpio.ACTIVE_LOW);    // Motores lado Esquerdo Para Frente
            pino3.setValue(true);
            pino4.setActiveType(Gpio.ACTIVE_HIGH);
            pino4.setValue(true);

        } catch (IOException e) {
            return e.getMessage() + " - " + msg;
        }
        return msg;
    }


    public String moveDireita() {
        String msg = new String();
        msg = " Carrinho Virando a Direita ";

        try {
            pino1.setActiveType(Gpio.ACTIVE_LOW);   // Motores lado Direito para Traz
            pino1.setValue(true);
            pino2.setActiveType(Gpio.ACTIVE_HIGH);
            pino2.setValue(true);

            pino3.setActiveType(Gpio.ACTIVE_LOW);    // Motores lado Esquerdo Para Frente
            pino3.setValue(true);
            pino4.setActiveType(Gpio.ACTIVE_HIGH);
            pino4.setValue(true);

        } catch (IOException e) {
            return e.getMessage() + " - " + msg;
        }
        return msg;
    }

    public String moveEsquerda() {
        String msg = new String();
        msg = " Carrinho Virando a Esquerda ";

        try {
            pino1.setActiveType(Gpio.ACTIVE_HIGH);   // Motores lado Direito para Fremte
            pino1.setValue(true);
            pino2.setActiveType(Gpio.ACTIVE_LOW);
            pino2.setValue(true);

            pino3.setActiveType(Gpio.ACTIVE_HIGH);    // Motores lado Esquerdo Para Traz
            pino3.setValue(true);
            pino4.setActiveType(Gpio.ACTIVE_LOW);
            pino4.setValue(true);

        } catch (IOException e) {
            return e.getMessage() + " - " + msg;
        }
        return msg;
    }


    public void definePinosMotor() {
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            for (int i = 0; i < portas.length; i++) {
                portas[i] = manager.openGpio(bcms[i]);
                portas[i].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

                pino1 = portas[0]; //Motores lado Esquerdo Frente
                pino2 = portas[1];

                pino3 = portas[2]; //Motores lado Direito Traz
                pino4 = portas[3];

            }
        } catch (IOException e) {
            e.getMessage();
        }

    }

    public String Break() {
        String msg = new String();
        msg = " Motores Parados ";

        try {
            pino1.setActiveType(Gpio.ACTIVE_LOW);   // Motores lado Direito Parados
            pino1.setValue(true);
            pino2.setActiveType(Gpio.ACTIVE_LOW);
            pino2.setValue(true);

            pino3.setActiveType(Gpio.ACTIVE_LOW);    // Motores lado Esquerdo Parados
            pino3.setValue(true);
            pino4.setActiveType(Gpio.ACTIVE_LOW);
            pino4.setValue(true);

        } catch (IOException e) {
            return e.getMessage() + " - " + msg;
        }
        return msg;
    }


}