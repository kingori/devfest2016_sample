package kingori.pe.kr.devfest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class MemoryLeakActivity extends BaseActivity {

    private static Activity instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);
        MemoryLeakActivity.instance = this;
    }
}
