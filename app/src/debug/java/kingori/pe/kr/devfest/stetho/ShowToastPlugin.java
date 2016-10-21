package kingori.pe.kr.devfest.stetho;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;

import kingori.pe.kr.devfest.Application;

public class ShowToastPlugin implements DumperPlugin {
    @Override
    public String getName() {
        return "toast";
    }

    @Override
    public void dump(DumperContext dumperContext) throws DumpException {
        final String message = dumperContext.getArgsAsList().get(0);
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Application.getInstance(), message, Toast.LENGTH_SHORT).show();
                    }
                });

        dumperContext.getStdout().println(message+" is shown!");
    }
}
