package eu.alefzero.owncloud.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ContactSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static AbstractOwnCloudSyncAdapter concretSyncAdapter = null;

    /*
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (concretSyncAdapter == null) {
                concretSyncAdapter = new ContactSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return concretSyncAdapter.getSyncAdapterBinder();
    }

}