package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

// row, column
public class Full implements IDoubleMatrix {
  private double[][] data;
  private final Shape shape;

  public Full(double[][] data) {
    int assumedRow = data[0].length;

    for (double[] row : data) {
      assert row.length == assumedRow;
    }

    this.data = data;
    this.shape = Shape.matrix(this.data.length, this.data[0].length);
  }

  public IDoubleMatrix plusLeft(Zero other) {
    return this;
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert this.shape() == other.shape();

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double get(int row, int column) {
    assert (0 <= row && row < shape.rows) && (0 <= column && column < shape.columns);

    return data[row][column];
  }

  @Override
  public double[][] data() {
    return data;
  }

  @Override
  public double normOne() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double normInfinity() {
    // TODO Auto-generated method stub
    return 0;
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
