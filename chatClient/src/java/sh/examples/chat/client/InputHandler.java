package sh.examples.chat.client;

import java.io.BufferedReader;
import java.io.IOException;

public class InputHandler extends Thread {
    private BufferedReader br;
    public InputHandler(BufferedReader br){
        this.br = br;
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if ("QUIT".equals(line)) {
                    break;
                }
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}