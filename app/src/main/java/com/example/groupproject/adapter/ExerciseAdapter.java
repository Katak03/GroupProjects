package com.example.groupproject.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.model.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<Exercise> mListData;
    private Context mContext;
    private int currentPos = -1; // Keeps track of the selected item for the menu

    // 1. MISSING CONSTRUCTOR FIXED
    public ExerciseAdapter(Context context, List<Exercise> listData) {
        this.mListData = listData;
        this.mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    // 2. MISSING METHOD FIXED: Inflates the layout (exercise_list_item.xml)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Ensure "exercise_list_item" matches your XML file name
        View view = inflater.inflate(R.layout.exercise_list_item, parent, false);
        return new ViewHolder(view);
    }

    // 3. Binding data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = mListData.get(position);

        holder.tvName.setText(exercise.getExerciseName());
        holder.tvCategory.setText(exercise.getCategory());
        // Handling integer conversion for setText
        holder.tvCalories.setText(String.valueOf(exercise.getCalories()) + " cal");

        // Force the long click listener to update position
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currentPos = holder.getAdapterPosition();
                return false; // Return false so the context menu can still show
            }
        });
    }

    // 4. MISSING METHOD FIXED: Tells the list how many items to show
    @Override
    public int getItemCount() {
        if (mListData == null) return 0;
        return mListData.size();
    }

    // Helper to get the item selected via Long Press
    public Exercise getSelectedItem() {
        if (currentPos >= 0 && mListData != null && currentPos < mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }



    // 5. VIEW HOLDER
    // Implements OnCreateContextMenuListener so the menu actually appears
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView tvName;
        public TextView tvCategory;
        public TextView tvCalories;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCalories = itemView.findViewById(R.id.tvCalories);

            // Register this view for the context menu
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // This updates the position when the menu is created
            currentPos = getAdapterPosition();

            // Create the menu options
            menu.setHeaderTitle("Select Action");

            // param1: groupId (position), param2: itemId (menu_details), param3: order, param4: title

            // Future options like "Update" or "Delete" would go here
        }
    }
}