package popitgame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PopItTheme {
    // Bảng màu thiết kế cao cấp
    public static final Color BG_CORAL = new Color(244, 107, 98);
    public static final Color MENU_BG = new Color(235, 201, 112);
    public static final Color CARD_BG = Color.WHITE;
    
    // Màu sắc các hàng bóng Pop It cầu vồng (Theo ảnh 1)
    public static final Color[] ROW_COLORS = {
        new Color(156, 106, 185), // Tím
        new Color(44, 182, 229),  // Xanh dương
        new Color(117, 196, 108), // Xanh lá
        new Color(246, 196, 85),  // Vàng
        new Color(242, 142, 94),  // Cam
        new Color(237, 108, 114)  // Đỏ hồng
    };

    // Thiết kế nút bấm bo tròn có đổ bóng đổ khối hiện đại
    public static class MenuButton extends JButton {
        public MenuButton(String text, Color bgColor) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setBackground(bgColor);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Đổ bóng chân nút tạo hiệu ứng 3D
            g2.setColor(getBackground().darker());
            g2.fill(new RoundRectangle2D.Float(0, 4, getWidth(), getHeight() - 4, 25, 25));
            // Mặt nút chính
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() - 4, 25, 25));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}