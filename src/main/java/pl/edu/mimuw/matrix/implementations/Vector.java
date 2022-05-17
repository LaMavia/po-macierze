package pl.edu.mimuw.matrix.implementations;

import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

public class Vector extends BaseMatrix {
  private int[] index;
  private double[] value;

  private int countNNZ(double[] values) {
    int nnz = 0;

    for (double v : values) {
      if (v != 0) {
        nnz++;
      }
    }

    return nnz;
  }

  public Vector(double... values) {
    assert values != null;

    int nnz = this.countNNZ(values);
    this.value = new double[nnz + 1];
    this.index = new int[nnz + 1];

    int index = 0;
    for (int i = 0; i < values.length; i++) {
      if (values[i] != 0) {
        this.value[index] = values[i];
        this.index[index] = i;

        index++;
      }
    }

    this.value[nnz] = 0;
    this.index[nnz] = values.length;
  }

  private Vector(double[] value, int[] index) {
    this.value = value;
    this.index = index;
  }

  public int numberOfRows() {
    return this.index[this.index.length - 1];
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    assert other.shape().columns == this.index[this.index.length - 1];

    return new Zero(Shape.vector(other.shape().rows));
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    assert other != null;
    assert other.shape().columns == this.numberOfRows();

    double[] values = new double[this.numberOfRows()];

    for (int i = 0; i < this.numberOfRows(); i++) {
      int rowPtr = other.getRowPointer(i);
      int thisRowIndex = 0;
      values[i] = 0;

      for (int j = other.getRowStart(rowPtr); j < other.getRowEnd(rowPtr); j++) {
        int columnIndex = other.getColumn(j);

        // Move the vector pointer
        while (columnIndex > this.index[thisRowIndex]) {
          thisRowIndex++;
        }

        if (this.index[thisRowIndex] == columnIndex) {
          values[i] += other.getValue(j) * this.value[thisRowIndex];
        }
      }
    }

    return new Vector(values);
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    assert other != null;
    assert other.shape().columns == this.numberOfRows();

    Shape otherShape = other.shape();

    // optimise?
    /*
     * Problem:
     * Tak czy inaczej musimy zaalokować tyle elementów, ponieważ jest to
     * możliwy
     * wynik, niezależnie od nnz wektora; np.:
     * |7 5 0| |1| |12|
     * |0 3 11| × |1| = | 3|
     * |0 2 0| |1| | 2|
     */
    double[] newValue = new double[otherShape.rows];

    for (int r = 0; r < otherShape.rows; r++) {
      for (int i = 0; i < this.index.length - 1; i++) {
        newValue[r] += other.get(r, this.index[i]) * this.value[i];
      }
    }

    return new Vector(newValue);
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    assert other != null;
    assert other.shape().columns == this.numberOfRows();

    Shape otherShape = other.shape();
    double[] newValue = new double[otherShape.rows];

    for (int i = 0; i < this.index.length - 1; i++) {
      newValue[this.index[i]] = this.value[i] * other.get(this.index[i], this.index[i]);
    }

    return new Vector(newValue);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    assert other != null;
    assert other.shape().columns == this.numberOfRows();

    Shape otherShape = other.shape();
    double[] newValue = new double[otherShape.rows];

    for (int i = 0; i < this.index.length - 1; i++) {
      int otherIndex = otherShape.rows - this.index[i] - 1;
      newValue[this.index[i]] = this.value[i] * other.get(otherIndex, otherIndex);
    }

    return new Vector(newValue);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (scalar == 0) {
      return new Zero(Shape.vector(this.numberOfRows()));
    }

    double[] newValue = new double[this.value.length];

    for (int i = 0; i < newValue.length; i++) {
      newValue[i] = this.value[i] * scalar;
    }

    return new Vector(newValue, this.index);
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    assert other != null;
    assert other.shape().equals(this.shape());

    return new Vector(this.value[0] + 1);
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert this.shape().equals(other.shape());

    return this;
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    assert this.shape().equals(other.shape());

    double[] newData = new double[this.numberOfRows()];

    int iThis = 0;

    for (int i = 0; i < newData.length; i++) {
      double newValue = other.get(i, 0);

      if (this.index[iThis] == i) {
        newValue += this.value[iThis++];
      }

      newData[i] = newValue;
    }

    return new Vector(newData);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert this.shape().equals(other.shape());

    double[] newData = new double[this.numberOfRows()];
    double[][] oldData = other.data();

    int iThis = 0;

    for (int i = 0; i < oldData.length; i++) {
      double newValue = oldData[i][0];

      if (this.index[iThis] == i) {
        newValue += this.value[iThis++];
      }

      newData[i] = newValue;
    }

    return new Vector(newData);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    assert this.shape().equals(other.shape());

    double vThis = this.value[0];
    double vOther = other.get(0, 0);

    if (vThis == vOther) {
      return new Zero(Shape.matrix(1, 1));
    } else {
      return new Vector(vThis + vOther);
    }
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    assert this.shape().equals(other.shape());

    // => [_] + [_]
    double vThis = this.value[0];
    double vOther = other.get(0, 0);

    if (vThis == vOther) {
      return new Zero(Shape.matrix(1, 1));
    } else {
      return new Vector(vThis + vOther);
    }

  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (Math.abs(scalar) == 0)
      return this;

    double[][] thisData = this.data();
    double[] data = new double[this.numberOfRows()];

    for (int i = 0; i < data.length; i++) {
      data[i] = thisData[i][0] + scalar;
    }

    return new Vector(data);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.index[this.index.length - 1];
    assert column == 0;

    for (int i = 0; i < this.index[this.index.length - 1] && this.index[i] <= row; i++) {
      if (this.index[i] == row) {
        return this.value[i];
      }
    }

    return 0;
  }

