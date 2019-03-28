package celesteortiz.com.texting.mainModule.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
 * UserAdapter class
 * Adaptador para los ContactosFragment del usuario logueado
 * */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserPojo> mUsers;
    private Context mContext;
    private OnItemClickListener mListener;

    public UserAdapter(List<UserPojo> mUsers, OnItemClickListener mListener) {
        this.mUsers = mUsers;
        this.mListener = mListener;
    }

    /*
    * This method calls onCreateViewHolder(ViewGroup, int) to create a new RecyclerView.ViewHolder
    * and initializes some private fields to be used by RecyclerView.
    * */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("DEBUG", " UserAdapter: onCreateViewHolder() ...");
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_user, viewGroup, false);
        mContext = viewGroup.getContext();

        return new ViewHolder(view);
    }

    /*
    * This method internally calls onBindViewHolder(ViewHolder, int) to update the
    * RecyclerView.ViewHolder contents with the item at the given position and also sets up some
    * private fields to be used by RecyclerView.
    * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("DEBUG", " UserAdapter: onBindViewHolder() Creando item de Contacto...");
        UserPojo user = mUsers.get(position);

        holder.setOnClickListener(user, mListener);
        holder.tvName.setText(user.getUsernameValid());

        //Mostrar mensajes sin leer
        int messageUnread = user.getMessagesUnread();

        if(messageUnread > 0){
            String countStr = messageUnread > 99 ?
                    mContext.getString(R.string.main_item_max_mssg_unread) : String.valueOf(messageUnread);
            holder.tvCountUnread.setText(countStr);
            holder.tvCountUnread.setVisibility(View.VISIBLE);
        }else{
            holder.tvCountUnread.setVisibility(View.GONE);
        }

        //Cargar imagen de contacto
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(options)
                .into(holder.imgPhotoContact);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /*
    * Metodos para agregar, actualizar y remover Usuarios de la List
    * */
    public void add(UserPojo user){
        Log.d("DEBUG", " UserAdapter: Agregar usuario a List");
        if(!mUsers.contains(user)){
            mUsers.add(user);
            notifyItemInserted(mUsers.size() -1);
        }else {
            update(user);
        }
    }

    public void update(UserPojo user) {
        Log.d("DEBUG", " UserAdapter: Actualizar usuario en List");
        if(mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.set(index, user);
            notifyItemChanged(index);
        }
    }

    public void remove(UserPojo user){
        Log.d("DEBUG", " UserAdapter: Remover usuario de List");
        if(mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.remove(index);
            notifyItemRemoved(index);
        }
    }

    //Clase ViewHolder : describes an item view and metadata about its place within the RecyclerView.
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgPhotoContact)
        CircleImageView imgPhotoContact;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvCountUnread)
        TextView tvCountUnread;

        private View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        void setOnClickListener(final UserPojo user, final OnItemClickListener listener){
            Log.d("DEBUG", " UserAdapter: setOnClickListener() Click en item de ContactosFragment...");

            //Entrar a un chat
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });

            //Eliminar un contacto
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(user);
                    return true;
                }
            });

        }
    }
}
