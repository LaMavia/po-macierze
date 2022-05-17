package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

public class ColumnMatrix extends RowMatrix {
  @Override
  protected void assertInputValidity(Shape shape, double[] values) {
    assert shape != null;
    assert values != null;
    
    assert values.length == shape.rows;
  }

  public ColumnMatrix(Shape shape, double[] values) {
    super(shape, values);
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    double[][] data = new double[other.shape().rows][this.shape.columns];

    for (int r = 0; r < other.shape().rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] = other.get(r, 0) * this.get(0, c);
      }
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    return this.timesLeft((Diagonal) other);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.times(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (scalar == 0) {
      return new Zero(this.shape);
    }

    if (scalar == 1) {
      return this;
    }

    double[] newValues = this.values.clone();

    for (int i = 0; i < this.values.length; i++) {
      newValues[i] *= scalar;
    }

    return new ColumnMatrix(this.shape, newValues);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (scalar == 0) {
      return this;
    }

    double[] newValues = this.values.clone();

    for (int i = 0; i < this.values.length; i++) {
      newValues[i] += scalar;
    }

    return new ColumnMatrix(this.shape, newValues);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.shape.rows;
    assert 0 <= column && column < this.shape.columns;

    return this.values[row];
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int r = 0; r < this.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] = this.get(r, c);
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    double sum = 0;
    for (double v : this.values) {
      sum += Math.abs(v);
    }

    return sum;
  }

  @Override
  public double normInfinity() {
    return super.normOne() / this.shape.rows * this.shape.columns;
  }

  @Override
  public double frobeniusNorm() {
    return Math.sqrt(this.shape.columns * this.normOne());
  }

  @Override
  public String toString() {
    String out = "";

    for (int r = 0; r < this.shape.rows; r++) {
      if (this.shape.columns >= 3) {
        out += String.format("%.2f ... %.2f\n", this.get(r, 0), this.get(r, 0));
      } else if (this.shape.columns == 2) {
        out += String.format("%.2f %.2f\n", this.get(r, 0), this.get(r, 0));
      } else {
        out += String.format("%.2f\n", this.get(r, 0));
      }
    }

    return out;
  }
}
