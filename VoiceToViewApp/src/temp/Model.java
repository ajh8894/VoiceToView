package temp;

import java.io.Serializable;

public class Model implements Serializable {

	private byte buffers[];
	private int perFrame;

	public Model(byte[] buffers) {
		this.buffers = buffers;
	}

	public byte[] getBuffers() {
		return buffers;
	}

	public void setBuffers(byte[] buffers) {
		this.buffers = buffers;
	}

	public int getPerFrame() {
		return perFrame;
	}

	public void setPerFrame(int perFrame) {
		this.perFrame = perFrame;
	}
}