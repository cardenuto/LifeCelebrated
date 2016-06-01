package info.anth.lifecelebrated.AddLocationSteps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import info.anth.lifecelebrated.Data.DbLocationNames;
import info.anth.lifecelebrated.R;

/**
 * Created by Primary on 6/1/2016.
 *
 *
 */
public class NamesRecyclerAdapter extends RecyclerView.Adapter<NamesRecyclerAdapter.ItemHolder> {

    //private List<String> itemsName;
    private List<DbLocationNames> itemsName;
    //private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public NamesRecyclerAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
        itemsName = new ArrayList<DbLocationNames>();
    }

    @Override
    public NamesRecyclerAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.x_step_names_list, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(NamesRecyclerAdapter.ItemHolder holder, int position) {
        holder.setItems(itemsName.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsName.size();
    }

    /*
    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(ItemHolder item, int position);
    }
*/
    public void add(int location, DbLocationNames iName){
        itemsName.add(location, iName);
        notifyItemInserted(location);
    }

    public void remove(int location){
        if(location >= itemsName.size())
            return;

        itemsName.remove(location);
        notifyItemRemoved(location);
    }
/*
    public static class ItemHolderOld extends RecyclerView.ViewHolder implements View.OnClickListener{

        private NamesRecyclerAdapter parent;
        EditText textItemName;

        public ItemHolder(View itemView, NamesRecyclerAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.parent = parent;
            textItemName = (EditText) itemView.findViewById(R.id.entered_text_family);
        }

        public void setItemName(CharSequence name){
            textItemName.setText(name);
        }

        public CharSequence getItemName(){
            return textItemName.getText();
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = parent.getOnItemClickListener();
            if(listener != null){
                listener.onItemClick(this, getPosition());
            }
        }
    }

*/
    public static class ItemHolder extends RecyclerView.ViewHolder {

        private NamesRecyclerAdapter parent;
        //View mView;
        EditText editFamilyName;
        EditText editFirstName;
        public DbLocationNames mItem;
        //public String mNameKey;

        public ItemHolder(View itemView, NamesRecyclerAdapter parent) {
            super(itemView);
            this.parent = parent;
            //mView = itemView;
            editFamilyName = (EditText) itemView.findViewById(R.id.entered_text_family);
            editFirstName = (EditText) itemView.findViewById(R.id.entered_text_first);
        }

        public void setItems(DbLocationNames dbLocationNames){
            editFamilyName.setText(dbLocationNames.getFamilyName());
            editFirstName.setText(dbLocationNames.getFirstName());
        }

    }
}

