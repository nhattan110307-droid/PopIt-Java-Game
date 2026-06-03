package popitgame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayOffline extends JFrame {
    private PopItBoard board;
    private JLabel lblStatus;
    private JButton btnConfirm;
    private JPanel mainContainer;
    
    private boolean isPlayerTurn = true;
    private boolean isGameOver = false;
    private String winMessage = "";

    public PlayOffline() {
        setTitle("Chơi với Máy - Pop It");
        setSize(480, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(350, 500)); // Chống thu nhỏ quá mức gãy UI

        mainContainer = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);
                if (isGameOver) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(244, 107, 98, 235));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 45));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(winMessage)) / 2;
                    g2.drawString(winMessage, x, getHeight() / 2 - 10);
                    g2.dispose();
                }
            }
        };
        // Set Full màu đỏ cho Game
        mainContainer.setBackground(PopItTheme.BG_CORAL);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // --- TOP PANEL (Chữ Status + Nút Exit) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        lblStatus = new JLabel("LƯỢT CỦA BẠN!", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblStatus.setForeground(Color.WHITE);
        topPanel.add(lblStatus, BorderLayout.CENTER);

        JButton btnExit = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Impact", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "EXIT";
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(txt, x, y);
                g2.dispose();
            }
        };
        btnExit.setPreferredSize(new Dimension(65, 65));
        btnExit.setFocusPainted(false);
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(e -> { new PopItMenu().setVisible(true); dispose(); });
        
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightTop.setOpaque(false);
        rightTop.add(btnExit);
        topPanel.add(rightTop, BorderLayout.EAST);

        // Bù 1 khoảng trắng bên trái để Status căn giữa hoàn hảo
        JPanel leftTop = new JPanel();
        leftTop.setPreferredSize(new Dimension(65, 65));
        leftTop.setOpaque(false);
        topPanel.add(leftTop, BorderLayout.WEST);

        mainContainer.add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (Bàn cờ tự động co giãn) ---
        board = new PopItBoard(() -> btnConfirm.setVisible(true));
        board.setTurn(true);
        mainContainer.add(board, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Nút Bóp Dấu Tick) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);

        btnConfirm = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                g2.setColor(PopItTheme.BG_CORAL);
                g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth()/2, cy = getHeight()/2;
                g2.drawLine(cx - 15, cy, cx - 2, cy + 12);
                g2.drawLine(cx - 2, cy + 12, cx + 18, cy - 12);
                g2.dispose();
            }
        };
        btnConfirm.setPreferredSize(new Dimension(80, 80));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setVisible(false);
        btnConfirm.addActionListener(e -> processTurn());
        
        JPanel confirmWrapper = new JPanel(new BorderLayout());
        confirmWrapper.setOpaque(false);
        confirmWrapper.setPreferredSize(new Dimension(80, 80)); // Giữ chỗ tránh bị nhảy layout
        confirmWrapper.add(btnConfirm, BorderLayout.CENTER);
        
        bottomPanel.add(confirmWrapper);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainContainer); // Gắn thẳng không cần lớp lót GridBag
    }

    private void processTurn() {
        if (!isPlayerTurn) return;
        if (board.validateAndCommitMove()) {
            btnConfirm.setVisible(false);
            if (board.isBoardEmpty()) {
                endGame("BOT THẮNG");
                return;
            }
            isPlayerTurn = false;
            lblStatus.setText("BOT ĐANG ĐI...");
            Timer timer = new Timer(900, e -> processBotTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác lỗi! Hãy bóp các nút cạnh nhau liên tục trên 1 hàng.");
            board.clearSelection();
            btnConfirm.setVisible(false);
        }
    }

    private void processBotTurn() {
        boolean[][] state = board.getPoppedState();
        List<Point[]> potentialMoves = new ArrayList<>();
        
        for (int r = 0; r < 6; r++) {
            int startIdx = -1;
            for (int c = 0; c < 6; c++) {
                if (!state[r][c]) {
                    if (startIdx == -1) startIdx = c;
                } else {
                    if (startIdx != -1) {
                        packSegments(r, startIdx, c - 1, potentialMoves);
                        startIdx = -1;
                    }
                }
            }
            if (startIdx != -1) packSegments(r, startIdx, 5, potentialMoves);
        }

        if (!potentialMoves.isEmpty()) {
            Point[] chosenMove = potentialMoves.get(new Random().nextInt(potentialMoves.size()));
            for (Point p : chosenMove) state[p.x][p.y] = true;
            board.syncState(state);
        }

        if (board.isBoardEmpty()) {
            endGame("BẠN THẮNG");
            return;
        }

        isPlayerTurn = true;
        lblStatus.setText("LƯỢT CỦA BẠN!");
        board.setTurn(true);
    }

    private void packSegments(int r, int start, int end, List<Point[]> moves) {
        for (int i = start; i <= end; i++) {
            for (int j = i; j <= end; j++) {
                Point[] move = new Point[j - i + 1];
                for (int k = i; k <= j; k++) move[k - i] = new Point(r, k);
                moves.add(move);
            }
        }
    }

    private void endGame(String msg) {
        isGameOver = true;
        winMessage = msg;
        lblStatus.setVisible(false);
        board.setVisible(false);
        mainContainer.repaint();
        
        Timer t = new Timer(3500, e -> { new PopItMenu().setVisible(true); dispose(); });
        t.setRepeats(false);
        t.start();
    }
}