  @Override
  public double[][] data() {
    // ∀i in I[value]. value[i] = v[sum{ offset[j] | 0 <= j <= i }]
    double[][] data = new double[this.index[this.index.length - 1]][1];

    for (int i = 0; i < this.value.length - 1; i++) {
      data[this.index[i]][0] = this.value[i];
    }

    return data;
  }

  @Override
  public double normOne() {
    double sum = 0;

    for (double v : this.value) {
      sum += Math.abs(v);
    }

    return sum;

  }

  @Override
  public double normInfinity() {
    double max = 0;

    for (double v : this.value) {
      max = Math.max(max, Math.abs(v));
    }

    return max;
  }

  @Override
  public double frobeniusNorm() {
    double sum = 0;

    for (double v : this.value) {
      sum += v * v;
    }

    return Math.sqrt(sum);
  }

  @Override
  public Shape shape() {
    return Shape.vector(this.index[this.index.length - 1]);
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    assert this.index[this.index.length - 1] == 1;

    return other.times(this.get(0, 0));
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    assert other != null;
    assert other.numberOfRows() == this.numberOfRows();

    double[] newValues = new double[this.value.length + other.value.length - 1];
    int[] newIndex = new int[newValues.length];

    int iThis = 0, iOther = 0, iNew = 0;

    while (!(this.index[iThis] == other.index[iOther] &&
        this.index[iThis] == this.numberOfRows())) {
      if (this.index[iThis] == other.index[iOther]) {
        double newValue = this.value[iThis] + other.value[iOther];

        if (newValue != 0) {
          newValues[iNew] = newValue;
          newIndex[iNew] = this.index[iThis];

          iNew++;
        }

        iThis++;
        iOther++;
      } else if (this.index[iThis] < other.index[iOther]) {
        newValues[iNew] = this.value[iThis];
        newIndex[iNew] = this.index[iThis];

        iNew++;
        iThis++;
      } else {
        newValues[iNew] = other.value[iOther];
        newIndex[iNew] = other.index[iOther];

        iNew++;
        iOther++;
      }
    }

    newValues = Arrays.copyOf(newValues, iNew + 1);
    newIndex = Arrays.copyOf(newIndex, iNew + 1);

    newValues[iNew] = 0;
    newIndex[iNew] = this.numberOfRows();

    return new Vector(newValues, newIndex);
  }

  @Override
  public String toString() {
    String out = super.toString();

    for (int r = 0; r < this.numberOfRows(); r++) {
      out += this.get(r, 0) + "\n";
    }

    return out;
  }
}
