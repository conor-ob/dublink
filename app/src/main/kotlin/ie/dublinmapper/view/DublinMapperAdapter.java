package ie.dublinmapper.view;

import androidx.annotation.NonNull;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DublinMapperAdapter<VH extends ViewHolder> extends GroupAdapter<VH> {

    private final List<Group> groups = new ArrayList<>();

    @Override
    public void update(@NonNull Collection<? extends Group> newGroups) {
        super.update(newGroups);
        groups.clear();
        groups.addAll(newGroups);
    }

    public List<Group> getGroups() {
        return groups;
    }

}
