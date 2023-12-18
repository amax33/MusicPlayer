package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ListView listView;//listing all songs
    String[] items;//containing all songs title
    Button shuffle;//use to randomly play a song
    ArrayList<song> my_songs, selected = new ArrayList<>();//to display songs, to select multiple songs
    Boolean is_menu_active = false;//for activating checkbox
    CheckBox checkBox;//to select multiple items
    View my_view;//view of each song
    ActionMode actionmode = null;//for deleting
    Boolean flag = true;//use for not selecting an item twicw
    public static ArrayList<song> mSongsList = new ArrayList<>();//contain found songs in all storages
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewsong);
        runtime_permission();//getting permissions
        //randomly play a song:
        shuffle = findViewById(R.id.Shuffle_first);
        Random r = new Random();
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int random = r.nextInt(listView.getAdapter().getCount() - 1);
                String songname = (String) listView.getItemAtPosition(random);
                startActivity(new Intent(getApplicationContext(), Player.class)
                        .putExtra("songname", songname)
                        .putExtra("pos", random));
            }
        });

        //for deleting multiple items using contextual menu:
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            }
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionmode = actionMode;
                //changing actionmode based on checkbox function;
                MenuInflater inf = actionMode.getMenuInflater();
                inf.inflate(R.menu.my_context_menu, menu);
                is_menu_active = true;//used to show checkbox.
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                //for deleting a song first we remove it from arraylist of songs
                // then we show list again.
                if (menuItem.getItemId() == R.id.delete) {
                    for (song song : selected) {
                        mSongsList.remove(song);
                    }
                    Toast.makeText(getBaseContext(), "Deleted succesfully!!", Toast.LENGTH_SHORT).show();
                    my_songs = mSongsList;
                    selected.clear();//emptying list of selected items
                    Display_Songs();//showing songs again without deleted ones
                    actionMode.finish();
                }
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selected.clear();
                is_menu_active = false;//closing checkbox.
            }
        });
    }
    //getting needed permission using Dexter:
    public void runtime_permission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        //if permission granted, we find songs on phone and then display them.
                        my_songs = Find_Songs();
                        Display_Songs();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        //continue asking for permissions if not granted.
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    //Finding all songs using MediaStore which has all medias in both External and Internal storage.
    public ArrayList<song> Find_Songs() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
        };
        /*
        Cursors are what contain the result set of a query made against a database in Android.
        here by using String selection we choose every musics in MediaStore,
        then in projection we choose the columns of every music we want,
        and in while loop by using class "song" we save the information.
         */
        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        while(cursor.moveToNext()){
            song song = new song();
            song.setId(cursor.getColumnName(0));
            song.setName(cursor.getString(1));
            song.setData(cursor.getString(2));
            mSongsList.add(song);
        }
        return mSongsList;
    }
    //converting title of the song to object of the song
    public song title_to_song(String title, ArrayList<song> songs){
        for (int i = 0; i < songs.size(); i++) {
            if(songs.get(i).getName().equals(title)){
                return songs.get(i);
            }
        }
        return null;
    }
    //displaying songs using Adaptor
    void Display_Songs(){
        items = new String[my_songs.size()];//contain songs display names
        for(int i = 0; i < my_songs.size(); i++){
            items[i] = my_songs.get(i).getName();
        }
        //creating a list of all songs using adaptor
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);
        //if click on each item Player class execute
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), Player.class)
                .putExtra("songname", songname)
                .putExtra("pos", i));
                //sending songname and position to Player
            }
        });
    }
    // The Adapter provides access to the data items.
    // The Adapter is also responsible for making a view for each item in the song list.
    class customAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return items.length;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //reading list_item.xml and creating the view for each song
            my_view = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView songtext = my_view.findViewById(R.id.songname);
            songtext.setSelected(true);
            songtext.setText(items[i]);
            //creating checkbox for deleting multiple items
            checkBox = my_view.findViewById(R.id.checkbox);
            if(is_menu_active)//checking if contextual menu active;
                checkBox.setVisibility(View.VISIBLE);
            else
                checkBox.setVisibility(View.GONE);
            checkBox.setTag(i);//saving position of checked checkbox
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int position = (int) compoundButton.getTag();
                    song select = title_to_song(items[position], my_songs);//finding selected song
                    for(song song : selected){
                        if(select==song){
                            flag = false;//using flag to check if song selected already or not
                        }
                    }
                    if(flag) {
                        selected.add(select);
                        actionmode.setTitle(selected.size() + " items selected.");
                        //changing title of menu
                    }else{
                        selected.remove(select);
                        actionmode.setTitle(selected.size() + " items selected.");
                    }
                    flag = true;
                }
            });

            return my_view;//returning view of each item in song list
        }
    }
}
//a class to save each song information
class song{
    String Id, Data, Name;
    public String getData() {
        return Data;
    }
    public String getName() {
        return Name;
    }
    public String getId() {
        return Id;
    }
    public void setData(String data) {
        Data = data;
    }
    public void setName(String display_name) {
        Name = display_name;
    }
    public void setId(String id) {
        Id = id;
    }
}