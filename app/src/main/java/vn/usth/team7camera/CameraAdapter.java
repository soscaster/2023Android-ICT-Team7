package vn.usth.team7camera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {
    private List<String> cameraNames;
    private List<String> cameraLinks;
    private OnCameraItemClickListener listener;
    private FragmentManager fragmentManager;

    public CameraAdapter(List<String> cameraNames,List<String> cameraLinks, FragmentManager fragmentManager) {
        this.cameraNames = cameraNames;
        this.cameraLinks = cameraLinks;
        this.fragmentManager = fragmentManager;
    }

    public interface OnCameraItemClickListener {
        void onCameraItemClick(String cameraName, String cameraLink);
    }

    public void setOnCameraItemClickListener(OnCameraItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_items2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cameraName = cameraNames.get(position);
        holder.textCameraName.setText(cameraName);
        holder.underlineImageView.setVisibility(View.VISIBLE);

        // Set an item click listener for the text view
        holder.textCameraName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        String cameraName = cameraNames.get(adapterPosition);
                        String cameraLink = cameraLinks.get(adapterPosition);
                        listener.onCameraItemClick(cameraName, cameraLink);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cameraNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCameraName;
        ImageView underlineImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textCameraName = itemView.findViewById(R.id.textCameraName);
            underlineImageView = itemView.findViewById(R.id.underlineImageView);
        }
    }
}
