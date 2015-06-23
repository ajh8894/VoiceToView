package temp;

class ListViewItem
{
	private int type;
	private int img;
	private String name;
	private int sw;
	
	public ListViewItem(int img, String name, int sw)
	{
		this.img = img;
		this.name = name;
		this.sw = sw;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSw() {
		return sw;
	}

	public void setSw(int sw) {
		this.sw = sw;
	}

}