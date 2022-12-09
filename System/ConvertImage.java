package System;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ConvertImage {
	public static BufferedImage Pretreatment(File file) throws IOException {
		String path = file.getPath();
		// Load thu vien
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// Doc hinh tu File chon
		Mat source = Imgcodecs.imread(path, Imgcodecs.IMREAD_GRAYSCALE); // chuyen anh ve dang anh xam

		// tang do phan giai anh
		Mat dst_Contrast = new Mat(source.rows(), source.cols(), source.type());
		Mat dst_smoothing = new Mat();
		source.convertTo(dst_Contrast, -1, 2, 0);
		// giam nhieu
		Size ksize = new Size(5, 5);
		Imgproc.GaussianBlur(source, dst_smoothing, ksize, 0);
		// nhi phan hoa hinh anh
		Mat dst_AdaptiveThresh = new Mat();
		Imgproc.adaptiveThreshold(dst_Contrast, dst_AdaptiveThresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
		Imgproc.THRESH_BINARY_INV, 11, 2);
		Imgcodecs.imwrite("D:\\ki5\\AI\\AI\\Giuaki\\Image\\test4.jpg", dst_AdaptiveThresh);
		//
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", dst_AdaptiveThresh, matOfByte);
		// Storing the encoded Mat in a byte array
		byte[] byteArray = matOfByte.toArray();
		// Preparing the Buffered Image
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage img = ImageIO.read(in);
		return img;
	}

	public static void main(String[] args) throws IOException {
		File file = new File("D:\\ki5\\AI\\\\AI\\Giuaki\\Image\\1.jpg");
		System.out.println(Pretreatment(file));
	}
}
