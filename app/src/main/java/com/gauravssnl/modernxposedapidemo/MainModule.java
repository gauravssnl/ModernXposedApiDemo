package com.gauravssnl.modernxposedapidemo;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.annotations.AfterInvocation;
import io.github.libxposed.api.annotations.BeforeInvocation;
import io.github.libxposed.api.annotations.XposedHooker;

public class MainModule extends XposedModule {

    private static MainModule mainModule;

    public MainModule(@NonNull XposedInterface base, @NonNull ModuleLoadedParam param) {
        super(base, param);
        log("MainModule at " + param.getProcessName());
        mainModule = this;
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam param) {
        super.onPackageLoaded(param);
        log("onPackageLoaded: " + param.getPackageName());
        log("param classloader is " + param.getClassLoader());
        log("module apk path: " + this.getApplicationInfo().sourceDir);
        log("----------");
        if (param.isFirstPackage()) {
            log("First package ...)");
            try {
                log("Trying to find classes & methods to hook");
                final String className = param.getPackageName() + ".MainActivity";
                log(Arrays.toString(param.getClassLoader().getClass().getDeclaredFields()));
                Class<?> clazz = ClassUtils.getClass(param.getClassLoader(), className, false);
                if (clazz == null) {
                    log("Failed to find the class :: " + className);
                    return;
                }
                log("Found the class  to hook  :: " + clazz);
                final String methodName = "J0";
                Method method = clazz.getDeclaredMethod(methodName);
                log("Found the method to be hooked :: " + method);
                hook(method, MyHooker.class);
                log("hooking completed :)");
            } catch (Exception ex) {
                log("Error in finding class method & hooking :: " + ex);
            }
        } else {
            log("Not the first package...");
            // we can hook other package loaded by the Android app here if we need to do so.
        }
    }

    @XposedHooker
    static public class MyHooker implements XposedInterface.Hooker {
        @BeforeInvocation
        public static MyHooker beforeInvocation(BeforeHookCallback callback) {
            mainModule.log("beforeInvocation values :: " + Arrays.toString(callback.getArgs()));
            return new MyHooker();
        }

        @AfterInvocation
        public static void afterInvocation(AfterHookCallback callback, MyHooker context) {
            mainModule.log("afterInvocation callback args: " + Arrays.toString(callback.getArgs()));
            mainModule.log("afterInvocation val: " + context.toString());
            HashMap<String, Object> map = new HashMap<>();
            map.put("noAd", true);
            callback.setResult(map);
        }
    }

}
