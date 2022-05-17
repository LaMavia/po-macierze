package pl.edu.mimuw.matrix;

import pl.edu.mimuw.matrix.implementations.AntiDiagonal;
import pl.edu.mimuw.matrix.implementations.CSR;
import pl.edu.mimuw.matrix.implementations.Diagonal;
import pl.edu.mimuw.matrix.implementations.Full;
import pl.edu.mimuw.matrix.implementations.Identity;
import pl.edu.mimuw.matrix.implementations.Vector;
import pl.edu.mimuw.matrix.implementations.Zero;

public class DoubleMatrixFactory {

  private DoubleMatrixFactory() {}

  public static IDoubleMatrix sparse(Shape shape, MatrixCellValue... values) {
    return new CSR(shape, values);
  }

  public static IDoubleMatrix full(double[][] values) {
    return new Full(values);
  }

  public static IDoubleMatrix identity(int size) {
    return new Identity(size);
  }

  public static IDoubleMatrix diagonal(double... diagonalValues) {
    return new Diagonal(diagonalValues);
  }

  public static IDoubleMatrix antiDiagonal(double... antiDiagonalValues) {
    return new AntiDiagonal(antiDiagonalValues);
  }

  public static IDoubleMatrix vector(double... values) {
    return new Vector(values);
  }

  public static IDoubleMatrix zero(Shape shape) {
    return new Zero(shape);
  }
}
