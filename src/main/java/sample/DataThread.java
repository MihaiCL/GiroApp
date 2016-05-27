package sample;

import com.fazecast.jSerialComm.SerialPort;
import gnu.io.CommPortIdentifier;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;

import java.util.Scanner;

/**
 * Created by ghita on 3/6/2016.
 */
public class DataThread implements Runnable {

    private CommPortIdentifier serialPortId = null;
    private TextArea textArea;
    private Rotate rxBox;
    private Rotate ryBox;
    private Rotate rzBox;
    private SerialPort serialPort;
    private Shape3D shape3D;
    private boolean continueRead;
    private Scanner data;

    public DataThread(SerialPort serialPort, Shape3D shape3D) {
        this.rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        this.ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        this.rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
        this.shape3D = shape3D;
        this.shape3D.getTransforms().addAll(rxBox, ryBox, rzBox);
        this.serialPort = serialPort;
        this.rxBox.setAngle(0);
        this.ryBox.setAngle(0);
        this.rzBox.setAngle(0);
        this.continueRead = true;
        this.data = null;
    }

    public void run() {

        String dataReceivedFromUSB;
        String xDegree;
        String yDegree;
        String zDegree;
        float x = 0;
        float y = 0;
        float z = 0;

        if (serialPort.openPort())
            System.out.println("Port opened successfully.");
        else {
            System.out.println("Unable to open the port.");
            return;
        }
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        data = new Scanner(serialPort.getInputStream());

        while (data.hasNext()) {
            dataReceivedFromUSB = data.nextLine();
            try{
                System.out.println(dataReceivedFromUSB);
                String[] a = dataReceivedFromUSB.split(" ");
                xDegree = a[0];
                yDegree = a[1];
                zDegree = a[2];

                x = Float.parseFloat(xDegree);
                y = Float.parseFloat(yDegree);
                z = Float.parseFloat(zDegree);

                rxBox.setAngle(x);
                ryBox.setAngle(z);
                rzBox.setAngle(y);
            }catch (Exception e){
                e.printStackTrace();
            }

            System.out.println(x + " " + y + " " + z);
            if(!continueRead){
                closeThread();
                break;
            }
        }
    }

    public void closeThread(){
        if(data != null){
            data.close();
        }
        serialPort.closePort();
    }

    public boolean isContinueRead() {
        return continueRead;
    }

    public void setContinueRead(boolean continueRead) {
        this.continueRead = continueRead;
    }
}
