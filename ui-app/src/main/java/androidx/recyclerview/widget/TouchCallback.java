package androidx.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.xwray.groupie.GroupieViewHolder;

public abstract class TouchCallback extends ItemTouchHelper.SimpleCallback {

    public TouchCallback() {
        super(0, 0);
    }

    @Override public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return ((GroupieViewHolder) viewHolder).getSwipeDirs();
    }

    @Override public int getDragDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return ((GroupieViewHolder) viewHolder).getDragDirs();
    }

    @Override
    public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {
        // nothing to do
    }
}
