package vn.usth.team7camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import vn.usth.team7camera.R;

public class SnapshotsFragment extends Fragment {
    private ArrayList<String> imagePaths = new ArrayList<>();
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snapshots, container, false);
        gridView = view.findViewById(R.id.gridView);

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures/Team7Camera");
        // Check if directory exists and is not empty
        if (storageDir.exists() && storageDir.listFiles() != null && storageDir.listFiles().length > 0) {
            File[] files = storageDir.listFiles();

            for (File file : files) {
                if (file.isFile()) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }

            ImageAdapter adapter = new ImageAdapter(getActivity(), imagePaths);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String path = imagePaths.get(position);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri fileUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
                    intent.setDataAndType(fileUri, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    public class ImageAdapter extends ArrayAdapter<String> {
        private Context context;
        private ArrayList<String> imagePaths;

        public ImageAdapter(Context context, ArrayList<String> imagePaths) {
            super(context, 0, imagePaths);
            this.context = context;
            this.imagePaths = imagePaths;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
            }
            ImageView imageView = convertView.findViewById(R.id.imageView);
            Picasso.get().load("file://" + imagePaths.get(position)).into(imageView);
            return convertView;
        }
    }
}
