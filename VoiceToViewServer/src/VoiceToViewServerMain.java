import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.swmem.voicetoview.data.Chunk;
 
public class VoiceToViewServerMain {
	private BlockingQueue<Chunk> senderQueue;
	private BlockingQueue<Chunk> receiverQueue;
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
 
        try {
            // ������ ���� ���� �� ���
            serverSocket = new ServerSocket(8080);
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
    ObjectInputStream oips;
 
    public FileReceiver(Socket socket) {
        this.socket = socket;
    }
 
    @Override
    public void run() {
        try {
            System.out.println("���� ���� �۾��� �����մϴ�.");
            oips = new ObjectInputStream(socket.getInputStream());
 
            // ���ϸ��� ���� �ް� ���ϸ� ����.
            Chunk fName = (Chunk) oips.readObject();
            System.out.println("���ϸ� " + fName.toString() + "�� ���۹޾ҽ��ϴ�.");
           
            //fName = fName.replaceAll("a", "b");
 
            // ������ �����ϰ� ���Ͽ� ���� ��� ��Ʈ�� ����
            //File f = new File(fName);
            //fos = new FileOutputStream(f);
            //bos = new BufferedOutputStream(fos);
            //System.out.println(fName + "������ �����Ͽ����ϴ�.");
 
            // ����Ʈ �����͸� ���۹����鼭 ���
/*            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = dis.read(data)) != -1) {
                bos.write(data, 0, len);
            }
 
            bos.flush();
            bos.close();
            fos.close();
            dis.close();*/
            oips.close();
            System.out.println(fName.getText());
            System.out.println("���� ���� �۾��� �Ϸ��Ͽ����ϴ�.");
           // System.out.println("���� ������ ������ : " + f.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}