package popitgame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class PlayOnline extends JFrame {
    private int assignedTeam = -1; 
    private Socket socket;
    private PrintWriter out;

    private PopItBoard board;
    private JLabel lblStatus;
    private JButton btnConfirm;
    private JButton btnMenu;
    private JPanel mainContainer;

    private boolean isGameOver = false;
    private String matchResult = "";

    public PlayOnline(String ip) {
        setTitle("Pop It Online Room");
        setSize(480, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(350, 500)); // Chống thu nhỏ quá mức gây vỡ UI

        // Dùng BorderLayout để giao diện tự động lấp đầy và căn giữa
        mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(PopItTheme.BG_CORAL);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // --- TOP PANEL (Chữ Status + Nút Exit) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        lblStatus = new JLabel("ĐANG KẾT NỐI PHÒNG...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
        btnExit.addActionListener(e -> {
            try { if(socket != null) socket.close(); } catch(Exception ex){}
            new PopItMenu().setVisible(true); 
            dispose();
        });
        
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightTop.setOpaque(false);
        rightTop.add(btnExit);
        topPanel.add(rightTop, BorderLayout.EAST);

        // Nút tàng hình bên trái để cân bằng vị trí của Status
        JPanel leftTop = new JPanel();
        leftTop.setPreferredSize(new Dimension(65, 65));
        leftTop.setOpaque(false);
        topPanel.add(leftTop, BorderLayout.WEST);

        mainContainer.add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (Bàn cờ tự động co giãn + Lớp phủ Game Over) ---
        board = new PopItBoard(() -> btnConfirm.setVisible(true));
        board.setOpaque(false);
        
        // Tạo một bọc riêng cho bàn cờ để vẽ chữ Thắng/Thua đè lên trên
        JPanel boardWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);
                if (isGameOver) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Nền mờ đè lên bàn cờ (Xanh lá nếu Thắng, Đỏ nếu Thua)
                    if (matchResult.equals("BẠN THẮNG!")) {
                        g2.setColor(new Color(117, 196, 108, 220));
                    } else {
                        g2.setColor(new Color(244, 107, 98, 230));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                    // Chữ Thông báo kết quả
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 50));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(matchResult)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    // Đổ bóng chữ
                    g2.setColor(new Color(0, 0, 0, 80));
                    g2.drawString(matchResult, x + 4, y + 4);
                    
                    g2.setColor(Color.WHITE);
                    g2.drawString(matchResult, x, y);
                    g2.dispose();
                }
            }
        };
        boardWrapper.setOpaque(false);
        boardWrapper.add(board, BorderLayout.CENTER);
        mainContainer.add(boardWrapper, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Chứa Nút Xác nhận và Nút Về Menu) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 80)); // Giữ chiều cao cố định để không bị giật layout

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
        btnConfirm.addActionListener(e -> fireMoveToServer());
        
        // Nút Về Menu (Sẽ hiện khi Game Over)
        btnMenu = new PopItTheme.MenuButton("VỀ MENU", new Color(156, 106, 185)); // Màu Tím
        btnMenu.setPreferredSize(new Dimension(200, 60));
        btnMenu.setVisible(false);
        btnMenu.addActionListener(e -> {
            try { if(socket != null) socket.close(); } catch(Exception ex){}
            new PopItMenu().setVisible(true);
            dispose();
        });

        bottomPanel.add(btnConfirm);
        bottomPanel.add(btnMenu);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        // Gắn màn hình chính vào Frame
        setContentPane(mainContainer);
        linkServer(ip);
    }

    private void linkServer(String ip) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, 12345);
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String line;
                while ((line = in.readLine()) != null) {
                    readPacket(line);
                }
            } catch (Exception e) {
                lblStatus.setText("LỖI KẾT NỐI PHÒNG!");
            }
        }).start();
    }

    private void fireMoveToServer() {
        if(board.validateAndCommitMove()) {
            btnConfirm.setVisible(false);
            board.setTurn(false);
            
            StringBuilder sb = new StringBuilder("MOVE");
            boolean[][] popped = board.getPoppedState();
            for(int r=0; r<6; r++) {
                for(int c=0; c<6; c++) sb.append("|").append(popped[r][c]);
            }
            out.println(sb.toString());
        }
    }

    private void readPacket(String packet) {
        SwingUtilities.invokeLater(() -> {
            String[] tokens = packet.split("\\|");
            switch (tokens[0]) {
                case "TEAM":
                    assignedTeam = Integer.parseInt(tokens[1]);
                    setTitle(assignedTeam == 0 ? "POP IT - ĐỘI ĐỎ" : "POP IT - ĐỘI XANH");
                    
                    // Xử lý luồng chữ: Chờ đối thủ vào phòng
                    if (assignedTeam == 0) {
                        lblStatus.setText("ĐANG ĐỢI NGƯỜI THAM GIA...");
                    } else {
                        lblStatus.setText("BẠN: ĐỘI XANH (TRẬN ĐẤU SẮP BẮT ĐẦU)");
                    }
                    break;
                case "SYNC":
                    boolean[][] syncState = new boolean[6][6];
                    int counter = 1;
                    for(int r=0; r<6; r++) {
                        for(int c=0; c<6; c++) syncState[r][c] = Boolean.parseBoolean(tokens[counter++]);
                    }
                    board.syncState(syncState);
                    break;
                case "TURN":
                    boolean active = (Integer.parseInt(tokens[1]) == assignedTeam);
                    board.setTurn(active);
                    
                    // Đổi chữ theo lượt
                    if (active) {
                        lblStatus.setText("ĐẾN LƯỢT BẠN!");
                    } else {
                        lblStatus.setText("ĐANG ĐỢI ĐỐI THỦ ĐI...");
                    }
                    break;
                case "OVER":
                    int loser = Integer.parseInt(tokens[1]);
                    
                    // Kiểm tra xem ai bóp bóng cuối cùng
                    if (loser == assignedTeam) {
                        matchResult = "BẠN THUA!";
                    } else {
                        matchResult = "BẠN THẮNG!";
                    }
                    
                    isGameOver = true;
                    lblStatus.setVisible(false);
                    btnConfirm.setVisible(false);
                    btnMenu.setVisible(true); // Hiển thị nút Về Menu
                    mainContainer.repaint(); // Cập nhật lại UI để vẽ lớp mờ Game Over
                    break;
            }
        });
    }
}