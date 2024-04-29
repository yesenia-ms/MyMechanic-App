package cs3773.group11.mymechanic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


//adapts list to recycler view format
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {


    private List<CommentItem> commentItemList;
    Context context;

    public CommentsAdapter(Context context, List<CommentItem> commentItemList) {
        this.commentItemList = commentItemList;
        this.context = context;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, roleTextView, commentTextView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.comment_name);
            roleTextView = itemView.findViewById(R.id.comment_role);
            commentTextView = itemView.findViewById(R.id.comment_text);

        }
    }









    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CommentItem commentItem = commentItemList.get(position);
        holder.nameTextView.setText(commentItem.getName());
        holder.roleTextView.setText(commentItem.getRole());
        holder.commentTextView.setText(commentItem.getComment());
    }



    @Override
    public int getItemCount() {
        return commentItemList.size();
    }
}
