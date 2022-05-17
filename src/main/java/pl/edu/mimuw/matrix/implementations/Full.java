package pl.edu.mimuw.matrix.implementations;

import java.util.ArrayList;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

// row, column
public class Full extends BaseMatrix {
  private double[][] data;
  private final Shape shape;

  public Full(double[][] data) {
    assert data != null;
    assert data.length > 0;

    int assumedRow = data[0].length;

    assert assumedRow > 0;

    for (double[] row : data) {
      assert row != null;
      assert row.length == assumedRow;
    }

    this.data = data;
    this.shape = Shape.matrix(this.data.length, this.data[0].length);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert this.shape().equals(other.shape());

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (scalar == 0) {
      return this;
    }

    int nnz = 0;
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        data[i][j] = this.get(i, j) + scalar;

        if (data[i][j] != 0) {
          nnz++;
        }
      }
    }

    if (nnz == 0) {
      return new Zero(this.shape);
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (scalar == 1) {
      return this;
    }

    if (scalar == 0) {
      return new Zero(this.shape);
    }

    int nnz = 0;
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        data[i][j] = this.get(i, j) * scalar;

        if (data[i][j] != 0) {
          nnz++;
        }
      }
    }

    if (nnz == 0) {
      return new Zero(this.shape);
    }

    return new Full(data);
  }

  private IDoubleMatrix plusLeftGeneric(IDoubleMatrix other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public double get(int row, int column) {
    assert (0 <= row && row < shape.rows);
    assert (0 <= column && column < shape.columns);

    return data[row][column];
  }

  @Override
  public double[][] data() {
    return this.data;
  }

  @Override
  public double normOne() {
    double max = 0;

    for (int c = 0; c < this.shape.columns; c++) {
      double sum = 0;

      for (int r = 0; r < this.shape.rows; r++) {
        sum += Math.abs(this.get(r, c));
      }

      max = Math.max(sum, max);
    }

    return max;
  }

  @Override
  public double normInfinity() {
    double max = 0;

    for (int r = 0; r < this.shape.rows; r++) {
      double sum = 0;

      for (int c = 0; c < this.shape.columns; c++) {
        sum += Math.abs(this.get(r, c));
      }

      max = Math.max(sum, max);
    }

    return max;
  }

  @Override
  public double frobeniusNorm() {
    double sum = 0;

    for (int r = 0; r < this.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        sum += Math.abs(Math.pow(this.get(r, c), 2));
      }
    }

    return Math.sqrt(sum);
  }

  @Override
  public Shape shape() {
    return this.shape;
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert other != null;
    assert other.shape.equals(this.shape);

    int nnz = 0;
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        data[i][j] = this.get(i, j) + other.get(i, j);

        if (data[i][j] != 0) {
          nnz++;
        }
      }
    }

    if (nnz == 0) {
      return new Zero(this.shape);
    }

    return new Full(data);
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

    ArrayList<MatrixCellValue> values = new ArrayList<>();
    MatrixCellValue[] data = new MatrixCellValue[0];

    for (int r = 0; r < other.shape().rows; r++) {
      int ri = other.getRowPointer(r);

      for (int c = 0; c < this.shape.columns; c++) {
        double sum = 0;

        for (int ptr = other.getRowStart(ri); ptr < other.getRowEnd(ri); ptr++) {
          sum += other.getValue(ptr) * this.get(other.getColumn(ptr), c);
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
  public IDoubleMatrix timesLeft(Full other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    double[][] data = new double[other.shape.rows][this.shape.columns];

    for (int r = 0; r < other.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        double sum = 0;

        for (int k = 0; k < this.shape.rows; k++) {
          sum += other.get(r, k) * this.get(k, c);
        }

        data[r][c] = sum;
      }
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    double[][] data = new double[other.shape().rows][this.shape.columns];

    for (int r = 0; r < other.shape().rows; r++) {
      double scalar = other.get(r, other.indexCompliment(r));

      if (scalar == 0) {
        continue;
      }

      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] = scalar * this.get(other.indexCompliment(r), c);
      }
    }

    return new Full(data);
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

    double[][] data = new double[other.shape().rows][this.shape.columns];

    for (int r = 0; r < other.shape().rows; r++) {
      double scalar = other.get(r, 0);

      if (scalar == 0) {
        continue;
      }

      for (int c = 0; c < this.shape.columns; c++) {
        data[r][c] = scalar * this.get(r, c);
      }
    }

    return new Full(data);
  }

  @Override
  public String toString() {
    String out = super.toString();

    for (int r = 0; r < this.shape.rows; r++) {
      for (int c = 0; c < this.shape.columns - 1; c++) {
        out += this.get(r, c) + " ";
      }

      out += this.get(r, this.shape.columns - 1) + "\n";
    }

    return out;
  }
}
