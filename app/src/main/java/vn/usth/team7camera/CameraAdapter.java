package vn.usth.team7camera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {
    private List<String> cameraNames;
    private OnCameraItemClickListener listener;

    public CameraAdapter(List<String> cameraNames) {
        this.cameraNames = cameraNames;
    }

    public interface OnCameraItemClickListener {
        void onCameraItemClick(String cameraName);
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
