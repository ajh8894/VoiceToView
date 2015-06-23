package temp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

class ListAdapter extends ArrayAdapter<ListViewItem> {
	private Context mContext;
	private int mLayout;
	private LayoutInflater mInflater;
	private ArrayList<ListViewItem> mItemList;
	
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_COMBO = 1;
	public static final int TYPE_Spinner = 2;
	 
	public ListAdapter(Context context, int layout, ArrayList<ListViewItem> items) {
		super(context, layout, items);
			this.mContext = context;
			this.mLayout = layout;
			this.mItemList = items;
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mItemList.get(position).getType();
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		ListViewItem listViewItem = mItemList.get(position);
		int itemType = getItemViewType(position);
		
		if (convertView == null) {
			convertView = mInflater.inflate(mLayout, parent, false);
			
			viewHolder = new ViewHolder();
			if (itemType == TYPE_NORMAL) {
			/*	viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconImage);
		        viewHolder.name = (TextView) convertView.findViewById(R.id.name);
		        viewHolder.address = (TextView) convertView.findViewById(R.id.address);
		        viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);*/
				/*convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.type_black, null);*/
			} else if (itemType == TYPE_COMBO) {

			} else if (itemType == TYPE_Spinner) {

			}

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	    /*viewHolder.name.setText(mItemList.get(position).getName());
	    viewHolder.address.setText(mItemList.get(position).getAddress());
	    viewHolder.phone.setText(mItemList.get(position).getPhone());*/

		return convertView;
	}
	
	public class ViewHolder
	{
	  public ImageView icon;
	  public TextView content;
	 
	  public Spinner spinner;
	}
}