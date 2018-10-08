package sh.examples.chat;

import java.io.BufferedReader;

public class InputHandler extends Thread {
    private BufferedReader br;
    public InputHandler(BufferedReader br){
        this.br = br;
    }

    @Override
    public void run() {
        super.run();
    }
}
