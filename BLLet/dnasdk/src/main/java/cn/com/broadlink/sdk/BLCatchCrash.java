package cn.com.broadlink.sdk;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhujunjie on 2017/7/11.
 */

public class BLCatchCrash implements Thread.UncaughtExceptionHandler {
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler实例
    private static BLCatchCrash INSTANCE = new BLCatchCrash();

    // 程序的Context对象
    private Context mContext;

    /** 保证只有一个CrashHandler实例 */
    private BLCatchCrash() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static BLCatchCrash getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                BLCommonTools.debug("error : " + e);
            }

            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 保存日志
        saveCrashInfo(ex);
        return true;
    }

    private void saveCrashInfo(Throwable ex) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        ex.printStackTrace(mPrintWriter);
        mPrintWriter.close();

        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String exceptionName = stackTraceElements[0].getMethodName();
        String exceptionReason = stackTraceElements[0].getClassName();

        BLCommonTools.debug(mStringWriter.toString());

        Map<String, Object> infos = new HashMap<String, Object>();
        infos.put("exceptionStack", mStringWriter.toString());
        infos.put("exceptionName", exceptionName);
        infos.put("exceptionReason", exceptionReason);

        BLCommonTools.debug("Save crash info " + infos.toString());
        BLLet.Picker.onEvent(BLConstants.Picker.CRASH_Event_ID, BLConstants.Picker.CRASH_Event_LABEL, infos);
        BLLet.finish();
    }
}
