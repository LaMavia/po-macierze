package pl.edu.mimuw;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;
import static pl.edu.mimuw.matrix.Shape.matrix;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.implementations.AntiDiagonal;
import pl.edu.mimuw.matrix.implementations.CSR;
import pl.edu.mimuw.matrix.implementations.ColumnMatrix;
import pl.edu.mimuw.matrix.implementations.Diagonal;
import pl.edu.mimuw.matrix.implementations.Full;
import pl.edu.mimuw.matrix.implementations.Identity;
import pl.edu.mimuw.matrix.implementations.RowMatrix;
import pl.edu.mimuw.matrix.implementations.Vector;
import pl.edu.mimuw.matrix.implementations.Zero;

public class Main {

  private static final String spacer = "======================";

  private static void runBinary(IDoubleMatrix a, IDoubleMatrix b) {
    System.out.printf("%s\n Combining:\n A =\n%s\n B =\n%s\n", spacer, a, b);

    if (a.shape().equals(b.shape())) {
      var r = a.plus(b);
      System.out.printf("A + B =\n%s\n", r);

      r = a.minus(b);
      System.out.printf("A - B =\n%s\n", r);
    }

    if (a.shape().columns == b.shape().rows) {
      var r = a.times(b);
      System.out.printf("A * B =\n%s\n", r);
    }

    System.out.println(spacer);
  }

  private static void runUnary(IDoubleMatrix m) {
    System.out.printf("%s\n Testing:\n M =\n%s", spacer, m);

    System.out.printf("||M||_1 = %f\n", m.normOne());
    System.out.printf("||M||_∞ = %f\n", m.normInfinity());
    System.out.printf("||M||_F = %f\n", m.frobeniusNorm());

    System.out.println(spacer);
  }

  public static void main(String[] args) {
    IDoubleMatrix[] matrices = new IDoubleMatrix[] {
      new CSR(
        matrix(10, 10),
        cell(0, 0, 1),
        cell(2, 3, 2),
        cell(2, 8, 3),
        cell(6, 0, 4),
        cell(7, 2, 5),
        cell(7, 1, 6)
      ),
      new Zero(matrix(10, 10)),
      new Vector(1, 2, 0, 0, 7),
      new Identity(10),
      new RowMatrix(
        matrix(10, 10),
        new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }
      ),
      new ColumnMatrix(
        matrix(10, 10),
        new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }
      ),
      new Diagonal(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
      new AntiDiagonal(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
      new Full(
        new double[][] {
          new double[] { 1, 2, 3, 4, 5, 1, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 4, 5, 1, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 4, 5, 2, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
          new double[] { 1, 2, 7, 4, 5, 2, 7, 8, 9, 10 },
          new double[] { 1, 0, 3, 4, 5, 6, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 1, 5, 6, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 4, 5, 8, 7, 8, 9, 10 },
          new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
        }
      ),
    };

    for (IDoubleMatrix a : matrices) {
      for (IDoubleMatrix b : matrices) {
        runBinary(a, b);
      }

      runUnary(a);
    }
  }
}
