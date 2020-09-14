package cm.deone.corp.tontines.models;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class Membre {
    private String memId;
    private String memName;
    private String memPhone;
    private String memDateAdded;
    private String memStatus;

    public Membre() {
    }

    public Membre(String memId, String memName, String memPhone, String memDateAdded, String memStatus) {
        this.memId = memId;
        this.memName = memName;
        this.memPhone = memPhone;
        this.memDateAdded = memDateAdded;
        this.memStatus = memStatus;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone;
    }

    public String getMemDateAdded() {
        return memDateAdded;
    }

    public void setMemDateAdded(String memDateAdded) {
        this.memDateAdded = memDateAdded;
    }

    public String getMemStatus() {
        return memStatus;
    }

    public void setMemStatus(String memStatus) {
        this.memStatus = memStatus;
    }

    public List<User> allContact(Context context){
        List<User> userList = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String mName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String mPhone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            mPhone = mPhone.replace(" ","");
            mPhone = mPhone.replace("-","");
            mPhone = mPhone.replace("(","");
            mPhone = mPhone.replace(")","");
            userList.add(new User(mName, mPhone));
        }

        return userList;
    }
}
