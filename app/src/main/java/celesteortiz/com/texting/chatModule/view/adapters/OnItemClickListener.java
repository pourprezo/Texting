package celesteortiz.com.texting.chatModule.view.adapters;

import celesteortiz.com.texting.common.pojo.Message;

public interface OnItemClickListener {
    //En caso de cargar imagen e impulsar el scroll
    void onImageLoaded();
    //En caso de que se haga click sobre la imagen se lanzara una vista para hacer zoom
    void onClickImage(Message message);
}
