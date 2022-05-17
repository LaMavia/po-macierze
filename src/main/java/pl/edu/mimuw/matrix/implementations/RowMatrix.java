package pl.edu.mimuw.matrix.implementations;

import java.util.ArrayList;
import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

public class RowMatrix extends BaseMatrix {

  protected double[] values;
  protected Shape shape;

  protected void assertInputValidity(Shape shape, double[] values) {
    assert shape != null;
    assert values != null;

    assert values.length == shape.columns;
  }

  public RowMatrix(Shape shape, double[] values) {
    this.assertInputValidity(shape, values);
    this.shape = shape;
    this.values = values;
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    assert other != null;
    assert other.shape.columns == this.shape.rows;

    return new Zero(Shape.matrix(other.shape.rows, this.shape.columns));
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    ArrayList<MatrixCellValue> values = new ArrayList<>();
    MatrixCellValue[] data = new MatrixCellValue[0];

    for (int ri = 0; ri < other.ner; ri++) {
      for (int c = 0; c < this.shape.columns; c++) {
        double value = 0;

        for (
          int ptr = other.getRowStart(ri);
          ptr < other.getRowEnd(ri);
          ptr++
        ) {
          value += other.getValue(ptr) * this.get(other.getColumn(ptr), c);
        }

        if (value != 0) {
          values.add(new MatrixCellValue(other.getRowNumber(ri), c, value));
        }
      }
    }

    Shape newShape = Shape.matrix(other.shape().rows, this.shape.columns);

    if (values.size() == 0) {
      return new Zero(newShape);
    }

    return new CSR(newShape, values.toArray(data));
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    double[][] data = new double[other.shape().rows][this.shape().columns];

    for (int r = 0; r < other.shape().rows; r++) {
      double scalar = other.get(r, other.indexCompliment(r));

      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] = scalar * this.get(other.indexCompliment(r), c);
      }
    }

    return new Full(data);
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

    return super.times(other);
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

    return new RowMatrix(this.shape, newValues);
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = this.data();

    for (int i = 0; i < this.shape.rows; i++) {
      data[i][i] += 1;
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    return other;
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = this.data();

    for (int ri = 0; ri < other.ner; ri++) {
      for (int ptr = other.getRowStart(ri); ptr < other.getRowEnd(ri); ptr++) {
        data[other.getRowNumber(ri)][other.getColumn(ptr)] +=
          other.getValue(ptr);
      }
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = this.data();

    for (int r = 0; r < this.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] += other.get(r, c);
      }
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = this.data();

    for (int i = 0; i < this.shape.rows; i++) {
      data[i][other.indexCompliment(i)] +=
        other.get(i, other.indexCompliment(i));
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    return new Full(
      new double[][] { new double[] { this.get(0, 0) + other.get(0, 0) } }
    );
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    return this.plusLeft((Diagonal) other);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = this.data();

    for (int r = 0; r < this.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] += other.get(r, c);
      }
    }

    return new Full(data);
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

    return new RowMatrix(this.shape, newValues);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.shape.rows;
    assert 0 <= column && column < this.shape.columns;

    return this.values[column];
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int r = 0; r < this.shape.rows; r++) {
      data[r] = this.values.clone();
    }

    return data;
  }

  @Override
  public double normOne() {
    double max = 0;

    for (double v : this.values) {
      max = Math.max(max, Math.abs(v));
    }

    return max * this.shape.rows;
  }

  @Override
  public double normInfinity() {
    double sum = 0;
    for (double v : this.values) {
      sum += Math.abs(v);
    }

    return sum;
  }

  @Override
  public double frobeniusNorm() {
    return Math.sqrt(this.shape.rows * this.normInfinity());
  }

  @Override
  public Shape shape() {
    return this.shape;
  }

  @Override
  public String toString() {
    if (this.values.length == 0) {
      return "\n";
    }

    String out = super.toString();
    String row = "";

    for (int i = 0; i < this.values.length - 1; i++) {
      row += this.values[i] + " ";
    }

    row += this.values[this.values.length - 1] + "\n";

    for (int i = 0; i < this.shape.rows; i++) {
      out += row;
    }

    return out;
  }
}
