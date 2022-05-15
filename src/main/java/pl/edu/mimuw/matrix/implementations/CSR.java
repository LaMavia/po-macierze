package pl.edu.mimuw.matrix.implementations;

import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

public class CSR implements IDoubleMatrix {
  public final int nnz;
  private final double[] value;
  private final int[] column;
  private final int[] row;
  private final Shape shape;

  private int countNNZ(MatrixCellValue[] values) {
    int nnz = 0;

    for (MatrixCellValue v : values) {
      if (v.value != 0)
        nnz++;
    }

    return nnz;
  }

  private void assertInputValidity(Shape shape, MatrixCellValue... values) {
    for (MatrixCellValue v : values) {
      assert (0 <= v.row && v.row < shape.rows) && (0 <= v.column && v.column < shape.columns);
    }
  }

  private CSR(
      double[] value,
      int[] column,
      int[] row,
      Shape shape) {
    this.nnz = value.length;

    this.value = value;
    this.column = column;
    this.row = row;
    this.shape = shape;
  }

  public CSR(Shape shape, MatrixCellValue... values) {
    assertInputValidity(shape, values);

    this.shape = shape;
    this.nnz = countNNZ(values); // O(n)

    this.value = new double[this.nnz];
    this.column = new int[this.nnz];
    this.row = new int[shape.rows + 1];

    Arrays.fill(this.row, this.nnz);

    int valueId = 0;
    int previousRow = -1;

    Arrays.sort(values); // O(n log(n))

    for (MatrixCellValue v : values) {
      if (v.value == 0)
        continue;

      if (v.row > previousRow) {
        previousRow = v.row;
        this.row[v.row] = valueId;
      }

      this.column[valueId] = v.column;
      this.value[valueId] = v.value;

      valueId++;
    }
  }

  public int getRowStart(int i) {
    assert 0 <= i && i < this.row.length - 1;

    return this.row[i];
  }

  public int getRowEnd(int i) {
    assert 0 <= i && i < this.row.length - 1;

    return this.row[i + 1];
  }

  public int getColumn(int i) {
    assert 0 <= i && i < this.column.length;

    return this.column[i];
  }

  public double getValue(int i) {
    assert 0 <= i && i < this.value.length;

    return this.value[i];
  }

  @Override
  public String toString() {
    return String.format("row:\n%s\ncolumn:\n%s\nvalue:\n%s\n",
        Arrays.toString(this.row),
        Arrays.toString(this.column),
        Arrays.toString(this.value));
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (scalar == 0.0) {
      return new Zero(this.shape);
    }
    if (scalar == 1.0) {
      return this;
    }

    double[] valueNew = new double[this.value.length];

    for (int i = 0; i < valueNew.length; i++) {
      valueNew[i] *= scalar;
    }

    return new CSR(valueNew, column, row, shape);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (Math.abs(scalar) == 0.0) {
      return this;
    }

    /*
     * Skoro większość komórek była zerowa, to po dodaniu niezerowej
     * większość będzie niezerowa.
     */

    return new Full(this.data()).plus(scalar);
  }

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    return other.plus(this.times(-1.0));
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    if (Math.abs(scalar) == 0.0) {
      return this;
    }

    /*
     * Skoro większość komórek była zerowa, to po dodaniu niezerowej
     * większość będzie niezerowa.
     */

    return new Full(this.data()).minus(scalar);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.shape.rows;
    assert 0 <= column && column < this.shape.columns;

    for (int i = this.row[row]; i < this.row[row + 1]; i++) {
      if (this.column[i] == column) {
        return this.value[i];
      }
    }

    return 0;
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int i = 0; i < this.shape.rows; i++) {
      int rowPtr = this.row[i];
      int rowPtrEnd = this.row[i + 1];

      for (int j = 0; j < this.shape.columns; j++) {
        if (rowPtr < rowPtrEnd && this.column[rowPtr] == j) {
          data[i][j] = this.value[rowPtr++];
        } else {
          data[i][j] = 0.0;
        }
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    // Calculate column sums
    double[] columnSums = new double[this.shape.columns];

    for (int i = 0; i < this.shape.rows; i++) {
      int rowPtr = this.row[i];
      int rowPtrEnd = this.row[i + 1];

      while (rowPtr < rowPtrEnd) {
        columnSums[this.column[rowPtr]] += Math.abs(this.value[rowPtr]);

        rowPtr++;
      }
    }

    // Pick the max
    double maxColumnSum = -1;

    for (int i = 0; i < columnSums.length; i++) {
      maxColumnSum = Math.max(maxColumnSum, columnSums[i]);
    }

    return maxColumnSum;
  }

  @Override
  public double normInfinity() {
    double maxRowSum = -1;

    for (int i = 0; i < this.shape.rows; i++) {
      int rowPtr = this.row[i];
      int rowPtrEnd = this.row[i + 1];

      double rowSum = 0;

      while (rowPtr < rowPtrEnd) {
        rowSum += Math.abs(this.value[rowPtr++]);
      }

      maxRowSum = Math.max(maxRowSum, rowSum);
    }

    return maxRowSum;
  }

  @Override
  public double frobeniusNorm() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Shape shape() {
    return this.shape;
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert other != null;
    
    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    // TODO Auto-generated method stub
    return null;
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
