package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RetakePhoto extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 2018;

    ImageView imgView;
    Button btnRetake;
    Button btnConfirm;

    ArrayList<Integer> scores;
    String studentName;
//    Bitmap photo;

    Uri fileURI;
    Mat srcOrig, src;
    int scaleFactor;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(!OpenCVLoader.initDebug()) {
            OpenCVLoader.initDebug();
            Log.e("OpenCV", "Should work now lol");
        }

        Intent intent = getIntent();
        this.studentName = intent.getStringExtra("Name");
        this.scores = intent.getIntegerArrayListExtra("PartialScores");

        this.imgView = (ImageView) findViewById(R.id.imageView1);

        this.btnRetake = (Button) findViewById(R.id.btnPhoto);
        this.btnConfirm = (Button) findViewById(R.id.btnConfirm);
    }

    public void retake(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Projectives";
        File imagesFolder = new File(file_path);
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "figureDrawing.jpg");
        this.fileURI = Uri.fromFile(image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void submit(View view) {

        Intent intent = new Intent(this, CheckStudentsActivity.class);
        intent.putExtra("Name", this.studentName);
        intent.putIntegerArrayListExtra("PartialScores", this.scores);
        intent.putExtra("FigureDrawing", true);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("File URI", this.fileURI.getPath());
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.fileURI);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            this.srcOrig = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
            this.src = new Mat();
            Utils.bitmapToMat(selectedImage, srcOrig);

            this.scaleFactor = calcScaleFactor(srcOrig.rows(), srcOrig.cols());

            Imgproc.resize(srcOrig, src, new Size(srcOrig.rows() / scaleFactor, srcOrig.cols() / scaleFactor));
            this.getDrawing();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static int calcScaleFactor(int rows, int cols) {
        int idealRow, idealCol;

        if(rows < cols) {
            idealRow = 240;
            idealCol = 320;
        }
        else {
            idealCol = 240;
            idealRow = 320;
        }

        int val = Math.min(rows/idealRow, cols/idealCol);

        if(val <= 0)
            return 1;
        else
            return val;
    }

    private void getDrawing() {
        //I: Prepare image
        Mat samples = new Mat(src.rows()*src.cols(), 3, CvType.CV_32F);
        for(int y = 0; y < src.rows(); y++) {
            for(int x = 0; x < src.cols(); x++) {
                for(int z = 0; z < 3; z++) {
                    samples.put(x+(y*src.cols()), z, src.get(y,x)[z]);
                }
            }
        }

        //II: K-means; background and foreground are two different clusters
        int clusterCount = 2;
        Mat labels = new Mat();
        int attempts = 5;
        Mat centers = new Mat();
        Core.kmeans(samples, clusterCount, labels,
                    new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS, 10000, 0.0001),
                    attempts, Core.KMEANS_PP_CENTERS, centers);

        //III: Find Eucledian distance between center and pure white
        //If dstCenter is closer to pure white, dstCenter is foreground
        double dstCenter0 = calcWhiteDist(centers.get(0, 0)[0], centers.get(0, 1)[0], centers.get(0, 2)[0]);
        double dstCenter1 = calcWhiteDist(centers.get(1, 0)[0], centers.get(1, 1)[0], centers.get(1, 2)[0]);
        int paperCluster = (dstCenter0 < dstCenter1) ? 0 : 1;

        Mat srcRes = new Mat(src.size(), src.type());
        Mat srcGray = new Mat();

        //IV: Segmentation; FG: white; BG: black
        for(int y = 0; y < src.rows(); y++) {
            for(int x = 0; x < src.cols(); x++) {
                int cluster_idx = (int) labels.get(x+(y*src.cols()), 0)[0];
                if(cluster_idx != paperCluster)
                    srcRes.put(y, x, 0, 0, 0, 255);
                else
                    srcRes.put(y, x, 255, 255, 255, 255);
            }
        }

        //V: Contour Detection
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(srcGray, srcGray, 50, 150);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(srcGray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //VI: Assume that the paper occupies the biggest part of the FG
//        int index = 0;
//        double maxim = Imgproc.contourArea(contours.get(0));
//        for(int contourIdx = 1; contourIdx < contours.size(); contourIdx++) {
//            double temp;
//            temp = Imgproc.contourArea(contours.get(contourIdx));
//            if(maxim < temp) {
//                maxim = temp;
//                index = contourIdx;
//            }
//        }
//        Mat drawing = Mat.zeros(srcRes.size(), CvType.CV_8UC1);
//        Imgproc.drawContours(drawing, contours, index, new Scalar(255));

        Collections.reverse(contours);
        List<MatOfPoint> cnts = contours.subList(0, 5);

        MatOfPoint approx = new MatOfPoint();
        MatOfPoint2f approx2f = new MatOfPoint2f();

        for(MatOfPoint cnt : cnts) {
            MatOfPoint2f cnt2f = new MatOfPoint2f();
            cnt.convertTo(cnt2f, CvType.CV_32FC2);
            double perimeter = Imgproc.arcLength(cnt2f, true);

            Imgproc.approxPolyDP(cnt2f, approx2f, perimeter*0.04, true);
            approx2f.convertTo(approx, CvType.CV_32S);

            if(approx.size().height == 4) {
                Log.e("RetakePhoto", "Screen found");
                break;
            }
        }

        Mat warped = this.fourPointTransform(this.sortCorners(approx.toList()), this.srcOrig);
        Imgproc.cvtColor(warped, warped, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(warped, warped, 251, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        Bitmap bitmap = Bitmap.createBitmap(warped.cols(), warped.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(warped, bitmap);
        this.imgView.setImageBitmap(bitmap);
//        Log.e("RetakePhoto", "ImgView");
    }

    private Mat fourPointTransform(List<Point> points, Mat srcImage) {
        Mat src = new MatOfPoint2f(points.get(0), points.get(1), points.get(2), points.get(3));

        double aWidth = computeDistance(points.get(0), points.get(1));
        double bWidth = computeDistance(points.get(2), points.get(3));
        double maxWidth = (aWidth > bWidth) ? aWidth : bWidth;

        double aHeight = computeDistance(points.get(0), points.get(3));
        double bHeight = computeDistance(points.get(1), points.get(2));
        double maxHeight = (aHeight > bHeight) ? aHeight : bHeight;

        Mat dst = new MatOfPoint2f(new Point(0, 0), new Point(maxWidth-1, 0), new Point(maxWidth-1, maxHeight-1), new Point(0, maxHeight-1));
        Mat destImage = new Mat((int)maxHeight, (int)maxWidth, srcImage.type());
        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(srcImage, destImage, transform, destImage.size());

        return destImage;
    }

    private double computeDistance(Point p1, Point p2) {
        double diff_x = Math.pow(p1.x-p2.x, 2);
        double diff_y = Math.pow(p1.y-p2.y, 2);

        return Math.sqrt(diff_x+diff_y);
    }

    private List<Point> sortCorners(List<Point> points) {
        List<Point> scaleUp = this.scaleToOrig(points);

        List<Point> rectangle = new ArrayList<>();
        List<Double> sum = new ArrayList<>();
        List<Double> diff = new ArrayList<>();

        for(int i = 0; i < scaleUp.size(); i++) {
            double tempTotal = scaleUp.get(i).x + scaleUp.get(i).y;
            double tempDiff = scaleUp.get(i).x - scaleUp.get(i).y;
            sum.add(tempTotal);
            diff.add(tempDiff);
        }

        rectangle.add(scaleUp.get(getMin(sum))); //Top Left
        rectangle.add(scaleUp.get(getMin(diff))); //Top Right
        rectangle.add(scaleUp.get(getMax(sum))); //Bottom Right
        rectangle.add(scaleUp.get(getMax(diff))); //Bottom Left

        return rectangle;
    }

    private List<Point> scaleToOrig(List<Point> points) {
        List<Point> scaleUp = new ArrayList<>();

        for(int i = 0; i < points.size(); i++) {
            double scale_x = points.get(i).x*this.scaleFactor;
            double scale_y = points.get(i).y*this.scaleFactor;

            scaleUp.add(new Point(scale_x, scale_y));
        }

        return scaleUp;
    }

    private int getMin(List<Double> list) {
        int index = -1;
        double min = Double.MAX_VALUE;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i) < min) {
                min = list.get(i);
                index = i;
            }
        }

        return index;
    }

    private int getMax(List<Double> list) {
        int index = -1;
        double max = Double.MIN_VALUE;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i) > max) {
                max = list.get(i);
                index = i;
            }
        }

        return index;
    }

