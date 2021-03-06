package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

public class Zero extends BaseMatrix {

  final Shape shape;

  public Zero(Shape shape) {
    assert shape != null;
    assert shape.rows > 0;
    assert shape.columns > 0;

    this.shape = shape;
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;
    assert this.shape.columns == other.shape().rows;

    return new Zero(Shape.matrix(this.shape.rows, other.shape().columns));
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    return this;
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert this.shape.equals(other.shape()) : String.format(
      "%s != %s",
      this.shape,
      other.shape()
    );

    return other;
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (Math.abs(scalar) == 0) {
      return this;
    }

    double[][] values = new double[this.shape.rows][this.shape.columns];
    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        values[i][j] = scalar;
      }
    }

    return new Full(values);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.shape.rows;
    assert 0 <= column && column < this.shape.columns;

    return 0;
  }

  @Override
  public double[][] data() {
    double[][] data = new double[shape.rows][shape.columns];

    for (int i = 0; i < shape.rows; i++) {
      for (int j = 0; j < shape.columns; j++) {
        data[i][j] = 0;
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    return 0;
  }

  @Override
  public double normInfinity() {
    return 0;
  }

  @Override
  public double frobeniusNorm() {
    return 0;
  }

  @Override
  public Shape shape() {
    return this.shape;
  }

  private IDoubleMatrix plusLeftGeneric(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    return other;
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    return plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    return plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    return plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    return plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    return plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    return plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    return plusLeftGeneric(other);
  }

  private IDoubleMatrix timesLeftGeneric(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    return new Zero(Shape.matrix(other.shape().rows, this.shape.columns));
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    return timesLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    return timesLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    return timesLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    return timesLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    return timesLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    return timesLeftGeneric(other);
  }

  @Override
  public String toString() {
    String out = super.toString();
    String row = "";

    switch (this.shape.columns) {
      case 1:
        row = "0\n";
        break;
      case 2:
        row = "0 0\n";
        break;
      default:
        row = "0 ... 0\n";
        break;
    }

    for (int i = 0; i < this.shape.rows; i++) {
      out += row;
    }

    return out;
  }
}
