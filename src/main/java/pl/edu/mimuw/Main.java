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
    final var l = DoubleMatrixFactory.sparse(
        matrix(1_000_000, 1_000_000_000),
        cell(0, 0, 3),
        cell(0, 213, 2),
        cell(0, 555_555, 66),

        cell(456_456, 1, 7),
        cell(456_456, 321, 8),
        cell(456_456, 444_444, 66)

    );
    final var r = DoubleMatrixFactory.sparse(
        matrix(1_000_000_000, 1_000_000),
        cell(0, 0, 4),
        cell(213, 0, 5),
        cell(666_666, 0, 66),

        cell(1, 456_456, 9),
        cell(321, 456_456, 10),
        cell(444_445, 456_456, 66));
    final var result = l.times(r);

    System.out.println(result);

    // printMatrix(SPARSE_2X3.times(FULL_3X2));

    // Tu trzeba wpisać kod testujący toString dla poszczególnych macierzy i
    // wyników
    // operacji
  }
}
