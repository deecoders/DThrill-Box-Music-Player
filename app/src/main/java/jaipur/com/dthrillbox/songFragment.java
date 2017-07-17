package jaipur.com.dthrillbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import jaipur.com.dthrillbox.dummy.DummyContent;
import jaipur.com.dthrillbox.dummy.DummyContent.DummyItem;

import java.util.List;

import static jaipur.com.dthrillbox.GlobalList.arrayartist;
import static jaipur.com.dthrillbox.GlobalList.arraypic;
import static jaipur.com.dthrillbox.GlobalList.arraytitle;



public class songFragment extends ListFragment {


    public songFragment() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        CustomPlaylist adapter=new CustomPlaylist(getActivity(),arraytitle,arrayartist,arraypic);
         setListAdapter(adapter);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        getListView().setSelector(android.R.color.holo_blue_dark);
        playsong(position);


    }

    public void playsong(int position)
    {

        MainActivity activity=(MainActivity)getActivity();
        activity.playapp(position);
    }

}



