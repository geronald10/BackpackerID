package goronald.web.id.backpackerid;

import android.app.Application;

import com.eyro.mesosfer.Mesosfer;

public class MesosferApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mesosfer.initialize(this, "V3B6udvrwj", "ItzvGjzbQGq0TOx0VgNshLlLcve4Wa09");
    }
}
