package System;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BorderImage {
	private int threshold = 100;
	private Random rng = new Random(12345);

	public void Border(Mat ThImage) {

		Mat srcGray = new Mat();
		Imgproc.cvtColor(ThImage, srcGray, Imgproc.COLOR_BGR2GRAY); // Đổi sang không gian màu xám
		Imgproc.blur(srcGray, srcGray, new Size(3, 3));
		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\dgrat.jpg", srcGray); // làm mờ

		Mat cannyOutput = new Mat();
		Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2); // tìm các cạnh
		Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3); // tạo 1 mat là khung
		// chuỗi các contour
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		// Sắp xếp contour giảm dần
		Collections.sort(contours, new Comparator<MatOfPoint>() {
			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				if (Imgproc.contourArea(o1) < Imgproc.contourArea(o2)) {
					return 1;
				} else {
					if (Imgproc.contourArea(o1) == Imgproc.contourArea(o2)) {
						return 0;
					} else {
						return -1;
					}
				}
			}
		});

		// list hình đa giác 4 cạnh
		List<MatOfPoint2f> screenCnt = new ArrayList<MatOfPoint2f>();

		for (int i = 0; i < 10; i++) {
			// Chu vi
			double peri = Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), false);
			// làm xấp xỉ đa giác, chỉ giữ contour có 4 cạnh
			MatOfPoint2f approx = new MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approx, 0.06 * peri, true);
			Rect boundingRect = Imgproc.boundingRect(approx);
			double ratio = boundingRect.width / boundingRect.height;
			if (approx.size().height == 4) {

				screenCnt.add(approx);
				Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
				Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, new Point());
			}
		}
		for (MatOfPoint2f cnt : screenCnt) {
			System.out.println(2);
			System.out.println(cnt.height());
			System.out.println(cnt.size());

		}

		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\drawing.jpg", drawing);
	}

	public void getCharacter(Mat drawing) {
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Mat thre_mor = null;
		// drawing chua thay bang anh da xoay va crop
		Imgproc.morphologyEx(drawing, thre_mor, threshold, kernel);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(drawing, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	}

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Imgcodecs.imread("C:\\Users\\USER\\Downloads\\imgThreshplate.jpg");
		new BorderImage().Border(mat);

//		Imgproc.rectangle(mat, new Point(10, 10), new Point(100, 100), new Scalar(0, 255, 0));
//		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\hehe.jpg", mat);
	}

}
