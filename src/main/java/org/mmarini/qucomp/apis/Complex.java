package org.mmarini.qucomp.apis;

import static java.lang.Math.abs;

/**
 * Representation of complex number (real + i im)
 *
 * @param real the x coordinate
 * @param im   the y coordinate
 */
public record Complex(double real, double im) {
    public static Complex ZERO = new Complex(0, 0);
    public static Complex ONE = new Complex(1, 0);
    public static Complex I = new Complex(0, 1);

    /**
     * Returns complex from real couple
     *
     * @param real the real part
     */
    public static Complex create(double real) {
        return new Complex(real, 0);
    }

    /**
     * Returns i complex (0 + i)
     */
    public static Complex i() {
        return I;
    }

    /**
     * Returns i complex (0 + im i)
     */
    public static Complex i(double im) {
        return new Complex(0, im);
    }

    /**
     * Returns 1 complex (1 + 0 i)
     */
    public static Complex one() {
        return ONE;
    }

    /**
     * Returns 0 complex (0 + 0 i)
     */
    public static Complex zero() {
        return ZERO;
    }

    /**
     * Returns the sum of complex numbers (this + other)
     *
     * @param other the other complex
     */
    public Complex add(Complex other) {
        return new Complex(real + other.real, im + other.im);
    }

    /**
     * Returns the complex added by scalar
     *
     * @param alpha the scala
     */
    public Complex add(double alpha) {
        return new Complex(real + alpha, im);
    }

    /**
     * Returns the conjugated
     */
    public Complex conj() {
        return new Complex(real, -im);
    }

    /**
     * Returns the ratio of complex numbers (this / other)
     *
     * @param other the other complex
     */
    public Complex div(Complex other) {
        double mb2 = other.moduleSquare();
        double r = (real * other.real + im * other.im) / mb2;
        double i = (im * other.real - real * other.im) / mb2;
        return new Complex(r, i);
    }

    /**
     * Returns the inverse comples
     */
    public Complex inv() {
        double m2 = moduleSquare();
        return new Complex(real / m2, -im / m2);
    }

    /**
     * Returns true if complex is close to other complex with epsilon range
     *
     * @param other   the other complex
     * @param epsilon the range
     */
    public boolean isClose(Complex other, double epsilon) {
        double dr = abs(real - other.real);
        double di = abs(im - other.im);
        return dr <= epsilon && di <= epsilon;
    }

    /**
     * Returns the module
     */
    public double module() {
        return Math.sqrt(moduleSquare());
    }

    /**
     * Returns the module square
     */
    public double moduleSquare() {
        return real * real + im * im;
    }

    /**
     * Returns the product of complexes (this * other)
     *
     * @param other the other product
     */
    public Complex mul(Complex other) {
        double r = real * other.real - im * other.im;
        double i = real * other.im + im * other.real;
        return new Complex(r, i);
    }

    /**
     * Returns the complex multiply by scalar
     *
     * @param alpha the scala
     */
    public Complex mul(double alpha) {
        return new Complex(real * alpha, im * alpha);
    }

    /**
     * Returns the negation of complex
     */
    public Complex neg() {
        return new Complex(-real, -im);
    }

    /**
     * Returns the complex subtracted by scalar
     *
     * @param alpha the scala
     */
    public Complex sub(double alpha) {
        return new Complex(real - alpha, im);
    }

    /**
     * Returns the difference of complex numbers (this - other)
     *
     * @param other the other complex
     */
    public Complex sub(Complex other) {
        return new Complex(real - other.real, im - other.im);
    }

    @Override
    public String toString() {
        return im < 0
                ? "(" + real + " " + im + " i)"
                : "(" + real + " +" + im + " i)";
    }
}
