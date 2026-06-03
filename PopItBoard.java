package popitgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopItBoard extends JPanel {
    private final int ROWS = 6;
    private final int COLS = 6;
    
    private boolean[][] popped = new boolean[ROWS][COLS];
    private boolean[][] selected = new boolean[ROWS][COLS];
    
    private int hoverRow = -1, hoverCol = -1;
    private boolean isMyTurn = false;
    private Runnable onSelectionChanged;

    public PopItBoard(Runnable onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
        setOpaque(false);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isMyTurn) return;
                Point cell = getCellAt(e.getPoint());
                if (cell != null && !popped[cell.x][cell.y]) {
                    selected[cell.x][cell.y] = !selected[cell.x][cell.y];
                    if (onSelectionChanged != null) onSelectionChanged.run();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isMyTurn) return;
                Point cell = getCellAt(e.getPoint());
                if (cell != null && !popped[cell.x][cell.y]) {
                    if (cell.x != hoverRow || cell.y != hoverCol) {
                        hoverRow = cell.x; hoverCol = cell.y;
                        repaint();
                    }
                } else {
                    if (hoverRow != -1 || hoverCol != -1) {
                        hoverRow = -1; hoverCol = -1;
                        repaint();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1; hoverCol = -1;
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void setTurn(boolean myTurn) {
        this.isMyTurn = myTurn;
        clearSelection();
    }

    public void clearSelection() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) selected[r][c] = false;
        }
        repaint();
    }

    public boolean validateAndCommitMove() {
        int targetRow = -1;
        int minCol = COLS, maxCol = -1;
        int count = 0;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (selected[r][c]) {
                    if (targetRow == -1) targetRow = r;
                    else if (targetRow != r) return false; 
                    
                    if (c < minCol) minCol = c;
                    if (c > maxCol) maxCol = c;
                    count++;
                }
            }
        }

        if (count == 0) return false;

        for (int c = minCol; c <= maxCol; c++) {
            if (!selected[targetRow][c] || popped[targetRow][c]) return false; 
        }

        for (int c = minCol; c <= maxCol; c++) {
            popped[targetRow][c] = true;
            selected[targetRow][c] = false;
        }
        repaint();
        return true;
    }

    public boolean isBoardEmpty() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (!popped[r][c]) return false;
            }
        }
        return true;
    }

    public void syncState(boolean[][] remoteState) {
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(remoteState[r], 0, this.popped[r], 0, COLS);
        }
        repaint();
    }

    public boolean[][] getPoppedState() { return popped; }

    // Tính toán tọa độ chuột tự động theo kích thước khung
    private Point getCellAt(Point p) {
        int cellW = getWidth() / COLS;
        int cellH = getHeight() / ROWS;

        if (cellW == 0 || cellH == 0) return null;

        int c = p.x / cellW;
        int r = p.y / cellH;

        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) return new Point(r, c);
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Khung nhựa bo cạnh màu đen co giãn theo khung
        g2.setColor(new Color(18, 18, 18));
        g2.fillRoundRect(0, 0, w, h, 40, 40);

        int cellW = w / COLS;
        int cellH = h / ROWS;

        for (int r = 0; r < ROWS; r++) {
            g2.setColor(PopItTheme.ROW_COLORS[r]);
            g2.fillRect(0, r * cellH, w, cellH);

            for (int c = 0; c < COLS; c++) {
                int cx = c * cellW + cellW / 2;
                int cy = r * cellH + cellH / 2;
                int radius = Math.min(cellW, cellH) / 2 - 8;
                if(radius < 2) radius = 2;

                if (popped[r][c]) {
                    g2.setColor(new Color(35, 35, 40));
                    g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
                } else {
                    Color baseColor = PopItTheme.ROW_COLORS[r];
                    Color lightColor = selected[r][c] ? Color.WHITE : baseColor.brighter();
                    
                    RadialGradientPaint rgp = new RadialGradientPaint(
                        cx - radius/3f, cy - radius/3f, radius * 1.5f,
                        new float[]{0.0f, 1.0f}, new Color[]{lightColor, baseColor.darker()}
                    );
                    g2.setPaint(rgp);
                    g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.fillOval(cx - radius/2, cy - radius/2, radius/2, radius/3);

                    if (r == hoverRow && c == hoverCol) {
                        g2.setColor(new Color(255, 255, 255, 140));
                        g2.setStroke(new BasicStroke(3));
                        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
                    }
                }
            }
        }
    }
}