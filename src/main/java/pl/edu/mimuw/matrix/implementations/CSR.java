package pl.edu.mimuw.matrix.implementations;

import java.util.ArrayList;
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
    assert values != null;
    
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

    int valueId = 0;
    int previousRow = -1;

    Arrays.fill(this.row, this.nnz);

    Arrays.sort(values); // O(n log(n))

    for (MatrixCellValue v : values) {
      if (v.value == 0)
        continue;

      if (v.row > previousRow) {
        for (int r = previousRow + 1; r <= v.row; r++) {
          this.row[r] = valueId;
        }

        previousRow = v.row;
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

    double[] valueNew = this.value.clone();

    for (int i = 0; i < valueNew.length; i++) {
      valueNew[i] *= scalar;
    }

    return new CSR(valueNew, column, row, shape);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;

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
    return this.plus(other.times(-1.0));
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
    double sum = 0;

    for (double v : this.value) {
      sum += v*v;
    }

    return Math.sqrt(sum);
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
    assert other != null;
    assert other.shape.equals(this.shape);

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[this.nnz + other.nnz];

    for (int r = 0; r < this.shape.rows; r++) {
      int thisPtr = this.getRowStart(r);
      int otherPtr = other.getRowStart(r);

      while (thisPtr < this.getRowEnd(r) && otherPtr < other.getRowEnd(r)) {
        int thisC = this.getColumn(thisPtr);
        int otherC = other.getColumn(otherPtr);

        if (thisC == otherC) {
          values[index++] = new MatrixCellValue(r, thisC, this.getValue(thisPtr++) + other.getValue(otherPtr++));
        } else if (thisC < otherC) {
          values[index++] = new MatrixCellValue(r, thisC, this.getValue(thisPtr++));
        } else {
          values[index++] = new MatrixCellValue(r, otherC, other.getValue(otherPtr++));
        }
      }

      // Push unmatched
      while (thisPtr < this.getRowEnd(r)) {
        values[index++] = new MatrixCellValue(r, this.getColumn(thisPtr), this.getValue(thisPtr));
        thisPtr++;
      }

      while (otherPtr < other.getRowEnd(r)) {
        values[index++] = new MatrixCellValue(r, other.getColumn(otherPtr), other.getValue(otherPtr));
        otherPtr++;
      }
    }

    values = Arrays.copyOf(values, index);

    return new CSR(this.shape, values);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = new double[this.shape.rows][this.shape.columns];
    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        data[i][j] = other.get(i, j);
      }

      for (int ptr = this.getRowStart(i); ptr < this.getRowEnd(i); ptr++) {
        data[i][this.getColumn(ptr)] += this.getValue(ptr);
      }
    }

    return new Full(data);
  }

  private IDoubleMatrix plusLeftGeneric(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    return this.plusLeftGeneric(other);
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

    int nnz = 0;

    ArrayList<MatrixCellValue> values = new ArrayList<>();
    MatrixCellValue[] data = new MatrixCellValue[0];

    for (int r = 0; r < other.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        double sum = 0;

        for (int ptr = other.getRowStart(r); ptr < other.getRowEnd(r); ptr++) {
          sum += other.getValue(ptr) * this.get(other.getColumn(ptr), c);
        }

        if (sum != 0) {
          nnz++;
        }

        values.add(new MatrixCellValue(r, c, sum));
      }
    }

    Shape newShape = Shape.matrix(other.shape.rows, this.shape.columns);

    if (nnz == 0) {
      return new Zero(newShape);
    }

    return new CSR(newShape, values.toArray(data));
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    ArrayList<MatrixCellValue> values = new ArrayList<>();
    MatrixCellValue[] data = new MatrixCellValue[0];

    for (int r = 0; r < other.shape().rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        double sum = 0;

        for (int k = 0; k < other.shape().columns; k++) {
          sum += other.get(r, k) * this.get(k, c);
        }

        if (sum != 0) {
          values.add(new MatrixCellValue(r, c, sum));
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

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[this.nnz];

    for (int r = 0; r < other.shape().rows; r++) {
      double scalar = other.get(r, other.indexCompliment(r));

      if (scalar == 0) {
        continue;
      }

      for (int ptr = this.getRowStart(other.indexCompliment(r)); ptr < this
          .getRowEnd(other.indexCompliment(r)); ptr++) {
        double value = scalar * this.getValue(ptr);

        if (value != 0) {
          values[index++] = new MatrixCellValue(r, this.getColumn(ptr), value);
        }
      }
    }

    Shape newShape = Shape.matrix(other.shape().rows, this.shape.columns);

    if (index == 0) {
      return new Zero(newShape);
    }

    values = Arrays.copyOf(values, index);

    return new CSR(newShape, values);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    return this.timesLeft((Diagonal) other);
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[this.nnz * other.numberOfRows()];

    for (int r = 0; r < other.numberOfRows(); r++) {
      double scalar = other.get(r, 0);

      if (scalar == 0) {
        continue;
      }

      for (int ptr = this.getRowStart(r); ptr < this.getRowEnd(r); ptr++) {
        double value = scalar * this.getValue(ptr);

        if (value != 0) {
          values[index++] = new MatrixCellValue(r, this.getColumn(ptr), value);
        }
      }
    }

    Shape newShape = Shape.matrix(other.numberOfRows(), this.shape.columns);

    if (index == 0) {
      return new Zero(newShape);
    }

    return new CSR(newShape, Arrays.copyOf(values, index));
  }
}