//    private void sortCorners1(ArrayList<Point> corners) {
//        ArrayList<Point> top, bottom;
//
//        top = new ArrayList<>();
//        bottom = new ArrayList<>();
//
//        Point center = new Point();
//
//        for(int i = 0; i < corners.size(); i++) {
//            center.x += corners.get(i).x/corners.size();
//            center.y += corners.get(i).y/corners.size();
//        }
//
//        for(int i = 0; i < corners.size(); i++) {
//            if(corners.get(i).y < center.y)
//                top.add(corners.get(i));
//            else
//                bottom.add(corners.get(i));
//        }
//
//        corners.clear();
//
//        if(top.size() == 2 && bottom.size() == 2) {
//            Point top_left = (top.get(0).x > top.get(1).x) ? top.get(1) : top.get(0);
//            Point top_right = (top.get(0).x > top.get(1).x) ? top.get(0) : top.get(1);
//            Point bottom_left = (bottom.get(0).x > bottom.get(1).x) ? bottom.get(1) : bottom.get(0);
//            Point bottom_right = (bottom.get(0).x > bottom.get(1).x) ? bottom.get(0) : bottom.get(1);
//
//            top_left.x *= this.scaleFactor;
//            top_left.y *= this.scaleFactor;
//
//            top_right.x *= this.scaleFactor;
//            top_right.y *= this.scaleFactor;
//
//            bottom_left.x *= this.scaleFactor;
//            bottom_left.y *= this.scaleFactor;
//
//            bottom_right.x *= this.scaleFactor;
//            bottom_right.y *= this.scaleFactor;
//
//            corners.add(top_left);
//            corners.add(top_right);
//            corners.add(bottom_right);
//            corners.add(bottom_left);
//        }
//    }

