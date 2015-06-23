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
            // 리스너 소켓 생성 후 대기
            serverSocket = new ServerSocket(8080);
            System.out.println("서버가 시작되었습니다.");
 
            // 연결되면 통신용 소켓 생성
            socket = serverSocket.accept();
            System.out.println("클라이언트와 연결되었습니다.");
 
            // 파일 수신 작업 시작
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
            System.out.println("파일 수신 작업을 시작합니다.");
            oips = new ObjectInputStream(socket.getInputStream());
 
            // 파일명을 전송 받고 파일명 수정.
            Chunk fName = (Chunk) oips.readObject();
            System.out.println("파일명 " + fName.toString() + "을 전송받았습니다.");
           
            //fName = fName.replaceAll("a", "b");
 
            // 파일을 생성하고 파일에 대한 출력 스트림 생성
            //File f = new File(fName);
            //fos = new FileOutputStream(f);
            //bos = new BufferedOutputStream(fos);
            //System.out.println(fName + "파일을 생성하였습니다.");
 
            // 바이트 데이터를 전송받으면서 기록
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
            System.out.println("파일 수신 작업을 완료하였습니다.");
           // System.out.println("받은 파일의 사이즈 : " + f.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}