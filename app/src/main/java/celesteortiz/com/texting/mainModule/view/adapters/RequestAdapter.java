package celesteortiz.com.texting.mainModule.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.pojo.UserPojo;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 19/03/2019
 *
 * RequestAdapter class
 * Adaptador para las Solicitudes
 * */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<UserPojo> mUsers;
    private Context mContext;
    private OnItemClickListener mListener;

    //Constructor
    public RequestAdapter(List<UserPojo> mUsers, OnItemClickListener mListener) {
        this.mUsers = mUsers;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("DEBUG", " RequestAdapter: onCreateViewHolder() Inflando layout de Solicitudes...");
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_request, viewGroup, false);
        mContext = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("DEBUG", " RequestAdapter: onBindViewHolder() Creando item de Solicitud...");
        UserPojo user = mUsers.get(position);
        String photoUrl= user.getPhotoUrl();

        holder.setOnClickListener(user, mListener);
        holder.tvName.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(options)
                .into(holder.imgPhotoRequest);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /*
     * Metodos para agregar, actualizar y remover Solicitudes de la List
     * */
    public void add(UserPojo user){
        Log.d("DEBUG", " RequestAdapter: Agregar usuario a List");
        if(!mUsers.contains(user)){
            mUsers.add(user);
            notifyItemInserted(mUsers.size() -1);
        }else {
            update(user);
        }
    }

    public void update(UserPojo user) {
        Log.d("DEBUG", " RequestAdapter: Actualizar usuario en List");
        if(mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.set(index, user);
            notifyItemChanged(index);
        }
    }

    public void remove(UserPojo user){
        Log.d("DEBUG", " RequestAdapter: Remover usuario de List");
        if(mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.remove(index);
            notifyItemRemoved(index);
        }
    }

    //Clase ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgPhotoRequest)
        CircleImageView imgPhotoRequest;
        @BindView(R.id.btnRejectRequest)
        AppCompatImageButton btnRejectRequest;
        @BindView(R.id.btnAcceptRequest)
        AppCompatImageButton btnAcceptRequest;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvEmail)
        TextView tvEmail;

        //Constructor ViewHolder
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setOnClickListener(final UserPojo user, final OnItemClickListener listener){

            btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG", " RequestAdapter: Click en Aceptar Solicitud...");
                    listener.onAcceptRequest(user);
                }
            });

            btnRejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG", " RequestAdapter: Click en Rechazar Solicitud...");
                    listener.onDenyRequest(user);
                }
            });

        }
    }
}
