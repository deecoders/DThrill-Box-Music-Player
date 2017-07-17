package jaipur.com.dthrillbox; /**
 * Created by DEEPANSHU GUPTA on 7/7/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jaipur.com.dthrillbox.R;


/**
 * Created by DEEPANSHU GUPTA on 6/21/2017.
 */

public class CustomPlaylist extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> arraytitle;
    private final ArrayList<String> arrayartist;
    private final ArrayList<byte[]> arraypic;

    public CustomPlaylist(Activity context,ArrayList<String> arraytitle,ArrayList<String> arrayartist,ArrayList<byte[]> arraypic)
    {
        super(context, R.layout.list_single,arraytitle);
        this.context=context;
        this.arraytitle=arraytitle;
        this.arrayartist=arrayartist;
        this.arraypic=arraypic;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_single,null,true);

        TextView txtTitle=(TextView)rowView.findViewById(R.id.title1);
        TextView txtArtist=(TextView)rowView.findViewById(R.id.artist1);
        ImageView songimage=(ImageView)rowView.findViewById(R.id.img);

        txtTitle.setText(arraytitle.get(position));
        txtArtist.setText(arrayartist.get(position));

        try{
            Bitmap bitmap = BitmapFactory.decodeByteArray(arraypic.get(position), 0,arraypic.get(position).length);
            songimage.setImageBitmap(bitmap); //associated cover art in bitmap
            songimage.setAdjustViewBounds(true);
        }
        catch(Exception e){
            songimage.setImageResource(R.drawable.defaultmelody);
            songimage.setAdjustViewBounds(true);
        }
        return rowView;
    }
}
