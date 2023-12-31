
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class DualMatrix {
    private int rows;
    private int columns;
    private int objective;
    private int[] supplies;
    private int[] demands;
    private int[] A;
    private int[] u;
    private int[] v;
    private int[][] costs;
    private int[][] gamma;
    private int[][] D;
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
        computeInitialValues();
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

    private void computeInitialValues() {
        A = new int[rows + columns];

        for (int i = 0; i < columns; i++) {
            A[i] = demands[i];
        }

        for (int i = columns; i < columns + rows; i++) {
            A[i] = -supplies[i - columns];
        }

        u = new int[rows];
        v = new int[columns];

        for (int i = 0; i < v.length; i++) {
            v[i] = costs[0][i];
        }

        for (int i = 0; i < rows; i++) {
            u[i] = 0;

            for (int j = 0; j < columns; j++) {
                if (costs[i][j] < v[j]) {
                    v[j] = costs[i][j];
                }
            }
        }

        gamma = new int[rows + columns][columns];

        for (int j = 1; j <= columns; j++) {
            for (int i = 1; i <= rows; i++) {
                if (costs[i - 1][j - 1] == v[j - 1]) {
                    gamma[j - 1][0] = i;
                    gamma[j - 1][1] = j;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            gamma[columns + i][0] = i + 1;
        }

        D = new int[rows + columns][rows + columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows + columns; j++) {
                if (i == j) {
                    D[i][j] = 1;
                } else {
                    D[i][j] = 0;
                }
            }
        }

        for (int i = rows; i < rows + columns; i++) {
            for (int j = 0; j < rows + columns; j++) {
                if (i == j) {
                    D[i][j] = -1;
                } else {
                    D[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            D[i][gamma[1][i] + rows] = -1;
        }
        
        System.out.println(Arrays.deepToString(D));
        computeObjective();
    }

    private void computeObjective() {
        int sumDemands = 0;
        int sumSupplies = 0;

        for (int i = 0; i < demands.length; i++) {
            sumDemands += demands[i] * v[i];
        }

        for (int i = 0; i < supplies.length; i++) {
            sumSupplies += supplies[i] * u[i];
        }

        objective = sumDemands - sumSupplies;
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

        // int[][] allocation = dm.getAllocation();

        // for (int i = 0; i < allocation.length; i++) {
        // System.out.println(Arrays.toString(allocation[i]));
        // }
    }
}
