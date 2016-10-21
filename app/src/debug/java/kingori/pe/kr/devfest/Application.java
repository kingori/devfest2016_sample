package kingori.pe.kr.devfest;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import kingori.pe.kr.devfest.stetho.ShowToastPlugin;
import kingori.pe.kr.devfest.stetho.StethoSocketIOLogger;
import okhttp3.OkHttpClient;

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(new Stetho.Initializer(Application.this) {
            @Nullable
            @Override
            protected Iterable<DumperPlugin> getDumperPlugins() {
                List<DumperPlugin> plugins = new ArrayList<>();
                DumperPluginsProvider defaultProvider = Stetho.defaultDumperPluginsProvider(Application.this);
                for (DumperPlugin defaultPlugin : defaultProvider.get()) {
                    plugins.add(defaultPlugin);
                }
                plugins.add(new ShowToastPlugin());
                return plugins;
            }

            @Nullable
            @Override
            protected Iterable<ChromeDevtoolsDomain> getInspectorModules() {
                return Stetho.defaultInspectorModulesProvider(Application.this).get();
            }
        });

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }

    @Override
    protected OkHttpClient.Builder createOkHtpClientBuilder() {
        return super.createOkHtpClientBuilder()
                .addNetworkInterceptor(new StethoInterceptor());
    }

    @Override
    protected SocketIOLogger createSocketIOLogger() {
        return new StethoSocketIOLogger();
    }
}
