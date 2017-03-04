package captchaCracker;
/**
 * This class extracts the background and text color(RBG) for a captcha
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class ColorExtractor {
	private BufferedImage image; 
	private int width, height;
	private	PixelDictionary dict;
	int[] temp;
	String file, f_out;
	public ColorExtractor(String file){
		try{
			this.file = file;
			File input = new File(file);
			image = ImageIO.read(input);
			width = image.getWidth();
			height = image.getHeight();
			dict = new PixelDictionary();
			for(int i=0;i<height;i++){
				for(int j=0;j<width;j++){
					Pixel p = new Pixel(new Color(image.getRGB(j, i)));
					dict.putData(p);
				}
			}
			//dict.print();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String removeNoiseByColor(){
		/**
		 * Brief of the method and some assumptions:
		 * Black is in the range 0 to 51
		 * White is in the range 235 to 255
		 * Rest is noise which will be converted into white
		 */
		try{
			for(int i=0;i<height;i++){
				for(int j=0;j<width;j++){
					Pixel p = new Pixel(new Color(image.getRGB(j, i)));
					if(p.getValue()>-1&&p.getValue()<151){ //black
						p.setValue(0);
					}else{ //noise and background
						p.setValue(255);
					}
					image.setRGB(j, i, p.getColor().getRGB());
				}
			}
			f_out = file.substring(0, file.indexOf(".")) + "_filtered.jpg";
			File output = new File(f_out);
			ImageIO.write(image, "jpg", output);
		}catch(Exception e){
			e.printStackTrace();
		}
		return f_out;
	}
	public String removeNoiseByWidth(){
		/**
		 * Remove lines and noise from the background
		 * We assume a minimum boundary for text color scheme
		 * If any, lines are assumed to be one pixel wide
		 * If number of adjacent black pixels is less than or equal to two, it is removed
		 */
		try{
			for(int i=1;i<height-1;i++){
				for(int j=1;j<width-1;j++){
					Pixel p = new Pixel(new Color(image.getRGB(j, i)));
					if(getAdjacentPixels(i, j)<=2||!checkVertical(i, j)){
						p.setValue(255);
					}					
					image.setRGB(j, i, p.getColor().getRGB());
				}
			}
			f_out = file.substring(0, file.indexOf(".")) + "_filtered_width.jpg";
			File output = new File(f_out);
			ImageIO.write(image, "jpg", output);
		}catch(Exception e){
			e.printStackTrace();
		}
		return f_out;
	}
	/**
	 * Counts the number of adjacent black cells
	 * @param i
	 * @param j
	 * @return count
	 */
	public int getAdjacentPixels(int i, int j){
		Pixel p[] = new Pixel[8];
		int count = 0;
		p[0] = new Pixel(new Color(image.getRGB(j-1, i)));
		p[1] = new Pixel(new Color(image.getRGB(j+1, i)));
		p[2] = new Pixel(new Color(image.getRGB(j, i-1)));
		p[3] = new Pixel(new Color(image.getRGB(j, i+1)));
		p[4] = new Pixel(new Color(image.getRGB(j+1, i+1)));
		p[5] = new Pixel(new Color(image.getRGB(j-1, i-1)));
		p[6] = new Pixel(new Color(image.getRGB(j+1, i-1)));
		p[7] = new Pixel(new Color(image.getRGB(j-1, i+1)));
		for(int k=0;k<8;k++){
			if(p[k].getValue()==0)
				count++;
		}
		//System.out.println(count);
		return count;
	}
	/**
	 * Checks if it has a black pixel above and a black pixel below
	 * @param i
	 * @param j
	 * @return bool
	 */
	public boolean checkVertical(int i, int j){
		Pixel upper = new Pixel(new Color(image.getRGB(j, i+1)));
		Pixel lower = new Pixel(new Color(image.getRGB(j, i-1)));
		if(upper.getValue()==0||lower.getValue()==0)
			return true;
		return false;
	}
}