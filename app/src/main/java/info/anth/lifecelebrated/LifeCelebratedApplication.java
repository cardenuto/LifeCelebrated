package info.anth.lifecelebrated;

import com.firebase.client.Firebase;

/**
 * Created by Primary on 5/31/2016.
 */
public class LifeCelebratedApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
