import java.util.*;
class tempSensors implements Runnable{
    private int num;
    int minutes;
    int[] sensorTemps;
    int temp;


    public tempSensors(int num, int[] sensorTemps){
        this.num = num;
        this.minutes = 0;
        this.sensorTemps = sensorTemps;
        this.temp = 0;
    }

    @Override
    public void run(){
        while(minutes < 60){
            temp = (int)(Math.random()*170)-100;
            sensorTemps[num+(8*minutes)] = temp;
            minutes++;
        }
    }
}

public class TempModule {

//8 sensors
//report every hour - 
    //top 5 temps, bottom 5 temps,
    //10 min interbal when the largest temp difference was the largest
//8 threads
//temp readings are every minute
    //randomly generated -100F-70F

    int[] sensorTemps;
    int[] highs;
    int[] lows;
    int[] flux;

    public static void main(String[] args){
        TempModule tempModule = new TempModule();

        tempModule.sensorTemps = new int[480];
        tempModule.highs = new int[5];
        Arrays.fill(tempModule.highs, -170);
        tempModule.lows = new int[5];
        Arrays.fill(tempModule.lows, 70);
        tempModule.flux = new int[3];
        Arrays.fill(tempModule.flux, 0);

        ArrayList<Thread> sensors = new ArrayList<Thread>(8);
        Thread th;

        for(int i = 0; i < 8; i++){
            tempSensors  sensorThread = new tempSensors(i, tempModule.sensorTemps);
            th = new Thread(sensorThread);
            sensors.add(th);
            th.start();
        }

        for(Thread t : sensors){
            try {
                t.join();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        int[] min = new int[2];
        int[] max = new int[2];

        for(int i = 0; i < 480; i++){
            min[0] = 100;
            min[1] = 0;

            max[0] = -170;
            max[1] = 0;

            for(int j = 0; j < 5; j++){
                if(tempModule.highs[j] < min[0]){
                    min[0] = tempModule.highs[j];
                    min[1] = j;
                }

                if(tempModule.lows[j] > max[0]){
                    max[0] = tempModule.lows[j];
                    max[1] = j;
                }
            }

            if(tempModule.sensorTemps[i] > min[0]){
                tempModule.highs[min[1]] = tempModule.sensorTemps[i];
            }

            if(tempModule.sensorTemps[i] < max[0]){
                tempModule.lows[max[1]] = tempModule.sensorTemps[i];
            }

            if(i+80 < 480){
                if((tempModule.sensorTemps[i+80] - tempModule.sensorTemps[i]) > tempModule.flux[2]){
                    int m = (i-(i%8))/8;
                    tempModule.flux[0] = m;
                    tempModule.flux[1] = m+10;
                    tempModule.flux[2] = (tempModule.sensorTemps[i+80] - tempModule.sensorTemps[i]);
                }
            }
        }

        Arrays.sort(tempModule.highs);
        Arrays.sort(tempModule.lows);

        System.out.println(Arrays.toString(tempModule.sensorTemps)+"\n");
        System.out.println("Hour Report:\n Highs: "+Arrays.toString(tempModule.highs)+"\n Lows: "+Arrays.toString(tempModule.lows)+"\n Largest 10 min. temperature fluxuation at: "+tempModule.flux[0]+" to "+tempModule.flux[1]+" minutes");
    }
}
