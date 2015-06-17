import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
 
public class ReceiveServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
 
        try {
            // ������ ���� ���� �� ���
            serverSocket = new ServerSocket(7777);
            System.out.println("������ ���۵Ǿ����ϴ�.");
 
            // ����Ǹ� ��ſ� ���� ����
            socket = serverSocket.accept();
            System.out.println("Ŭ���̾�Ʈ�� ����Ǿ����ϴ�.");
 
            // ���� ���� �۾� ����
            FileReceiver fr = new FileReceiver(socket);
            fr.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
}
 
class FileReceiver extends Thread {
    Socket socket;
    DataInputStream dis;
    FileOutputStream fos;
    BufferedOutputStream bos;
 
    public FileReceiver(Socket socket) {
        this.socket = socket;
    }
 
    @Override
    public void run() {
        try {
            System.out.println("���� ���� �۾��� �����մϴ�.");
            dis = new DataInputStream(socket.getInputStream());
 
            // ���ϸ��� ���� �ް� ���ϸ� ����.
            String fName = dis.readUTF();
            System.out.println("���ϸ� " + fName + "�� ���۹޾ҽ��ϴ�.");
            fName = fName.replaceAll("a", "b");
 
            // ������ �����ϰ� ���Ͽ� ���� ��� ��Ʈ�� ����
            File f = new File(fName);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            System.out.println(fName + "������ �����Ͽ����ϴ�.");
 
            // ����Ʈ �����͸� ���۹����鼭 ���
            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = dis.read(data)) != -1) {
                bos.write(data, 0, len);
            }
 
            bos.flush();
            bos.close();
            fos.close();
            dis.close();
            System.out.println("���� ���� �۾��� �Ϸ��Ͽ����ϴ�.");
            System.out.println("���� ������ ������ : " + f.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}