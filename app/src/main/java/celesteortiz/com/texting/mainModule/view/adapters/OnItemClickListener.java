package celesteortiz.com.texting.mainModule.view.adapters;

import com.firebase.ui.auth.data.model.User;

import celesteortiz.com.texting.common.pojo.UserPojo;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 19/03/2019
 *
 * OnItemClickListener interface
 * Interfaz que estara a la escucha en el caso de que se den
 * cualquiera de estos eventos. Sera el intermediario entre RequestAdapter
 * y la clase que lo llama
 * */
public interface OnItemClickListener {
    void onItemClick(UserPojo user);
    void onItemLongClick(UserPojo user);

    void onAcceptRequest(UserPojo user);
    void onDenyRequest(UserPojo user);

}
