
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class DualMatrix {
    private int rows;
    private int columns;
    private int[] supplies;
    private int[] demands;
    private int[] A;
    private int[] u;
    private int[] v;
    private int[][] basicCells;
    private int[][] costs;
    private int[][] allocation;

    public DualMatrix() {
    }

    public void readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner input = new Scanner(file);
        rows = input.nextInt();
        columns = input.nextInt();
        supplies = new int[rows];
        demands = new int[columns];
        costs = new int[rows][columns];

        final int size = rows >= columns ? rows : columns;

        for (int i = 0; i < size; i++) {
            if (i < rows) {
                supplies[i] = input.nextInt();
            }

            if (i < columns) {
                demands[i] = input.nextInt();
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                costs[i][j] = input.nextInt();
            }
        }

        input.close();
    }

    public void solve() {
        int m = supplies.length;
        int n = demands.length;

        while (true) {
            int[][] dualMatrix = computeDualMatrix();

            int leavingRow = findLeavingRow(dualMatrix);
            if (leavingRow == -1) {
                break; // optimal solution found
            }

            int enteringColumn = findEnteringColumn(dualMatrix, leavingRow);
            if (enteringColumn == -1) {
                throw new IllegalStateException("The problem is unbounded"); // unbounded problem
            }

            allocate(leavingRow, enteringColumn);
        }
    }

    public void computeInitialValues() {
        A = new int[rows + columns];

        for (int i = 0; i < columns; i++) {
            A[i] = demands[i];
        }
        for (int i = columns; i < columns + rows; i++) {
            A[i] = -supplies[i - columns];
        }

        u = new int[rows];
        v = new int[columns];

        v[0] = costs[0][0];
        v[0] = costs[0][1];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (costs[i][j] > v[j]) {
                    v[j] = costs[i][j];
                }
            }
        }

        basicCells = new int[rows + columns][columns];

        for (int i = 0; i < rows + columns; i++) {
            for (int j = 0; j < columns; j++) {
                if (costs[i][j] > v[j]) {
                    v[j] = costs[i][j];
                }
            }
        }
    }

    private int[][] computeDualMatrix() {
        int m = supplies.length;
        int n = demands.length;
        int[][] dualMatrix = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dualMatrix[i][j] = costs[i][j];
            }
        }

        return dualMatrix;
    }

    private int findLeavingRow(int[][] dualMatrix) {
        int m = supplies.length;
        int n = demands.length;
        int leavingRow = -1;
        int minDualValue = Integer.MAX_VALUE;

        for (int i = 0; i < m; i++) {
            int dualValue = 0;
            for (int j = 0; j < n; j++) {
                dualValue += dualMatrix[i][j] * allocation[i][j];
            }

            if (dualValue < minDualValue) {
                minDualValue = dualValue;
                leavingRow = i;
            }
        }

        return leavingRow;
    }

    private int findEnteringColumn(int[][] dualMatrix, int leavingRow) {
        int m = supplies.length;
        int n = demands.length;
        int enteringColumn = -1;
        int minReducedCost = Integer.MAX_VALUE;

        for (int j = 0; j < n; j++) {
            int reducedCost = dualMatrix[leavingRow][j];
            if (reducedCost < minReducedCost) {
                minReducedCost = reducedCost;
                enteringColumn = j;
            }
        }

        return enteringColumn;
    }

    private void allocate(int leavingRow, int enteringColumn) {
        int quantity = Math.min(supplies[leavingRow], demands[enteringColumn]);
        allocation[leavingRow][enteringColumn] = quantity;
        supplies[leavingRow] -= quantity;
        demands[enteringColumn] -= quantity;
    }

    public int[][] getAllocation() {
        return allocation;
    }

    public static void main(String[] args) {
        String fileName = "./problem_1.txt";
        DualMatrix dm = new DualMatrix();

        try {
            dm.readFile(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // dm.solve();
        // int[][] allocation = dm.getAllocation();

        // for (int i = 0; i < allocation.length; i++) {
        // System.out.println(Arrays.toString(allocation[i]));
        // }
    }
}
