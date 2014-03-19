package test.demo.ui;

import java.awt.Rectangle;
import java.awt.TextArea;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MyDialog extends JPanel {
    private static final long serialVersionUID = 1L;

    private TextArea errorLable;
    private JProgressBar progressBar;

    public MyDialog() {
        super();

        setLayout(null);
        // Create the demo's UI.
        progressBar = new JProgressBar();
        progressBar.setBounds(new Rectangle(5, 10, 300, 15));

        errorLable = new TextArea(20, 50);
        errorLable.setBounds(new Rectangle(5, 40, 300, 100));

        add(progressBar, null);
        add(errorLable, null);
        setBounds(new Rectangle(0, 0, 400, 200));
    }

    public void changeErrorLable(String process) {
        errorLable.append(process + "\n");
    }

    public void changeProcess(int process) {
        progressBar.setValue(process);
    }

//    //test codes
//    public static void main(String args []) {
//        JFrame dialog = new JFrame();
//        final MyDialog dialog2 = new MyDialog();
//        dialog.getContentPane().add(dialog2);
//        dialog.pack();
//        dialog.setSize(340, 180);
//        dialog.setVisible(true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 1 ;i <= 100;i++) {
//                    dialog2.changeProcess(i);
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
}