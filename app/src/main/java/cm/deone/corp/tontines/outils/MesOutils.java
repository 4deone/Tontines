package cm.deone.corp.tontines.outils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import cm.deone.corp.tontines.models.User;

public abstract class MesOutils {

    /**
     * Conversion de de date au format firebase en string
     * @param patternDate En chaine de caractère le pattern à afficher
     * @param timestamp En chaine de caractere la date au format firebase
     * @return Le renvoi de la date en chane de caractere
     */
    public static String dateToString(String patternDate, String timestamp){
        SimpleDateFormat formater = new SimpleDateFormat(patternDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        return formater.format(calendar.getTime());
    }

    /**
     * Mise en forme du numéro de téléphone
     * @param phone Le numéro à formater
     * @return La chaine de caractère du numéro formaté
     */
    public static String formatPhone(String phone){
        // Cleanup the phone number
        //phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
        //phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
        return  phone.replaceAll(" ", "");
    }

    /**
     * Récupération de la liste des contact de l'utilisateur
     * @param context L'activité voulant accéder à la liste des contacts de l'utilisateur
     * @return La liste complète des contacts de l'utilisateur
     */
    public static List<User> loadAllContacts(Context context){
        List<User> uContacts = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // Cleanup the phone number
            phoneNumber = formatPhone(phoneNumber);

            // Add in the list
            User uContact = new User(name,phoneNumber);
            uContacts.add(uContact);
        }
        phones.close();
        return uContacts;
    }

    /**
     *
     * @param context L'activité voulant accéder à la liste des contacts de l'utilisateur
     * @param telephone Le téléphone à rechercher
     * @return Le boolean pour determiner si oui ou non le téléphone est bel et bien présent dans la liste des contacts
     */
    public static boolean findContact(Context context, String telephone){
        boolean result = false;
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll(" ", "");
            if (phoneNumber.equals(telephone)) {
                result = true;
                break;
            }
        }
        phones.close();
        return result;
    }

    /**
     * Generation d'un ID unique
     * @param timestamp La date du jour
     * @return ID unique
     */
    public static String keygenerator(String timestamp){
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"+timestamp;
        StringBuilder sB = new StringBuilder();
        Random rD = new Random();
        for (int count = 0; count< 17; count++){
            sB.append(source.charAt(rD.nextInt(source.length())));
        }
        return sB.toString();
    }

    /**
     * Conversion d'une chaine de caractère en tableau
     * @param source
     * @return
     */
    public static char[] stringToTable(String source){
        char[] result = new char[source.length()];
        for (int count = 0; count < source.length(); count++){
            result[count] = source.charAt(count);
        }
        return result;
    }

}
