package celesteortiz.com.texting.chatModule.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.pojo.Message;
import celesteortiz.com.texting.common.pojo.UserPojo;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private Context mContext;
    private List<Message> messages;
    private OnItemClickListener mListener;

    private int lastPhoto = 0;

    public ChatAdapter(List<Message> messages, OnItemClickListener listener) {
        this.messages = messages;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Message message = messages.get(position);
        Log.d("DEBUG CHAT", "ChatAdapter: Configurando vista para los mensajes");
        //Establecer los margenes maximos y minimos tanto en lo alto como en lo ancho
        // Para diferenciar nuestros mensajes de los de nuestro amigo y que separacin habra
        // entre cada mensaje consecutivo
        final int maxMarginHorizontal = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_horizontal);
        final int maxMarginTop = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_top);
        final int minMargin = mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_min);

        //Inicializar propiedades por default hacia la derecha, que seran NUESTROS MENSAJES
        int gravity = Gravity.END;
        Drawable bgMessage = ContextCompat.getDrawable(mContext, R.drawable.background_chat_me);
        int marginStart = maxMarginHorizontal;
        int marginTop = minMargin;
        int marginEnd = minMargin;

        //Preguntar si el mensaje es o no nuestro, para corregir la orientacion en caso de ser necesario
        if(!message.isSentByMe()){
            gravity = Gravity.START;
            bgMessage = ContextCompat.getDrawable(mContext, R.drawable.background_chat_friend);
            marginEnd = maxMarginHorizontal;
            marginStart = minMargin;
        }

        //comprobar si no son nuestros mensajes consecutivos o si hay alguna interrupcion
        if(position > 0 &&
                message.isSentByMe() != messages.get(position-1).isSentByMe()){
            marginTop = maxMarginTop;

        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.tvMessage.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(marginStart, marginTop, marginEnd, minMargin);

        //Comprobar si es un mensaje o un texto
        if(message.getPhotoUrl() != null){
            Log.d("DEBUG CHAT", "Interactor: Se ha enviado una foto, configurando vista");
            holder.tvMessage.setVisibility(View.GONE);
            holder.imgPhoto.setVisibility(View.VISIBLE);

            //recolectar la ultima posicion de la imagen para avisarle al contexto que ya tenemos
            //cargada la imagen y podemos recorrer el scroll para que se empuje el listado de mensajes
            if(position > lastPhoto){
                lastPhoto = position;
            }

            //Cargar la imagen con Glide
            final int size = mContext.getResources().getDimensionPixelSize(R.dimen.chat_size_image);
            params.width = size;
            params.height = size;
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_timer_sand_160)
                    .error(R.drawable.ic_emoticon_sad)
                    .centerCrop();

            Glide.with(mContext)
                    .asBitmap()
                    .load(message.getPhotoUrl())
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            int dimension = size - mContext.getResources().getDimensionPixelSize(R.dimen.chat_padding_image);
                            Bitmap bitmap = ThumbnailUtils.extractThumbnail(resource, dimension, dimension);
                            holder.imgPhoto.setImageBitmap(bitmap);

                            if(!message.isLoaded()){
                                message.setLoaded(true);
                                if(position == lastPhoto){
                                    mListener.onImageLoaded();
                                }
                            }
                            return true;
                        }
                    })
                    .into(holder.imgPhoto);
            holder.imgPhoto.setBackground(bgMessage);
            holder.setClickListener(message, mListener);
        }else {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.imgPhoto.setVisibility(View.GONE);

            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            holder.tvMessage.setBackground(bgMessage);
            holder.tvMessage.setText(message.getMsg());

        }

        holder.imgPhoto.setLayoutParams(params);
        holder.tvMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void add(Message message){
       Log.d("DEBUG CHAT ", "Chat Adapter : Agregando mensaje....");
        if(!messages.contains(message)){
            messages.add(message);
            notifyItemInserted(messages.size() -1);
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvMessage)
        TextView tvMessage;
        @BindView(R.id.imgPhoto)
        AppCompatImageView imgPhoto;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setClickListener(final Message message, final OnItemClickListener listener){
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickImage(message);
                }
            });
        }
    }
}
