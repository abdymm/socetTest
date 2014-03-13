
package test.demo.ui;

import test.demo.connect.Client;
import test.demo.connect.ClientFileSendListener;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;

//由于我们在程序中要使用到File与FileFilter对象,因此要import File与FileFilter这两个类.

public class FileUploader implements ActionListener, ClientFileSendListener {
    JFrame f = null;
    JLabel label = null;
    JFileChooser fileChooser = null;
    JButton b;
    JButton upload;
    JButton disConnect;
    JLabel stateLable = null;
    Client mClient;
    String filePath = null;
    MyDialog processDialog = null;

    public FileUploader(Client client) {
        f = new JFrame("文件选择");
        f.setLayout(new GridBagLayout());
        f.setLocationRelativeTo(null);
        Container contentPane = f.getContentPane();

        GridBagConstraints c = new GridBagConstraints();
        b = new JButton("打开文件");
        b.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 20, 20);
        c.gridx = 0;
        c.gridy = 2;
        contentPane.add(b, c);

        upload = new JButton("上传文件");
        upload.addActionListener(this);
        upload.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 20, 20);
        c.gridx = 1;
        c.gridy = 2;
        contentPane.add(upload, c);

        disConnect = new JButton("断开连接");
        disConnect.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 20, 20, 20);
        c.gridx = 2;
        c.gridy = 2;
        contentPane.add(disConnect, c);

        stateLable = new JLabel("连接成功", JLabel.LEFT);
        stateLable.setPreferredSize(new Dimension(150, 30));
        c.fill = GridBagConstraints.VERTICAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 20, 0, 20); // top padding
        contentPane.add(stateLable, c);

        label = new JLabel(" ", JLabel.CENTER);
        label.setPreferredSize(new Dimension(400, 30));
        c.fill = GridBagConstraints.VERTICAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 20, 0, 20); // top padding
        contentPane.add(label, c);

        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (mClient != null) {
                    mClient.disConnect();
                }
                System.exit(0);
            }
        });

        this.mClient = client;
        client.setFileSendLitener(this);
    }

    // 处理用户按下"打开旧文件"按钮事件.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(b)) {
            fileChooser = new JFileChooser("C:\\winnt");
            fileChooser.addChoosableFileFilter(new TextFileFileter("txt"));
            int result = fileChooser.showOpenDialog(f);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                label.setText(file.getAbsolutePath() + " " + file.length() / 1024 + "kb");
                filePath = file.getAbsolutePath();
                upload.setEnabled(true);
            } else if (result == JFileChooser.CANCEL_OPTION) {
                label.setText("你没有选取文件");
            }
        } else if (e.getSource().equals(upload)) {
            createProcessDialog();
            showDialog();
        } else if (e.getSource().equals(disConnect)) {
            mClient.disConnect();
            System.exit(0);
        }
    }

    private void createProcessDialog() {
        processDialog = new MyDialog();
        processDialog.setOpaque(true);
    }

    private void showDialog() {
        JDialog dialog = new JDialog(f);
        dialog.getContentPane().add(processDialog);
        dialog.pack();
        dialog.setSize(460, 250);
        dialog.setLocationRelativeTo(f);
        dialog.setVisible(true);
        mClient.sendFile(filePath);
    }

    @Override
    public void onError(String errorMessage) {
        if (processDialog != null) {
            processDialog.changeErrorLable(errorMessage);
        }
    }

    @Override
    public void onProcess(final String message) {
        System.out.println("onProcess" + message + (processDialog == null));
        if (processDialog != null) {
            processDialog.changeText(message);
        }
    }

    @Override
    public void sendSuccess() {

    }

    @Override
    public void changeProcess(int process) {
        // TODO Auto-generated method stub

    }
}

class TextFileFileter extends FileFilter {
    String ext;

    public TextFileFileter(String ext) {
        this.ext = ext;
    }

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            String extension = fileName.substring(index + 1).toLowerCase();
            if (extension.equals(ext))
                return true;
        }
        return false;
    }

    public String getDescription() {
        if (ext.equals("txt"))
            return "Text File(*.txt)";
        return "";
    }
}
