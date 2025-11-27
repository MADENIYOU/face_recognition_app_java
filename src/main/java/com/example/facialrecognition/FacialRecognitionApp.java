package com.example.facialrecognition;

import com.github.sarxos.webcam.Webcam;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.swing.JFrame;
import java.awt.image.BufferedImage;

public class FacialRecognitionApp {

    private GUI gui;
    private FaceRecognizerLogic logic;
    private Webcam webcam;
    private boolean isCameraRunning = false;
    private boolean isRegistering = false;
    private String registrationName;
    private int registrationStep = 0;
    private final String[] registrationPoses = {"Center", "Left", "Right", "Up", "Down"};
    private long lastCaptureTime = 0;
    
    private final Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
    private final OpenCVFrameConverter.ToMat toMatConverter = new OpenCVFrameConverter.ToMat();

    public FacialRecognitionApp(JFrame frame) {
        this.gui = new GUI(frame);
        this.logic = new FaceRecognizerLogic();
        this.gui.setApp(this);
        this.logic.trainModel();
    }

    public void startCamera() {
        if (!isCameraRunning) {
            webcam = Webcam.getDefault();
            if (webcam == null) {
                gui.setStatus("No webcam found.");
                return;
            }
            webcam.open();
            isCameraRunning = true;
            new Thread(this::captureFrames).start();
        }
    }

    public void stopCamera() {
        if (isCameraRunning) {
            isCameraRunning = false;
            webcam.close();
        }
    }

    private void captureFrames() {
        while (isCameraRunning) {
            BufferedImage image = webcam.getImage();
            if (image == null) {
                continue;
            }

            Mat frame = bufferedImageToMat(image);

            if (isRegistering) {
                handleRegistrationStep(frame);
            } else {
                Mat processedFrame = logic.detectFaces(frame);
                gui.updateVideoFrame(matToBufferedImage(processedFrame));
            }
        }
    }

    private void handleRegistrationStep(Mat frame) {
        if (registrationStep < registrationPoses.length) {
            String pose = registrationPoses[registrationStep];
            gui.setStatus("Please look " + pose + " and hold still.");

            if (System.currentTimeMillis() - lastCaptureTime > 2000) {
                RectVector faces = logic.detectFaceRects(frame);

                if (faces.size() > 0) {
                    Rect faceRect = faces.get(0);
                    Mat face = new Mat(frame, faceRect);
                    
                    logic.saveFace(face, registrationName);
                    gui.setStatus("Captured " + pose + " pose.");
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    lastCaptureTime = System.currentTimeMillis();
                    registrationStep++;
                } else {
                    gui.setStatus("No face detected. Please look " + pose + ".");
                }
            }
            gui.updateVideoFrame(matToBufferedImage(frame));
        } else {
            isRegistering = false;
            gui.setStatus("Registration complete. Ready for recognition.");
            logic.trainModel();
        }
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        return toMatConverter.convert(java2dConverter.convert(bi));
    }
    
    public BufferedImage matToBufferedImage(Mat mat) {
        return java2dConverter.convert(toMatConverter.convert(mat));
    }

    public void startRegistration(String name) {
        this.isRegistering = true;
        this.registrationName = name;
        this.registrationStep = 0;
        this.lastCaptureTime = System.currentTimeMillis();
        gui.setStatus("Registering " + name + ". Please look at the camera.");
    }
}