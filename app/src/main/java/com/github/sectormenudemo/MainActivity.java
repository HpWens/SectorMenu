package com.github.sectormenudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.sectormenudemo.widget.SectorLayout;

public class MainActivity extends AppCompatActivity {

    private SectorLayout mSectorLayout;

    private int[] mImages = new int[]{
            R.drawable.ic_action_sms,
            R.drawable.ic_action_phone,
            R.drawable.ic_action_camera,
            R.drawable.ic_action_contacts,
            R.drawable.ic_action_music,
            R.drawable.ic_action_name,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectorLayout = (SectorLayout) findViewById(R.id.sector);

        mSectorLayout.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mImages.length;
            }

            @Override
            public Object getItem(int position) {
                return mImages[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                holder holder = null;
                if (convertView == null) {
                    holder = new holder();
                    convertView = View.inflate(MainActivity.this, R.layout.activity_item, null);
                    holder.mImage = (ImageView) convertView.findViewById(R.id.iv);
                    convertView.setTag(holder);
                } else {
                    holder = (holder) convertView.getTag();
                }
                holder.mImage.setBackgroundResource(mImages[position]);
                if (position != (getCount() - 1)) {
                    holder.mImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return convertView;
            }
        });
    }

    public class holder {
        ImageView mImage;
    }
}
