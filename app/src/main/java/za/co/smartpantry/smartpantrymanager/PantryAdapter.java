package za.co.smartpantry.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
    private ArrayList<PantryItem> pantryItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
        void onItemLongClick(PantryItem item);
    }

    public PantryAdapter (
            ArrayList<PantryItem> pantryItems,
            OnItemClickListener listener
    ) {
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int ViewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position
    ) {
        PantryItem item = pantryItems.get(position);
        holder.textIngredientName.setText(item.getName());
        holder.textQuantity.setText(
                item.getQuantity() + " " + item.getUnit()
        );

        String expiryDate = item.getExpiryDate();

        if (expiryDate == null || expiryDate.isEmpty()) {
            holder.textExpiryDate.setText("No Expiry Date");
        } else {
            holder.textExpiryDate.setText(
                    "Expiry: " + expiryDate
            );
        }

        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(item)
        );

        holder.itemView.setOnLongClickListener(v-> {
            listener.onItemLongClick(item);
            return true;
        });
    }
    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public void updateData(ArrayList<PantryItem> newItems) {
        this.pantryItems = newItems;
        notifyDataSetChanged();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textIngredientName;
        TextView textQuantity;
        TextView textExpiryDate;

        public PantryViewHolder(@NonNull View itemView) {
            super (itemView);

            textIngredientName = itemView.findViewById(R.id.textIngredientName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiryDate = itemView.findViewById(R.id.textExpiryDate);
        }
    }
}
