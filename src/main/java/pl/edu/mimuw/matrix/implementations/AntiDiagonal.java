package pl.edu.mimuw.matrix.implementations;

import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

public class AntiDiagonal implements IDoubleMatrix {
  private double[] values;

  public AntiDiagonal(double... values) {
    this.values = values;
  }

  public int size() {
    return this.values.length;
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (Math.abs(scalar) == 0) {
      return new Zero(this.shape());
    }

    if (scalar == 1) {
      return this;
    }

    double[] values = this.values.clone();

    for (int i = 0; i < values.length; i++) {
      values[i] *= scalar;
    }

    return new AntiDiagonal(values);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    double[][] values = new double[this.size()][this.size()];

    for (int i = 0; i < this.size(); i++) {
      for (int j = 0; j < this.size(); j++) {
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
    assert 0 <= row & row < this.values.length;
    assert 0 <= column & column < this.values.length;

    if (column == this.size() - row - 1) {
      return this.values[row];
    } else {
      return 0;
    }
  }

  private int indexCompliment(int i) {
    return this.size() - i - 1;
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.size()][this.size()];

    for (int i = 0; i < this.size(); i++) {
      for (int j = 0; j < this.size(); j++) {
        data[i][j] = this.get(i, j);
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    double max = 0;

    for (double v : this.values) {
      max = Math.max(Math.abs(v), max);
    }

    return max;
  }

  @Override
  public double normInfinity() {
    return this.normOne();
  }

  @Override
  public double frobeniusNorm() {
    double sum = 0;

    for (double v : this.values) {
      sum += Math.abs(v);
    }

    return Math.sqrt(sum);
  }

  @Override
  public Shape shape() {
    return Shape.matrix(this.size(), this.size());
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    assert other != null;
    assert other.shape() == this.shape();

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[other.nnz + 1 + this.size()];

    for (int r = 0; r < this.values.length; r++) {
      boolean visitedDiagonal = false;

      for (int i = other.getRowStart(r); i < other.getRowEnd(r); i++) {
        double value = other.getValue(i);

        if (other.getColumn(i) == this.size() - r - 1) {
          visitedDiagonal = true;
          value += this.get(r, this.size() - r - 1);
        }

        values[index++] = new MatrixCellValue(r, other.getColumn(i), value);
      }

      if (!visitedDiagonal) {
        values[index++] = new MatrixCellValue(r, this.size() - r - 1, this.get(r, this.size() - r - 1));
      }
    }

    values = Arrays.copyOf(values, index);

    return new CSR(this.shape(), values);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    // TODO Auto-generated method stub
    return null;
  }

}
