package com.alvkeke.tools.todo.Noticication;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.alvkeke.tools.todo.MainFeatures.Functions;
import com.alvkeke.tools.todo.R;

import java.io.File;

public class ReminderService extends IntentService implements RunnableCallback{

    private File dbfile;

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        new Thread(new CheckRunnable(this)).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String filename = intent.getStringExtra("dbfile");
        dbfile = new File(filename);
        doAction();
    }

    @Override
    public void doAction() {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "reminder");

        if(dbfile == null) {
            return;
        }

        int count = 0;

        StringBuilder sb = new StringBuilder();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        String sql = "select id, content, time, isFinished from tasks";
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()){
            String taskContent = c.getString(1);
            long time = c.getLong(2);
            if(Functions.isToday(time)){
                sb.append(++count);
                sb.append(":");
                sb.append(taskContent);
                sb.append("\n\n");
            }
        }

        c.close();
        db.close();

        if(count == 0) return;

        String title = "今日任务数量: " + count;
        String smalltext = "长按查看详情";

        builder.setContentTitle(title)
                .setContentText(smalltext)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        bigTextStyle.setBigContentTitle(title)
                .bigText(sb.toString());

        builder.setStyle(bigTextStyle);

        manager.notify(1, builder.build());

    }
}
