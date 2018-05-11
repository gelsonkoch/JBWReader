package br.edu.mca.jbwreader;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MotorPasso {

    private String[] bcms = {"BCM4", "BCM17", "BCM27", "BCM22"};
    private Gpio[] portas = new Gpio[bcms.length];
    private Integer RPM = 0;
    private Integer Metro_segundo = 0;
    private Integer Estado = 0;

    public String[] getBcms() {
        return bcms;
    }

    public void setBcms(String[] bcms) {
        this.bcms = bcms;
    }

    public Gpio[] getPortas() {
        return portas;
    }

    public void setPortas(Gpio[] portas) {
        this.portas = portas;
    }

    public Integer getRPM() {
        return RPM;
    }

    public void setRPM(Integer RPM) {
        this.RPM = RPM;
    }

    public Integer getMetro_segundo() {
        return Metro_segundo;
    }

    public void setMetro_segundo(Integer metro_segundo) {
        Metro_segundo = metro_segundo;
    }

    public Integer getEstado() {
        return Estado;
    }

    public void setEstado(Integer estado) {
        Estado = estado;
    }

    public MotorPasso() {
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            for (int i = 0; i < portas.length; i++) {
                portas[i] = manager.openGpio(bcms[i]);
                portas[i].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            }


            Move(false, 5, 2000);


        } catch (IOException ex) {
            ex.getMessage();
        }
    }


    public Boolean Move(boolean Direction, Integer Velocidade, int Passo) throws IOException {
        Gpio PA = portas[0];
        Gpio PB = portas[1];
        Gpio PC = portas[2];
        Gpio PD = portas[3];

        if (Passo > 0) {
            int Pulso = 1;
            for (int i = Passo; i >= 0; i--) {

                Metro_segundo = (int) (Velocidade * 2.083333);

                switch (Pulso) {
                    case 1: {

                        if (Direction == true) {
                            PB.setActiveType(Gpio.ACTIVE_LOW);
                            PB.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_LOW);
                            PD.setValue(true);
                            PA.setActiveType(Gpio.ACTIVE_HIGH);
                            PA.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_HIGH);
                            PC.setValue(true);
                        } else {
                            PB.setActiveType(Gpio.ACTIVE_LOW);
                            PB.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_LOW);
                            PC.setValue(true);
                            PA.setActiveType(Gpio.ACTIVE_HIGH);
                            PA.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_HIGH);
                            PD.setValue(true);
                        }

                        Pulso = 2;
                        try {
                            Thread.sleep(Metro_segundo);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    case 2: {
                        if (Direction == true) {
                            PA.setActiveType(Gpio.ACTIVE_LOW);
                            PA.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_LOW);
                            PD.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_HIGH);
                            PC.setValue(true);
                            PB.setActiveType(Gpio.ACTIVE_HIGH);
                            PB.setValue(true);
                        } else {
                            PA.setActiveType(Gpio.ACTIVE_LOW);
                            PA.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_LOW);
                            PC.setValue(true);
                            PB.setActiveType(Gpio.ACTIVE_HIGH);
                            PB.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_HIGH);
                            PD.setValue(true);
                        }

                        Pulso = 3;
                        try {
                            Thread.sleep(Metro_segundo);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    case 3: {
                        if (Direction == true) {
                            PA.setActiveType(Gpio.ACTIVE_LOW);
                            PA.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_LOW);
                            PC.setValue(true);
                            PB.setActiveType(Gpio.ACTIVE_HIGH);
                            PB.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_HIGH);
                            PD.setValue(true);
                        } else {
                            PA.setActiveType(Gpio.ACTIVE_LOW);
                            PA.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_LOW);
                            PD.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_HIGH);
                            PC.setValue(true);
                            PB.setActiveType(Gpio.ACTIVE_HIGH);
                            PB.setValue(true);
                        }

                        Pulso = 4;
                        try {
                            Thread.sleep(Metro_segundo);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    case 4: {
                        if (Direction == true) {
                            PB.setActiveType(Gpio.ACTIVE_LOW);
                            PB.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_LOW);
                            PC.setValue(true);
                            PA.setActiveType(Gpio.ACTIVE_HIGH);
                            PA.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_HIGH);
                            PD.setValue(true);

                        } else {
                            PB.setActiveType(Gpio.ACTIVE_LOW);
                            PB.setValue(true);
                            PD.setActiveType(Gpio.ACTIVE_LOW);
                            PD.setValue(true);
                            PA.setActiveType(Gpio.ACTIVE_HIGH);
                            PA.setValue(true);
                            PC.setActiveType(Gpio.ACTIVE_HIGH);
                            PC.setValue(true);
                        }

                        Pulso = 1;
                        try {
                            Thread.sleep(Metro_segundo);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }


                }
            }

        }
        return true;
    }


}
