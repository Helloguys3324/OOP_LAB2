package lab2;

import lab1.*;
public class Display {
    private String model;
    private int width;
    private int height;
    private float ppi;
    private Logger logger;

    public Display(String model, int width, int height, float ppi, Logger logger) {
        this.model = model;
        this.width = width;
        this.height = height;
        this.ppi = ppi;
        this.logger = logger;
        logger.trace("Display % created (%\", % ppi)", model, round1(getDiagonal()), round1(ppi));
    }

    public String getModel() { return model; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getPpi() { return ppi; }

    public double getDiagonal() {
        return Math.sqrt((double) width * width + (double) height * height) / ppi;
    }

    private String round1(double val) {
        return String.valueOf(Math.round(val * 10.0) / 10.0);
    }

    public int compareSize(Display other) {
        double d1 = this.getDiagonal();
        double d2 = other.getDiagonal();

        if (Math.abs(d1 - d2) < 0.05) {
            logger.info("% (%\") and % (%\") have the same screen size", model, round1(d1), other.model, round1(d2));
            return 0;
        } else if (d1 > d2) {
            logger.info("% (%\") is bigger than % (%\")", model, round1(d1), other.model, round1(d2));
            return 1;
        } else {
            logger.info("% (%\") is bigger than % (%\")", other.model, round1(d2), model, round1(d1));
            return -1;
        }
    }

    public int compareSharpness(Display other) {
        if (Math.abs(this.ppi - other.ppi) < 0.05f) {
            logger.info("% (% ppi) and % (% ppi) have the same sharpness", model, round1(ppi), other.model, round1(other.ppi));
            return 0;
        } else if (this.ppi > other.ppi) {
            logger.info("% (% ppi) is sharper than % (% ppi)", model, round1(ppi), other.model, round1(other.ppi));
            return 1;
        } else {
            logger.info("% (% ppi) is sharper than % (% ppi)", other.model, round1(other.ppi), model, round1(ppi));
            return -1;
        }
    }

    public void compareWithMonitor(Display other) {
        if (this == other) {
            logger.warn("Comparing % with itself", model);
            return;
        }

        int sizeRes = compareSize(other);
        int sharpRes = compareSharpness(other);

        if (sizeRes > 0 && sharpRes > 0) {
            logger.info("Result: % wins (bigger and sharper than %)", model, other.model);
        } else if (sizeRes < 0 && sharpRes < 0) {
            logger.info("Result: % wins (bigger and sharper than %)", other.model, model);
        } else if (sizeRes == 0 && sharpRes == 0) {
            logger.info("Result: % and % are basically the same", model, other.model);
        } else if (sizeRes >= 0 && sharpRes >= 0) {
            logger.info("Result: % is a bit better than %", model, other.model);
        } else if (sizeRes <= 0 && sharpRes <= 0) {
            logger.info("Result: % is a bit better than %", other.model, model);
        } else {
            String bigger = sizeRes > 0 ? model : other.model;
            String sharper = sharpRes > 0 ? model : other.model;
            logger.info("Result: depends on what you want - % is bigger, % is sharper", bigger, sharper);
        }
    }
}
