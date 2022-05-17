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
import pl.edu.mimuw.matrix.implementations.RowMatrix;
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
    var a = new RowMatrix(Shape.matrix(3, 3), new double[] { 4, 5, 6 });

    printMatrix(a);

    printMatrix(new Diagonal(1, 2, 3).times(a));

    // printMatrix(SPARSE_2X3.times(FULL_3X2));

    // Tu trzeba wpisać kod testujący toString dla poszczególnych macierzy i
    // wyników
    // operacji
  }
}
