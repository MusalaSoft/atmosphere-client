package com.musala.atmosphere.client;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.musala.atmosphere.bitmap.comparison.kmp.KMPMatrixComparator;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.util.Pair;

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
     * @return <code>true</code> if the given image is found in this image and <code>false</code> otherwise
     */
    public boolean containsImage(Image soughtImage) {
        int[][] bufferedImageMatrix = this.getRGBMatrix();
        int[][] soughtImageMatrix = soughtImage.getRGBMatrix();

        boolean containsMatrix = matrixComparator.containsMatrix(bufferedImageMatrix, soughtImageMatrix);
        return containsMatrix;
    }

    /**
     * Crops the element in the given (@link Bounds) and returns it as an image.
     *
     * @param elementBounds
     *        - bounds of the element
     * @param screenOrientaion
     *        - screen orientation of the device
     * @param resolution
     *        - resolution of the screen
     * @return <code>Image</code> of the element
     */
    public Image getSubimage(Bounds elementBounds, ScreenOrientation screenOrientaion, Pair<Integer, Integer> resolution) {

        Point upperLeftCorner = elementBounds.getUpperLeftCorner();
        Point upperRightCorner = elementBounds.getUpperRightCorner();
        Point lowerLeftCorner = elementBounds.getLowerLeftCorner();
        Point lowerRightCorner = elementBounds.getLowerRightCorner();

        int upperLeftCornerPointX;
        int upperLeftCornerPointY;
        int elementWidth;
        int elementHeight;

        int resolutionX = resolution.getKey();
        int resolutionY = resolution.getValue();

        BufferedImage croppedBufferedImage;
        Image croppedImage;
        switch (screenOrientaion) {
            case LANDSCAPE:
                upperLeftCornerPointX = lowerLeftCorner.getY();
                upperLeftCornerPointY = lowerLeftCorner.getX();
                elementWidth = elementBounds.getHeight();
                elementHeight = elementBounds.getWidth();

                croppedBufferedImage = this.image.getSubimage(resolutionY - upperLeftCornerPointX,
                                                              upperLeftCornerPointY,
                                                              elementWidth,
                                                              elementHeight);
                croppedImage = new Image(croppedBufferedImage);
                return croppedImage.rotate(270);
            case UPSIDE_DOWN_LANDSCAPE:
                upperLeftCornerPointX = upperRightCorner.getX();
                upperLeftCornerPointY = upperRightCorner.getY();
                elementWidth = elementBounds.getHeight();
                elementHeight = elementBounds.getWidth();

                croppedBufferedImage = this.image.getSubimage(upperLeftCornerPointY, resolutionX
                        - upperLeftCornerPointX, elementWidth, elementHeight);
                croppedImage = new Image(croppedBufferedImage);
                return croppedImage.rotate(90);
            case PORTRAIT:
                upperLeftCornerPointX = upperLeftCorner.getX();
                upperLeftCornerPointY = upperLeftCorner.getY();
                elementWidth = elementBounds.getWidth();
                elementHeight = elementBounds.getHeight();

                croppedBufferedImage = this.image.getSubimage(upperLeftCornerPointX,
                                                              upperLeftCornerPointY,
                                                              elementWidth,
                                                              elementHeight);
                croppedImage = new Image(croppedBufferedImage);
                return croppedImage;
            case UPSIDE_DOWN_PORTRAIT:
                upperLeftCornerPointX = lowerRightCorner.getX();
                upperLeftCornerPointY = lowerRightCorner.getY();
                elementWidth = elementBounds.getWidth();
                elementHeight = elementBounds.getHeight();

                croppedBufferedImage = this.image.getSubimage(resolutionX - upperLeftCornerPointX, resolutionY
                        - upperLeftCornerPointY, elementWidth, elementHeight);

                croppedImage = new Image(croppedBufferedImage);
                return croppedImage.rotate(180);
            default:
                return null;
        }
    }

    /**
     * Rotates the {@link Image} on 90, 180 and 270 degrees.
     *
     * @param degrees
     *        - degrees of the rotation
     * @return the rotated <code>Image</code>
     */
    private Image rotate(int degrees) {
        BufferedImage bufferedImage = this.getBufferedImage();
        BufferedImage transformedBufferedImage;

        double angle = Math.toRadians(degrees);
        AffineTransform affineTransform = new AffineTransform();

        if (degrees == 90 || degrees == 270) {

            affineTransform.translate(bufferedImage.getHeight() / 2, bufferedImage.getWidth() / 2);
            affineTransform.rotate(angle);
            affineTransform.translate(-bufferedImage.getWidth() / 2, -bufferedImage.getHeight() / 2);

            transformedBufferedImage = new BufferedImage(bufferedImage.getHeight(),
                                                         bufferedImage.getWidth(),
                                                         bufferedImage.getType());

            AffineTransformOp transformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);

            transformOp.filter(bufferedImage, transformedBufferedImage);
        } else {
            AffineTransform flipTransform = AffineTransform.getScaleInstance(-1, -1);
            flipTransform.translate(-bufferedImage.getWidth(), -bufferedImage.getHeight());

            transformedBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                                                         bufferedImage.getHeight(),
                                                         bufferedImage.getType());

            AffineTransformOp op = new AffineTransformOp(flipTransform, AffineTransformOp.TYPE_BILINEAR);
            op.filter(bufferedImage, transformedBufferedImage);
        }

        Image rotatedImage = new Image(transformedBufferedImage);

        return rotatedImage;
    }

    /**
     * Checks if this {@link Image} has the same RGB matrix and resolution as the passed one.
     *
     * @param object
     *        - the {@link Image} for comparison
     *
     * @return <code>true</code>, if this Image has the same properties as the passed <code>object</
    code> and <code>false</code> if
     *         the passed object is not an {@link Image} or differs from this {@link Image}
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }

        Image image = (Image) object;
        return (image.getHeight() == this.getHeight() && image.getWidth() == this.getWidth() && this.containsImage(image));
    }

    /**
     * Returns a hash code for this value.
     *
     * @return the hashcode of this object
     * @see HashCodeBuilder
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(image).append(format).toHashCode();
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
