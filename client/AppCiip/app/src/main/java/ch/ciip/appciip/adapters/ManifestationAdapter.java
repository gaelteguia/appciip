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
import ch.ciip.appciip.models.Manifestation;

public class ManifestationAdapter extends ArrayAdapter<Manifestation> {
    // View lookup cache
    private static class ViewHolder {
        public ImageView ivCover;
        public TextView tvTitle;
        public TextView tvDescription;
        public TextView tvStartTime;
        public TextView tvAddress;
    }

    public ManifestationAdapter(Context context, ArrayList<Manifestation> aManifestations) {
        super(context, 0, aManifestations);
    }

    // Translates a particular `Manifestation` given a position
    // into a relevant row within an AdapterView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Manifestation manifestation = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_manifestation, parent, false);
            viewHolder.ivCover = (ImageView) convertView.findViewById(R.id.ivManifestationCover);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.tvStartTime = (TextView) convertView.findViewById(R.id.tvStartTime);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (manifestation.getTitle().length() < 30)
            viewHolder.tvTitle.setText(manifestation.getTitle());
        else
            viewHolder.tvTitle.setText(manifestation.getTitle().substring(0, 30) + "...");

        if (manifestation.getDescription().length() < 100)
            viewHolder.tvDescription.setText(manifestation.getDescription());
        else
            viewHolder.tvDescription.setText(manifestation.getDescription().substring(0, 100) + "...");
        viewHolder.tvStartTime.setText(manifestation.getStartTime());
        viewHolder.tvAddress.setText(manifestation.getAddress());
        Picasso.with(getContext()).load(Uri.parse(manifestation.getCoverUrl())).error(R.drawable.ic_nocover).into(viewHolder.ivCover);
        // Return the completed view to render on screen
        return convertView;
    }
}