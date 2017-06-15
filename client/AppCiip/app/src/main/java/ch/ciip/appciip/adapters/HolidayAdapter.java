package ch.ciip.appciip.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ch.ciip.appciip.R;
import ch.ciip.appciip.models.Holiday;

public class HolidayAdapter extends ArrayAdapter<Holiday> {
    // View lookup cache
    private static class ViewHolder {
        public ImageView ivCover;
        public TextView tvTitle;
        public TextView tvDescription;
        public TextView tvStartTime;
        public TextView tvCanton;
    }

    public HolidayAdapter(Context context, ArrayList<Holiday> aHolidays) {
        super(context, 0, aHolidays);
    }

    // Translates a particular `Holiday` given a position
    // into a relevant row within an AdapterView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Holiday holiday = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_holiday, parent, false);
            viewHolder.ivCover = (ImageView)convertView.findViewById(R.id.ivHolidayCover);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
            viewHolder.tvStartTime = (TextView)convertView.findViewById(R.id.tvStartTime);
            viewHolder.tvCanton = (TextView)convertView.findViewById(R.id.tvCanton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tvTitle.setText(holiday.getTitle());
        viewHolder.tvDescription.setText(holiday.getDescription());
        viewHolder.tvStartTime.setText(holiday.getStartTime());
        viewHolder.tvCanton.setText(holiday.getCanton());
        Picasso.with(getContext()).load(Uri.parse(holiday.getCoverUrl())).error(R.drawable.ic_nocover).into(viewHolder.ivCover);
        // Return the completed view to render on screen
        return convertView;
    }
}