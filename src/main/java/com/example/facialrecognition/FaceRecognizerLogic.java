package com.example.facialrecognition;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


public class FaceRecognizerLogic {

    private CascadeClassifier faceCascade;
    private LBPHFaceRecognizer faceRecognizer;
    private Map<Integer, String> labelNameMap = new HashMap<>();

    public FaceRecognizerLogic() {
        try {
            // Load cascade file from resources
            URL resource = getClass().getResource("/haarcascade_frontalface_default.xml");
            if (resource == null) {
                throw new IOException("Cannot find haarcascade_frontalface_default.xml");
            }
            File cascadeFile = new File(System.getProperty("java.io.tmpdir"), "haarcascade_frontalface_default.xml");
            try (InputStream is = resource.openStream()) {
                Files.copy(is, cascadeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            this.faceCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
            this.faceRecognizer = createLBPHFaceRecognizer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trainModel() {
        File knownFacesDir = new File("known_faces");
        if (!knownFacesDir.exists() || knownFacesDir.listFiles() == null || knownFacesDir.listFiles().length == 0) {
            return; // No faces to train on
        }

        File[] faceFiles = knownFacesDir.listFiles();
        MatVector faces = new MatVector(faceFiles.length);
        Mat labels = new Mat(faceFiles.length, 1, CV_32SC1);
        IntPointer labelsBuf = labels.createIndexer().createIndexer();

        Map<String, Integer> nameLabelMap = new HashMap<>();
        int labelCounter = 0;
        int counter = 0;

        for (File faceFile : faceFiles) {
            String name = faceFile.getName().split("_")[0];
            if (!nameLabelMap.containsKey(name)) {
                nameLabelMap.put(name, labelCounter);
                labelNameMap.put(labelCounter, name);
                labelCounter++;
            }
            int label = nameLabelMap.get(name);
            Mat faceImage = imread(faceFile.getAbsolutePath(), IMREAD_GRAYSCALE);
            faces.put(counter, faceImage);
            labelsBuf.put(counter, label);
            counter++;
        }

        if (faces.size() > 1) {
            faceRecognizer.train(faces, labels);
        }
    }


    public Mat detectFaces(Mat frame) {
        RectVector faceRects = detectFaceRects(frame);
        for (int i = 0; i < faceRects.size(); i++) {
            Rect rect = faceRects.get(i);
            rectangle(frame, rect, new Scalar(0, 255, 0, 1));
            
            Mat face = new Mat(frame, rect);
            Mat grayFace = new Mat();
            cvtColor(face, grayFace, COLOR_BGR2GRAY);
            resize(grayFace, grayFace, new Size(200, 200));

            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            faceRecognizer.predict(grayFace, label, confidence);

            String name = "Unknown";
            if (confidence.get(0) < 100) { // Confidence threshold
                name = labelNameMap.getOrDefault(label.get(0), "Unknown");
            }
            putText(frame, name, new Point(rect.x(), rect.y() - 10), FONT_HERSHEY_SIMPLEX, 0.9, new Scalar(0, 255, 0, 1), 2, LINE_AA, false);
        }
        return frame;
    }

    public RectVector detectFaceRects(Mat frame) {
        Mat grayFrame = new Mat();
        cvtColor(frame, grayFrame, COLOR_BGR2GRAY);
        equalizeHist(grayFrame, grayFrame);

        RectVector faces = new RectVector();
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0, new Size(30, 30), new Size());
        return faces;
    }

    public void saveFace(Mat face, String name) {
        File knownFacesDir = new File("known_faces");
        if (!knownFacesDir.exists()) {
            knownFacesDir.mkdirs();
        }

        int i = 1;
        File faceFile;
        do {
            faceFile = new File(knownFacesDir, name + "_" + i + ".png");
            i++;
        } while (faceFile.exists());

        Mat grayFace = new Mat();
        cvtColor(face, grayFace, COLOR_BGR2GRAY);
        
        Mat resizedFace = new Mat();
        resize(grayFace, resizedFace, new Size(200, 200));

        imwrite(faceFile.getAbsolutePath(), resizedFace);
    }
}
