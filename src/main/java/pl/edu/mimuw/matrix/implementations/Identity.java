package pl.edu.mimuw.matrix.implementations;

import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

public class Identity implements IDoubleMatrix {
  private final int size;

  public Identity(int size) {
    assert size > 0;
    this.size = size;
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;
    assert this.size == other.shape().rows;

    return other;
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (Math.abs(scalar) == 0) {
      return new Zero(Shape.matrix(this.size, this.size));
    }

    if (scalar == 1) {
      return new Identity(size);
    }

    double[] values = new double[this.size];

    for (int i = 0; i < this.size; i++) {
      values[i] = scalar;
    }

    return new Diagonal(values);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;
    assert other.shape() == this.shape();

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (Math.abs(scalar) == 0) {
      return this;
    }

    double[][] values = new double[this.size][this.size];

    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        values[i][j] = scalar + this.get(i, j);
      }
    }

    return new Full(values);
  }

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    return this.plus(other.times(-1));
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    return this.plus(-scalar);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.size;
    assert 0 <= column && column < this.size;

    return row == column ? 1 : 0;
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.size][this.size];

    for (int i = 0; i < this.size; i++) {
      data[i][i] = 1;
    }

    return data;
  }

  @Override
  public double normOne() {
    return 1;
  }

  @Override
  public double normInfinity() {
    return 1;
  }

  @Override
  public double frobeniusNorm() {
    return Math.sqrt(this.size);
  }

  @Override
  public Shape shape() {
    return Shape.matrix(this.size, this.size);
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert other != null;
    assert other.shape() == this.shape();

    return this;
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    assert other != null;
    assert other.shape() == this.shape();

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[other.nnz + 1 + this.size];

    for (int r = 0; r < this.size; r++) {
      boolean visitedDiagonal = false;

      for (int i = other.getRowStart(r); i < other.getRowEnd(r); i++) {
        double value = other.getValue(i);

        if (other.getColumn(i) == r) {
          visitedDiagonal = true;
          value += this.get(r, r);
        }

        values[index++] = new MatrixCellValue(r, other.getColumn(i), value);
      }

      if (!visitedDiagonal) {
        values[index++] = new MatrixCellValue(r, r, this.get(r, r));
      }
    }

    values = Arrays.copyOf(values, index);

    return new CSR(this.shape(), values);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert other != null;
    assert other.shape() == this.shape();

    double[][] values = new double[this.size][this.size];

    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        values[i][j] = other.get(i, j) + this.get(i, j);
      }
    }

    return new Full(values);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    assert other != null;
    assert other.shape() == this.shape();

    double[] values = new double[this.size];
    int nz = 0;

    for (int i = 0; i < this.size; i++) {
      values[i] = other.get(i, i) + 1;
      nz += values[i] == 0 ? 1 : 0;
    }

    if (nz == this.size) {
      return new Zero(this.shape());
    }

    return new Diagonal(values);
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    assert other != null;
    assert other.shape() == this.shape();

    MatrixCellValue[] values = new MatrixCellValue[this.size * 2];

    int index = 0;

    for (int i = 0; i < this.size; i++) {
      int iOther = this.size - i - 1;

      if (i == iOther) {
        values[index++] = new MatrixCellValue(i, i, this.get(i, i) + other.get(iOther, iOther));
      } else {
        values[index++] = new MatrixCellValue(i, i, this.get(i, i));
        values[index++] = new MatrixCellValue(iOther, iOther, other.get(iOther, iOther));
      }
    }

    return new CSR(this.shape(), values);
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    assert other != null;
    assert other.shape() == this.shape(); // => size = 1

    return new Vector(1 + other.get(0, 0));
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    assert other != null;
    assert other.shape() == this.shape();

    double[] values = new double[this.size];

    for (int i = 0; i < this.size; i++) {
      values[i] = 2;
    }

    return new Diagonal(values);
  }

  private IDoubleMatrix timesLeftGeneric(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().columns == this.size;

    return other;
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
}