//    private boolean exists(ArrayList<Point> corners, Point pt) {
//        for(int i = 0; i < corners.size(); i++) {
//            if(Math.sqrt(Math.pow(corners.get(i).x-pt.x, 2) +
//                    Math.pow(corners.get(i).y-pt.y, 2)) < 10) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private Point findIntersection(double[] line1, double[] line2) {
//        double start_x1 = line1[0], start_y1 = line1[1], end_x1 = line1[2], end_y1 = line1[3];
//        double start_x2 = line2[0], start_y2 = line2[1], end_x2 = line2[2], end_y2 = line2[3];
//        double denominator = ((start_x1-end_x1)*(start_y2-end_y2)) - ((start_y1-end_y1)*(start_x2-end_x2));
//
//        if(denominator != 0) {
//            Point pt = new Point();
//            pt.x = ((start_x1*end_y1-start_y1*end_x1) *
//                    (start_x2-end_x2) - (start_x1-end_x1) *
//                    (start_x2*end_y2 - start_y2*end_x2)) /
//                    denominator;
//            pt.y = ((start_x1*end_y1 - start_y1*end_x1) *
//                    (start_y2-end_y2)-(start_y1-end_y1) *
//                    (start_x2*end_y2 - start_y2*end_x2)) /
//                    denominator;
//
//            return pt;
//        }
//        else
//            return new Point(-1, -1);
//
//    }

    private double calcWhiteDist(double r, double g, double b) {
        return Math.sqrt(Math.pow(255-r, 2)+Math.pow(255-g, 2)+Math.pow(255-b, 2));
    }
}
