package cm.deone.corp.tontines.interfaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ImageView;

public interface IntMedia {
    /**
     * Recuperation d'une image à partir de la base de données firebase
     * @param imageView La vue sur laquelle sera affiché l'image
     * @param path Le chemin de recupération de l'image
     */
    public void readPicture(ImageView imageView, String path);

    /**
     * Sauvegarde d'une image dans la base de donnée storage de firebase
     * @param activity L'activité qui initie la sauvegarde de l'image
     * @param progressDialog La vue désigne à l'utilisateur que le traitement est en cours d'exécution
     * @param imageView La vue qui détient l'image à sauvegarder
     * @param type Indique si l'image est un cover ou un avatar
     */
    public void savePicture(final Activity activity, final ProgressDialog progressDialog, ImageView imageView, String type);
}
