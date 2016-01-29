package material.sahitya.assignmentwork;

import java.io.Serializable;

public class UserDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _userName, _userId, _userKey;



	public UserDetails() {}

    public UserDetails(String userName, String userId, String userKey) {
        this._userName = userName;
        this._userId = userId;
        this._userKey = userKey;
    }


    public void set_userName(String userName) {
        this._userName = userName;
    }

    public String get_userName() {
        return this._userName;
    }

    public void set_userId(String userId) {
        this._userId = userId;
    }

    public String get_userId() {
        return this._userId;
    }

    public void set_userKey(String userKey) {
        this._userKey = userKey;
    }

    public String get_userKey() {
        return this._userKey;
    }


}
