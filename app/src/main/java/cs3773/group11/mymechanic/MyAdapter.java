package cs3773.group11.mymechanic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


//test

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<SolutionItem> solutionItemList;

    public MyAdapter(List<SolutionItem> solutionItemList) {
        this.solutionItemList = solutionItemList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, solutionTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            descriptionTextView = itemView.findViewById(R.id.text_view_description);
            solutionTextView = itemView.findViewById(R.id.text_view_solution);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SolutionItem solutionItem = solutionItemList.get(position);
        holder.titleTextView.setText(solutionItem.getSolutionTitle());
        holder.descriptionTextView.setText(solutionItem.getSolutionDescription());
        holder.solutionTextView.setText(solutionItem.getSolution());
    }

    @Override
    public int getItemCount() {
        return solutionItemList.size();
    }

}
