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
     * Returns complex from the reals
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
        double mb2 = other.normSquare();
        double r = (real * other.real + im * other.im) / mb2;
        double i = (im * other.real - real * other.im) / mb2;
        return new Complex(r, i);
    }

    /**
     * Returns the ratio of complex numbers (this / other)
     *
     * @param other the other complex
     */
    public Complex div(double other) {
        return new Complex(real / other, im / other);
    }

    /**
     * Returns the inverse complex
     */
    public Complex inv() {
        double m2 = normSquare();
        return new Complex(real / m2, -im / m2);
    }

    /**
     * Returns true if complex is close to another complex with epsilon range
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
     * Returns the module
     */
    public double norm() {
        return Math.sqrt(normSquare());
    }

    /**
     * Returns the module square
     */
    public double normSquare() {
        return real * real + im * im;
    }

    /**
     * Returns the negation of complex
     */
    public Complex neg() {
        return new Complex(-real, -im);
    }

    /**
     * Returns the square root
     */
    public Complex sqrt() {
        if (abs(normSquare()) == 0) {
            return Complex.zero();
        } else if (real >= 0) {
            double reDelta = real + norm();
            double re = Math.sqrt(reDelta / 2);
            double im = this.im / Math.sqrt(2 * reDelta);
            return new Complex(re, im);
        } else {
            double reDelta = -real + norm();
            double re = im / Math.sqrt(2 * reDelta);
            double im = Math.sqrt(reDelta / 2);
            return new Complex(re, im);
        }
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
        if (im == 0) {
            return Double.toString(real);
        }
        if (real == 0) {
            if (im == 1) {
                return "i";
            }
            if (im == -1) {
                return "-i";
            }
            return im + " i";
        }
        if (im == 1) {
            return real + " +i";
        }
        if (im == -1) {
            return real + " -i";
        }
        return im > 0
                ? real + " +" + im + " i"
                : real + " " + im + " i";
    }
}