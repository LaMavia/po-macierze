package pl.edu.mimuw;

import java.util.Arrays;

import pl.edu.mimuw.matrix.DoubleMatrixFactory;
import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;
import pl.edu.mimuw.matrix.implementations.AntiDiagonal;
import pl.edu.mimuw.matrix.implementations.CSR;
import pl.edu.mimuw.matrix.implementations.Diagonal;
import pl.edu.mimuw.matrix.implementations.Full;
import pl.edu.mimuw.matrix.implementations.Identity;
import pl.edu.mimuw.matrix.implementations.Vector;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;
import static pl.edu.mimuw.matrix.Shape.matrix;
import static pl.edu.mimuw.matrix.DoubleMatrixFactory.*;

public class Main {

  private static void printMatrix(IDoubleMatrix m) {
    System.out.println("\n" + m);

    for (var row : m.data()) {
      System.out.println(Arrays.toString(row));
    }
  }

  public static void main(String[] args) {
    final IDoubleMatrix SPARSE_2X3 = sparse(matrix(2, 3),
        cell(0, 0, 1),
        cell(0, 1, 2),
        cell(0, 2, 3),
        cell(1, 0, 4),
        cell(1, 1, 5),
        cell(1, 2, 6));

    final IDoubleMatrix FULL_3X2 = full(new double[][] {
        new double[] { 1, 2 },
        new double[] { 3, 4 },
        new double[] { 5, 6 }
    });

  printMatrix(SPARSE_2X3);
  printMatrix(FULL_3X2);

  printMatrix(SPARSE_2X3.times(FULL_3X2));    

    // Tu trzeba wpisać kod testujący toString dla poszczególnych macierzy i
    // wyników
    // operacji
  }
}
