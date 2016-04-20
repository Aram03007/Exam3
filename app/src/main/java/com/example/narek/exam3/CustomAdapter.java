package com.example.narek.exam3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

/**
 * Created by Narek on 4/20/16.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String> imagePaths;
    private ViewListener viewListener;



    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        AsyncTask<Integer, String, Bitmap> asyncTask = new AsyncTask<Integer, String, Bitmap>() {



            public  Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight) {

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                options.inSampleSize = DrawView.calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(path, options);
            }

            @Override
            protected void onPreExecute() {
                viewHolder.items++;
                viewHolder.myAsyncTask = this;
                viewHolder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Bitmap doInBackground(Integer... params) {
                if (isCancelled()) return null;
                return decodeSampledBitmap(imagePaths.get(params[0]), 100, 100);
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                viewHolder.items--;
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.icon.setImageBitmap(bmp);
            }
        };

        asyncTask.execute(i);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }


    public CustomAdapter(List<String> imagePaths, ViewListener viewListener) {
        this.viewListener = viewListener;
        this.imagePaths = imagePaths;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private ProgressBar progressBar;
        private int items = 0;
        AsyncTask<Integer, String, Bitmap> myAsyncTask;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.recyclerViewItemIcon);
            progressBar = (ProgressBar) itemView.findViewById(R.id.myProgressBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

}
