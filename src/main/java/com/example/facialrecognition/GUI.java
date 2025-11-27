package com.example.facialrecognition;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {

    private FacialRecognitionApp app;
    private JFrame frame;
    private JLabel videoLabel;
    private JButton startButton;
    private JButton stopButton;
    private JButton registerButton;
    private JLabel statusBar;

    public GUI(JFrame frame) {
        this.frame = frame;
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);

        videoLabel = new JLabel();
        videoLabel.setPreferredSize(new Dimension(640, 480));
        mainPanel.add(videoLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start Camera");
        stopButton = new JButton("Stop Camera");
        registerButton = new JButton("Start Registration");

        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        registerButton.addActionListener(this);

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        statusBar = new JLabel("Welcome!");
        mainPanel.add(statusBar, BorderLayout.NORTH);
    }

    public void setApp(FacialRecognitionApp app) {
        this.app = app;
    }
    
    public void updateVideoFrame(java.awt.Image image) {
        videoLabel.setIcon(new javax.swing.ImageIcon(image));
    }
    
    public void setStatus(String text) {
        statusBar.setText(text);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == startButton) {
            app.startCamera();
        } else if (source == stopButton) {
            app.stopCamera();
        } else if (source == registerButton) {
            String name = JOptionPane.showInputDialog(frame, "Enter your name:");
            if (name != null && !name.isEmpty()) {
                app.startRegistration(name);
            }
        }
    }
}
