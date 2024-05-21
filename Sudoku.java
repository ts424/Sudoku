import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Sudoku extends JFrame implements ActionListener, KeyListener {
    private static final int SIZE = 9; // 9x9 Sudoku
    private JTextField[][] textFields; // 2D array to hold JTextField references

    public Sudoku() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());
        textFields = new JTextField[SIZE][SIZE];

        // Create and add the menu panel
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        // Create and add the Sudoku board
        JPanel sudokuBoard = createSudokuBoard();
        add(sudokuBoard, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.blue);

        JButton newGame = new JButton("New Game");
        JButton solveGame = new JButton("Solve");
        JButton checkGame = new JButton("Check Solution");
        JButton finalcheck = new JButton("Final Check");
        newGame.setBackground(Color.orange);
        finalcheck.setBackground(Color.green);
        panel.add(newGame);
        panel.add(solveGame);
        panel.add(checkGame);
        panel.add(finalcheck);

        newGame.addActionListener(e -> startNewGame());
        solveGame.addActionListener(e -> solveGame());
        checkGame.addActionListener(e -> checkSolution());
        finalcheck.addActionListener(e -> finalCheck());
        
        return panel;
    }

    private JPanel createSudokuBoard() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));
        Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 21);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JTextField textField = new JTextField();
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.addKeyListener(this);
                textField.setFont(boldFont);
                textField.setBackground(Color.pink);
                textFields[row][col] = textField; // Store the reference in the array
                panel.add(textField);
            }
        }

        return panel;
    }

    private void startNewGame() {
        char[][] puzzle = generatePuzzle();

        // Fill the Sudoku board with the generated puzzle
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                char digit = puzzle[row][col];
                if (digit != '.') {
                    textFields[row][col].setText(String.valueOf(digit));
                    textFields[row][col].setEditable(false); // Make filled cells uneditable
                } else {
                    textFields[row][col].setText(""); // Clear the remaining cells
                    textFields[row][col].setEditable(true); // Make empty cells editable
                }
            }
        }
    }

      private void solveGame() {
        char[][] board = new char[SIZE][SIZE];

        // Populate the board with values from the JTextFields
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = textFields[row][col].getText();
                if (!text.isEmpty()) {
                    char digit = text.charAt(0);
                    board[row][col] = digit;
                } else {
                    board[row][col] = '.'; // Use '.' to represent empty cells
                }
            }
        }

        if (solveSudoku(board)) {
            // Update the JTextFields with the solved puzzle
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    textFields[row][col].setText(String.valueOf(board[row][col]));
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No solution exists for the given Sudoku puzzle.", "Solution", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean solveSudoku(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '.') {
                    for (char c = '1'; c <= '9'; c++) {
                        if (isValid(board, i, j, c)) {
                            board[i][j] = c;
                            if (solveSudoku(board))
                                return true;
                            else
                                board[i][j] = '.';
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSudokuValid() {
        char[][] board = new char[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = textFields[row][col].getText();
                if (!text.isEmpty()) {
                    char digit = text.charAt(0);
                    board[row][col] = digit;
                } else {
                    board[row][col] = '.';
                }
            }
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                char currentChar = board[row][col];
                if (currentChar != '.') {
                    if (!isValid(board, row, col, currentChar)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isValid(char[][] board, int row, int col, char c) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == c || board[row][i] == c) {
                return false;
            }
            int subgridRow = 3 * (row / 3) + i / 3;
            int subgridCol = 3 * (col / 3) + i % 3;
            if (board[subgridRow][subgridCol] == c) {
                return false;
            }
        }
        return true;
    }

    public static char[][] generatePuzzle() {
        char[][] puzzle = new char[SIZE][SIZE];
        Random random = new Random();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                puzzle[row][col] = '.';
            }
        }

        int numToFill = random.nextInt(6) + 15;
        for (int i = 0; i < numToFill; i++) {
            int row = random.nextInt(SIZE);
                        int col = random.nextInt(SIZE);
            char digit = getRandomValidDigit(puzzle, row, col);
            puzzle[row][col] = digit;
        }

        return puzzle;
    }

    private static char getRandomValidDigit(char[][] puzzle, int row, int col) {
        Random random = new Random();
        char[] validDigits = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        for (int i = validDigits.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = validDigits[index];
            validDigits[index] = validDigits[i];
            validDigits[i] = temp;
        }

        for (char digit : validDigits) {
            if (isValid(puzzle, row, col, digit)) {
                return digit;
            }
        }

        return '.';
    }
    
    private void checkSolution() {
    // Create a 2D character array to represent the Sudoku board
    char[][] board = new char[SIZE][SIZE];

    // Populate the board with values from the JTextFields
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            String text = textFields[row][col].getText();
            if (!text.isEmpty()) {
                char digit = text.charAt(0);
                board[row][col] = digit;
            } else {
                board[row][col] = '.'; // Use '.' to represent empty cells
            }
        }
    }

    // Check if the Sudoku board is valid
    boolean isValid = solveSudoku(board);

    if (isValid) {
        JOptionPane.showMessageDialog(this, "Your Sudoku puzzle is valid till now.", "Solution Validity", JOptionPane.INFORMATION_MESSAGE);
    } else {
                 JOptionPane.showMessageDialog(this, "Oops! You have put in a wrong entry in the puzzle.", "Solution Validity", JOptionPane.ERROR_MESSAGE);

    }
}

    private void finalCheck() {
    // Create a 2D character array to represent the Sudoku board
    char[][] board = new char[SIZE][SIZE];
    boolean flag=true;
    // Populate the board with values from the JTextFields
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            String text = textFields[row][col].getText();
            if (!text.isEmpty()) {
                char digit = text.charAt(0);
                board[row][col] = digit;
            } else {
                flag = false; // If any cell is empty, return false
            }
        }
    }
    if(flag){
         JOptionPane.showMessageDialog(this, "Congratulations! Your Sudoku puzzle is Solved Correcctly.", "Solution Validity", JOptionPane.INFORMATION_MESSAGE);    }
    else{
                JOptionPane.showMessageDialog(this, "Oops! The Sudoku puzzle solution is invalid.", "Solution Validity", JOptionPane.ERROR_MESSAGE);

    }
    
    // Check if the Sudoku board is valid
    
}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Sudoku game = new Sudoku();
            game.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Implement action event handling
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Implement key typed event handling
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Implement key pressed event handling
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Implement key released event handling
    }
}

