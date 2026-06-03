package popitgame;

import javax.swing.*;
import java.awt.*;

public class PopItMenu extends JFrame {
    
    public PopItMenu() {
        setTitle("Pop It Menu");
        setSize(480, 740);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(null);
        mainContainer.setPreferredSize(new Dimension(450, 680));
        mainContainer.setBackground(PopItTheme.MENU_BG);

        JLabel lblTitle = new JLabel("POP IT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 55));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 35, 450, 65);
        mainContainer.add(lblTitle);

        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PopItTheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
                g2.dispose();
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setBounds(35, 140, 380, 480);

        JButton btnBot = new PopItTheme.MenuButton("CHƠI VỚI MÁY", new Color(44, 182, 229));
        JButton btnHost = new PopItTheme.MenuButton("TẠO PHÒNG", new Color(117, 196, 108));
        JButton btnJoin = new PopItTheme.MenuButton("VÀO PHÒNG", new Color(242, 142, 94));

        btnBot.setBounds(40, 70, 300, 65);
        btnHost.setBounds(40, 180, 300, 65);
        btnJoin.setBounds(40, 290, 300, 65);

        // Nút Chơi với máy
        btnBot.addActionListener(e -> {
            new PlayOffline().setVisible(true);
            dispose();
        });
        
        // Nút Tạo phòng
        btnHost.addActionListener(e -> {
            ServerManager server = new ServerManager();
            server.setVisible(false);
            
            new PlayOnline("127.0.0.1").setVisible(true);
            dispose();
        });

        // Nút Vào phòng
        btnJoin.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog(this, "Nhập IP Máy chủ (Phòng đấu):", "127.0.0.1");
            if (ip == null || ip.trim().isEmpty()) return;
            
            new PlayOnline(ip).setVisible(true);
            dispose();
        });

        cardPanel.add(btnBot);
        cardPanel.add(btnHost);
        cardPanel.add(btnJoin);
        mainContainer.add(cardPanel);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 242, 245)); 
        wrapper.add(mainContainer);
        setContentPane(wrapper);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PopItMenu().setVisible(true);
        });
    }
}