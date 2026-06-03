package popitgame;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerManager extends JFrame {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private boolean[][] boardState = new boolean[6][6];
    private int activeTurn = 0;

    public ServerManager() {
        setupServer();
    }

    private void setupServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(12345);
                System.out.println("=== SERVER DIỄN RA TRẬN ĐẤU ===");
                System.out.println("Đang chờ người chơi kết nối vào mạng...");
                
                while (clients.size() < 2) {
                    Socket s = serverSocket.accept();
                    int id = clients.size();
                    ClientHandler handler = new ClientHandler(s, id, this);
                    clients.add(handler);
                    new Thread(handler).start();
                }
                
                System.out.println("Đã đủ 2 người chơi! Bắt đầu trận đấu...");
                broadcast("START");
                
                String syncMsg = "SYNC";
                for (int r = 0; r < 6; r++) {
                    for (int c = 0; c < 6; c++) {
                        syncMsg += "|" + boardState[r][c];
                    }
                }
                broadcast(syncMsg);
                
                // KÍCH HOẠT LƯỢT ĐI ĐẦU TIÊN (Cho đội 0 đi trước)
                activeTurn = 0;
                broadcast("TURN|" + activeTurn); 
                
            } catch (Exception e) {
                System.out.println("Lỗi khởi tạo cổng Socket: " + e.getMessage());
            }
        }).start();
    }

    public void syncTurnMove(int id, boolean[][] resState) {
        this.boardState = resState;
        
        if (isBoardEmpty()) {
            broadcast("OVER|" + id); 
            return;
        }
        
        activeTurn = (id == 0) ? 1 : 0;
        
        String syncMsg = "SYNC";
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                syncMsg += "|" + boardState[r][c];
            }
        }
        broadcast(syncMsg);
        broadcast("TURN|" + activeTurn);
    }

    private boolean isBoardEmpty() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (!boardState[r][c]) return false;
            }
        }
        return true;
    }

    public void broadcast(String msg) {
        for (ClientHandler c : clients) c.send(msg);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private int id;
    private ServerManager manager;

    public ClientHandler(Socket socket, int id, ServerManager manager) {
        this.socket = socket;
        this.id = id;
        this.manager = manager;
    }
    
    public void send(String msg) { 
        if (out != null) out.println(msg); 
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("TEAM|" + id); 

            String packet;
            while ((packet = in.readLine()) != null) {
                if (packet.startsWith("MOVE")) {
                    String[] tokens = packet.split("\\|");
                    boolean[][] resState = new boolean[6][6];
                    int counter = 1;
                    for(int r=0; r<6; r++) {
                        for(int c=0; c<6; c++) {
                            resState[r][c] = Boolean.parseBoolean(tokens[counter++]);
                        }
                    }
                    manager.syncTurnMove(id, resState);
                }
            }
        } catch (Exception e) {}
    }
}