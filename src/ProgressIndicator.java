import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by qiaoruixiang on 22/05/2017.
 */
public class ProgressIndicator {

    private JFrame mainFrame;
    private JProgressBar progressBar;
    private static volatile ProgressIndicator instance;

    private String text;
    private int value;
    private int max;

    private ProgressIndicator() {
    }

    public static ProgressIndicator getInstance() {
        if (instance == null) {
            synchronized (ProgressIndicator.class) {
                if (instance == null) {
                    instance = new ProgressIndicator();
                }
            }
        }
        return instance;
    }

    public void init() {
        mainFrame = new JFrame("Program");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        Container content = mainFrame.getContentPane();
        progressBar = new JProgressBar();
        progressBar.setString("");
        progressBar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Running...");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        mainFrame.setSize(500, 100);
        mainFrame.setVisible(true);
    }

    public void setText(String s) {
        text = s;
        updateStatus();
    }

    public void setValue(int v) {
        value = v;
        updateStatus();
    }

    public void setMax(int m) {
        max = m;
        //updateStatus();
    }

    private void updateStatus() {
        if (max != 0) {
            progressBar.setValue(value/max);
        }
        progressBar.setString(text + " " + value + "/" + max);
    }

    public void close() {
        mainFrame.setVisible(false);
    }

}
