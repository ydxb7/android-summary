package ai.tomorrow.todolist1;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private static final String TAG = "MyAdapter";
    private Cursor mCursor;
    private onDeleteClickListener onDeleteClickListener;

    public static interface onDeleteClickListener{
        void onDeleteClickListener(long id);
    }

    public MyAdapter(Cursor mCursor, onDeleteClickListener onDeleteClickListener) {
        this.mCursor = mCursor;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        if(!mCursor.moveToPosition(position)) return;

        final String message = mCursor.getString(mCursor.getColumnIndex(ToDoListContract.ToDoListEntry.COLUMN_NAME_MESSAGE));
        final long messageID = mCursor.getLong(mCursor.getColumnIndex(ToDoListContract.ToDoListEntry._ID));

        holder.textView.setText(message);
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: delete a message id:" + messageID);
                onDeleteClickListener.onDeleteClickListener(messageID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) mCursor.close();
        mCursor = cursor;
        if(mCursor != null) notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ImageView deleteImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_tv);
            deleteImageView = itemView.findViewById(R.id.delete_iv);
        }
    }

}
