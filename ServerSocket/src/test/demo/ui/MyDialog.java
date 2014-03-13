package test.demo.ui;

import java.awt.Rectangle;
import java.awt.TextArea;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyDialog extends JPanel
        implements SocketListener{
    private TextArea errorLable;
    private JLabel statusLable;

    public MyDialog() {
        super();

        setLayout(null);
        // Create the demo's UI.
        statusLable = new JLabel("status");
        statusLable.setBounds(new Rectangle(5, 10, 100, 25));

        errorLable = new TextArea(20, 50);
        errorLable.setBounds(new Rectangle(110, 10, 300, 200));


        add(statusLable, null);
        add(errorLable, null);
        setBounds(new Rectangle(0, 0, 400, 200));
    }

    @Override
    public void changeErrorLable(String process) {
        errorLable.append(process + "\n");
    }

    @Override
    public void changeText(String text) {
        statusLable.setText(text);
    }
}

interface SocketListener {
    public void changeErrorLable(String process);
    public void changeText(String text);
}
