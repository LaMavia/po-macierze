package pl.edu.mimuw.matrix;

public class DoubleMatrixFactory {

  private DoubleMatrixFactory() {
  }

  public static IDoubleMatrix sparse(Shape shape, MatrixCellValue... values) {
    return null; // CSR
  }

  public static IDoubleMatrix full(double[][] values) {
    return null; // 2D array
  }

  public static IDoubleMatrix identity(int size) {
    return null; // (anti-/)diagonal conditions -> [1] | [0]
  }

  public static IDoubleMatrix diagonal(double... diagonalValues) {
    return null; // Array of values, [(i, i)] -> [i], [(i, j)] -> [0]
  }

  public static IDoubleMatrix antiDiagonal(double... antiDiagonalValues) {
    return null; // Array of values, [(i, N-i)] -> [i], [(i, j)] -> [0]
  }

  public static IDoubleMatrix vector(double... values) {
    return null; // Array of values, [(i, 0)] -> [i], [(i, j)] -> [0]
  }

  public static IDoubleMatrix zero(Shape shape) {
    return null; // nada
  }
}
