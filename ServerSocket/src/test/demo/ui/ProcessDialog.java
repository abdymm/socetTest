
package test.demo.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProcessDialog implements ActionListener
{
    private JDialog dialog;
    private JProgressBar progressBar;
    private JLabel lbStatus;
    private JButton btnCancel;
    private Window parent;
    private String statusInfo;
    private JPanel mainPane;

    public static ProcessDialog show(Window parent, String statusInfo) {
        return new ProcessDialog(parent, statusInfo);
    }

    private ProcessDialog(Window parent, String statusInfo) {
        this.parent = parent;
        this.statusInfo = statusInfo;
        initUI();
        dialog.setVisible(true);
    }

    public void setMessage(final String message) {
        lbStatus.setText(message);
        mainPane.updateUI();
        dialog.getContentPane().add(mainPane);
    }

    private void initUI() {
        if (parent instanceof Dialog) {
            dialog = new JDialog((Dialog) parent, true);
        }
        else if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, true);
        }
        else {
            dialog = new JDialog((Frame) null, true);
        }
        mainPane = new JPanel(null);
        progressBar = new JProgressBar();
        lbStatus = new JLabel("" + statusInfo);
        btnCancel = new JButton("Cancel");
        progressBar.setIndeterminate(true);
        btnCancel.addActionListener(this);
        lbStatus.setBounds(20, 20, 350, 25);
        progressBar.setBounds(20, 50, 350, 15);
        btnCancel.setBounds(20, 70, 100, 25);
        mainPane.add(progressBar);
        mainPane.add(lbStatus);
        mainPane.add(btnCancel);
        dialog.setUndecorated(true); // 除去title
        dialog.setResizable(true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 不允许关闭
        dialog.setSize(390, 100);
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().add(mainPane);
    }

    public void actionPerformed(ActionEvent e) {
        dissmiss();
    }

    public void dissmiss() {
        dialog.dispose();
    }

    public static void main(String args []) {
        final ProcessDialog dialog = ProcessDialog.show(null, "test");
        dialog.setMessage("cao ni mei");
    }
}
