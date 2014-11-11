package com.musala.atmosphere.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.musala.atmosphere.bitmap.comparison.kmp.KMPMatrixComparator;

/**
 * A buffer image wrapper with comparison and conversion functionalities.
 * 
 * @author denis.bialev
 * 
 */
public class Image {

    private BufferedImage image;

    private final String format;

    public static final String DEFAULT_FORMAT = "png";

    private static final KMPMatrixComparator matrixComparator = new KMPMatrixComparator();

    /**
     * Construct image object with the given image.
     * 
     * @param image
     *        - that will be used in the class
     */
    public Image(BufferedImage image) {
        this.image = image;
        this.format = DEFAULT_FORMAT;
    }

    /**
     * Construct image object with the given image.
     * 
     * @param image
     *        - that will be used in the class
     * @param format
     *        - of the image
     */
    public Image(BufferedImage image, String format) {
        this.image = image;
        this.format = format;
    }

    public BufferedImage getBufferedImage() {
        return this.image;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.image = bufferedImage;
    }

    public int getHeight() {
        return this.image.getHeight();
    }

    public int getWidth() {
        return this.image.getWidth();
    }

    public int getRGB(int x, int y) {
        return this.image.getRGB(x, y);
    }

    /**
     * Saves the image on the given path.
     * 
     * @param pathToImageFile
     *        - path, including name, where the image will be saved
     * @return <code>true</code> if the image is saved successfully and <code>false</code> otherwise
     */
    public boolean save(String pathToImageFile) {
        try {
            File imageFile = new File(pathToImageFile);
            ImageIO.write(image, format, imageFile);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Loads an image from a given path.
     * 
     * @param imagePath
     *        - path where the image should be present
     * @return buffered image of the file found in the given path
     * @throws IOException
     *         - if the given path is invalid or the file is not an image
     */
    public Image load(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        return new Image(bufferedImage);
    }

    /**
     * Checks if the image contains in it the given image.
     * 
     * @param soughtImage
     *        - to check if it is present in the image
     * @return <code>true</code> if the given image is found in the image variable and <code>false</code> otherwise
     */
    public boolean containsImage(Image soughtImage) {
        int[][] bufferedImageMatrix = this.getRGBMatrix();
        int[][] soughtImageMatrix = soughtImage.getRGBMatrix();

        boolean containsMatrix = matrixComparator.containsMatrix(bufferedImageMatrix, soughtImageMatrix);
        return containsMatrix;
    }

    /**
     * Used to get the RGB matrix of the given image with positive values.
     * 
     * @return matrix with the RGB values of the given image
     */
    private int[][] getRGBMatrix() {
        int rows = image.getHeight();
        int columns = image.getWidth();
        int[][] imageMatrix = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Integer.MIN_Value because getRGB returns negatives values and our algorithm prefers positive.
                imageMatrix[i][j] = Integer.MIN_VALUE + image.getRGB(j, i);
            }
        }

        return imageMatrix;
    }
}
