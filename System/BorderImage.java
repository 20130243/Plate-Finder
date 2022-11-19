package System;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BorderImage {
	

	public void Border(Mat ThImage) {
		long a = ThImage.total();
		long b = ThImage.elemSize1();
		System.out.println(a);
		System.out.println(b);
	}

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Imgcodecs.imread("C:\\Users\\USER\\Downloads\\314477857_674696020758845_4558010974948644436_n.jpg");
//		Imgproc.rectangle(mat, new Point(10, 10), new Point(100, 100), new Scalar(0, 255, 0));
//		Imgcodecs.imwrite("C:\\Users\\USER\\Downloads\\hehe.jpg", mat);
		new BorderImage().Border(mat);
	}
}
