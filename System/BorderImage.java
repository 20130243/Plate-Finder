package System;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;

public class BorderImage {
	private int threshold = 100;
	private double Min_char = 0.01;
	private double Max_char = 0.09;
	private Random rng = new Random(12345);

	private int RESIZED_IMAGE_WIDTH = 20;
	private int RESIZED_IMAGE_HEIGHT = 30;

	public void Border(Mat ThImage) {

		Mat srcGray = new Mat();
		Imgproc.cvtColor(ThImage, srcGray, Imgproc.COLOR_BGR2GRAY); // Đổi sang không gian màu xám
		Imgproc.blur(srcGray, srcGray, new Size(3, 3));
		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\1.jpg", srcGray); // làm mờ

		Mat cannyOutput = new Mat();
		Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2); // tìm các cạnh
		Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3); // tạo 1 mat là khung
		// chuỗi các contour
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\2.jpg", hierarchy);
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
		System.out.println(screenCnt.size());
		for (MatOfPoint2f matOfPoint2f : screenCnt) {

			getCharacter(matOfPoint2f);
		}

		Imgcodecs.imwrite("D:\\ki5\\AI\\AI\\Giuaki\\Image\\test123.jpg", drawing);
	}

	public void getCharacter(Mat drawing) {
		System.out.println("lan 1 ");
		Mat srcGray = new Mat();
		Imgproc.cvtColor(drawing, srcGray, Imgproc.COLOR_BGR2GRAY); // Đổi sang không gian màu xám
		Imgproc.blur(srcGray, srcGray, new Size(3, 3));
		Mat cannyOutput = new Mat();
		Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2); // tìm các cạnh
		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\4.jpg", cannyOutput);
		Mat roi = drawing;
		int roiarea = roi.width() * roi.height();
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Mat thre_mor = new Mat();

		// drawing chua thay bang anh da xoay va crop
		// tim contour cua cac ki tu
		Imgproc.morphologyEx(drawing, thre_mor, Imgproc.MORPH_DILATE, kernel);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(drawing, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		System.out.println("lan 2 ");
		List<Integer> char_x = new ArrayList<Integer>();
		Map<Integer, Integer> char_x_ind = new HashMap<Integer, Integer>();
		for (int i = 0; i < contours.size(); i++) {

			// ve contour cho tung ki tu
			Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
			Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, new Point());

			Rect rectangle = Imgproc.boundingRect(contours.get(i));
			int x = rectangle.x;
			int y = rectangle.y;

			int w = rectangle.width;
			int h = rectangle.height;

			double ratiochar = w / h;
			double char_area = w * h;
			System.out.println("lan 3 ");

			if (Min_char * roiarea < char_area && char_area < Max_char * roiarea && 0.25 < ratiochar
					&& ratiochar < 0.7) {
				if (char_x.contains(x)) {
					x = x + 1;
				}
				char_x.add(x);
				char_x_ind.put(x, i);

			}

			// nhan dang ki tu
			Collections.sort(char_x, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					if (o1 < o2) {
						return 1;
					} else {
						if (o1 == o2) {
							return 0;
						} else {
							return -1;
						}
					}
				}
			});

			String strFinalString = "";
			String first_line = "";
			String second_line = "";
			for (int j = 0; j < char_x.size(); j++) {
				rectangle = Imgproc.boundingRect(contours.get(i));
				x = rectangle.x;
				y = rectangle.y;
				w = rectangle.width;
				h = rectangle.height;

				System.out.println("lan 4 ");
				// cắt chữ
				Imgproc.rectangle(roi, new Point(x, y), new Point(x + w, y + h), color, j);
				Mat imgROI = thre_mor.submat(y, y + w, x, x + h);
				// resize anh
				Mat imgROIResize = new Mat();
				Imgproc.resize(imgROI, imgROIResize, new Size(RESIZED_IMAGE_WIDTH, RESIZED_IMAGE_HEIGHT));
				imgROIResize.reshape(1, RESIZED_IMAGE_WIDTH * RESIZED_IMAGE_HEIGHT);

				imgROIResize.convertTo(imgROIResize, CvType.CV_32F);

				KNearest knn = KNearest.create();
				Mat flatten = Imgcodecs
						.imread("C:\\Users\\USER\\eclipse-workspace\\FindPlate\\src\\System\\flattened_images.txt");
				Mat classification = Imgcodecs
						.imread("C:\\Users\\USER\\eclipse-workspace\\FindPlate\\src\\System\\classifications.txt");
				knn.train(flatten, Ml.ROW_SAMPLE, classification);

				Float charset = knn.findNearest(classification, 3, imgROIResize);
//				char vOut = Character.valueOf(charset);
				if (y < h / 3) {
					first_line = first_line + charset;

				} else {
					second_line = second_line + charset;
				}
			}
			System.out.println(first_line);
			System.out.println(second_line);

		}

	}

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Imgcodecs.imread("C:\\Users\\USER\\Downloads\\imgThreshplate.jpg");
		new BorderImage().Border(mat);

//		Imgproc.rectangle(mat, new Point(10, 10), new Point(100, 100), new Scalar(0, 255, 0));
//		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\hehe.jpg", mat);
	}

}
