package pl.edu.mimuw.matrix.implementations;

import java.util.Arrays;
import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

public class Diagonal extends BaseMatrix {

  protected double[] values;

  public Diagonal(double... values) {
    assert values != null;

    this.values = values;
  }

  protected int size() {
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

    double[] newValues = this.values.clone();

    for (int i = 0; i < this.values.length; i++) {
      newValues[i] *= scalar;
    }

    return new Diagonal(newValues);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().equals(this.shape());

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
  public double get(int row, int column) {
    assert 0 <= row & row < this.values.length;
    assert 0 <= column & column < this.values.length;

    if (column == this.indexCompliment(row)) {
      return this.values[row];
    } else {
      return 0;
    }
  }

  protected int indexCompliment(int i) {
    return i;
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.values.length][this.values.length];

    for (int i = 0; i < this.values.length; i++) {
      for (int j = 0; j < this.values.length; j++) {
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
      sum += v * v;
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
    assert other.shape().equals(this.shape());

    return this;
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    assert other != null;
    assert other.shape().equals(this.shape());

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[other.nnz + 1 + this.size()];

    for (int r = 0; r < this.values.length; r++) {
      boolean visitedDiagonal = false;
      int ri = other.getRowPointer(r);

      for (int i = other.getRowStart(ri); i < other.getRowEnd(ri); i++) {
        double value = other.getValue(i);

        if (other.getColumn(i) == this.indexCompliment(r)) {
          visitedDiagonal = true;
          value += this.get(r, this.indexCompliment(r));
        }

        values[index++] = new MatrixCellValue(r, other.getColumn(i), value);
      }

      if (!visitedDiagonal) {
        values[index++] =
          new MatrixCellValue(
            r,
            this.indexCompliment(r),
            this.get(r, this.indexCompliment(r))
          );
      }
    }

    values = Arrays.copyOf(values, index);

    return new CSR(this.shape(), values);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert other != null;
    assert other.shape().equals(this.shape());

    double[][] values = new double[this.size()][this.size()];

    for (int i = 0; i < this.size(); i++) {
      for (int j = 0; j < this.size(); j++) {
        values[i][j] = other.get(i, j) + this.get(i, j);
      }
    }

    return new Full(values);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    assert other != null;
    assert other.shape().equals(this.shape());

    double[] values = new double[this.size()];

    for (int i = 0; i < this.size(); i++) {
      values[i] = this.get(i, i) + other.get(i, i);
    }

    return new Diagonal(values);
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    assert other != null;
    assert other.shape().equals(this.shape());

    MatrixCellValue[] values = new MatrixCellValue[this.size() * 2];
    int index = 0;

    for (int i = 0; i < this.size(); i++) {
      int iOther = this.size() - i - 1;

      if (i == iOther) {
        values[index++] =
          new MatrixCellValue(i, i, this.get(i, i) + other.get(iOther, iOther));
      } else {
        values[index++] = new MatrixCellValue(i, i, this.get(i, i));
        values[index++] =
          new MatrixCellValue(iOther, iOther, other.get(iOther, iOther));
      }
    }

    return new CSR(this.shape(), values);
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    assert other != null;

    return other.times(this);
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    assert other != null;
    assert other.shape().columns == this.size();

    MatrixCellValue[] data = new MatrixCellValue[other.nnz];

    int index = 0;
    for (int r = 0; r < this.size(); r++) {
      int ri = other.getRowPointer(r);

      for (int c = 0; c < this.size(); c++) {
        double sum = 0;

        // @todo optimise
        for (
          int rowPtr = other.getRowStart(ri);
          rowPtr < other.getRowEnd(ri);
          rowPtr++
        ) {
          sum += this.get(other.getColumn(rowPtr), c) * other.getValue(rowPtr);
        }

        if (Math.abs(sum) != 0) {
          data[index++] = new MatrixCellValue(r, c, sum);
        }
      }
    }

    return new CSR(Shape.matrix(other.shape().rows, this.size()), data);
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    assert other != null;
    assert other.shape().columns == this.size();

    // @todo optimise to csr?
    Shape otherShape = other.shape();
    double[][] data = new double[otherShape.rows][this.size()];

    for (int c = 0; c < this.size(); c++) {
      for (int r = 0; r < otherShape.rows; r++) {
        data[r][c] += this.get(c, this.indexCompliment(c)) * other.get(r, c);
      }
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    assert other != null;
    assert other.size() == this.size();

    int oneCount = 0;
    int zeroCount = 0;

    double[] values = new double[this.size()];

    for (int i = 0; i < this.size(); i++) {
      values[i] = this.get(i, i) * other.get(i, i);

      if (values[i] == 1) {
        oneCount++;
      } else if (values[i] == 0) {
        zeroCount++;
      }
    }

    if (oneCount == this.size()) {
      return new Identity(this.size());
    }
    if (zeroCount == this.size()) {
      return new Zero(Shape.matrix(this.size(), this.size()));
    }

    return new Diagonal(values);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    assert other != null;
    assert other.size() == this.size();

    double[] values = new double[this.size()];

    for (int i = 0; i < this.size(); i++) {
      int iOther = this.size() - i - 1;

      values[iOther] = this.get(i, i) * other.get(iOther, i);
    }

    return new AntiDiagonal(values);
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    assert other != null;
    assert this.size() == 1;

    double value = other.get(0, 0) * this.get(0, 0);

    if (value == 0) {
      return new Zero(Shape.vector(1));
    }
    if (value == 1) {
      return new Identity(1);
    }

    return new Vector(value);
  }

  private String padding(int distance) {
    switch (distance) {
      case 0:
        return "";
      case 1:
        return "0";
      case 2:
        return "0 0";
      default:
        return "0 ... 0";
    }
  }

  protected int leftDistance(int r) {
    return r;
  }

  protected int rightDistance(int r) {
    return this.size() - r - 1;
  }

  @Override
  public String toString() {
    String out = super.toString();

    for (int r = 0; r < this.size(); r++) {
      String padLeft = padding(this.leftDistance(r));
      String padRight = padding(this.rightDistance(r));

      out +=
        String.format(
          "%s%.2f%s\n",
          padLeft.equals("") ? padLeft : (padLeft + " "),
          this.get(r, this.indexCompliment(r)),
          padRight.equals("") ? padRight : (" " + padRight)
        );
    }

    return out;
  }
}
