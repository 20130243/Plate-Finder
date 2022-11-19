package System;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ConvertImage {
	public static void GreyImage(String path) throws IOException {
		File file = new File(path);
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
			  // lấy chiều cao và chiều rộng của ảnh
	        int width = img.getWidth();
	        int height = img.getHeight();

	        // chuyển đổi sang màu xám
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                // x,y là toạ độ của ảnh để sửa các giá trị pixel
	                int p = img.getRGB(x, y);

	                int a = (p >> 24) & 0xff;
	                int r = (p >> 16) & 0xff;
	                int g = (p >> 8) & 0xff;
	                int b = p & 0xff;

	                // tính giá trị trung bình
	                int avg = (r + g + b) / 3;

	                // thay RGB bằng giá trị avg vừa tính được
	                p = (a << 24) | (avg << 16) | (avg << 8) | avg;

	                img.setRGB(x, y, p);
	            }
	        }
			
		} catch (Exception ex) {
			// TODO: handle exception
		}
		}
	
		
	}
