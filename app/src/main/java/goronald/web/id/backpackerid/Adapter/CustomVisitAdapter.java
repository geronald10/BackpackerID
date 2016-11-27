package goronald.web.id.backpackerid.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.squareup.picasso.Picasso;

import java.util.List;

import goronald.web.id.backpackerid.Object.VisitObject;
import goronald.web.id.backpackerid.R;

/**
 * Created by Zachary on 11/24/2016.
 */

public class CustomVisitAdapter extends RecyclerView.Adapter<CustomVisitAdapter.ViewHolder> {

    private final List<VisitObject> mObjs;
    private Context mCtxt;

    public CustomVisitAdapter(Context mContext, List<VisitObject> mObjects){
        mCtxt = mContext;
        mObjs = mObjects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit_list_content,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.imageObject

        holder.nameObject.setText(mObjs.get(position).getObjName());
        Log.d("adapter",mObjs.get(position).getObjName());
//        Picasso.with(mContext)
//                .load(mObjs.get(position).getObjPhoto())
//                .resize(125,90)
//                .centerCrop()
//                .into(holder.imageObject);
        Glide.with(mCtxt)
                .load(mObjs.get(position).getObjPhoto())
                .centerCrop()
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_error)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageObject);
        holder.budgetObject.setText(mObjs.get(position).getObjPrice());

    }

    @Override
    public int getItemCount() {
        return mObjs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        private ImageView imageObject;
        private TextView budgetObject;
        private TextView nameObject;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageObject = (ImageView) itemView.findViewById(R.id.ivPlace);
            budgetObject = (TextView) itemView.findViewById(R.id.tvPrice);
            nameObject = (TextView)itemView.findViewById(R.id.tvPlaceName);

        }
    }
}
