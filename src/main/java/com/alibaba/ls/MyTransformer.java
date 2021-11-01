package com.alibaba.ls;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author LvSheng
 * @date 2021/10/30
 **/
public class MyTransformer implements ClassFileTransformer {

    public static final String MYAGENT_BASE_PACKAGE = "myagent.base.package";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String basePackage = getBasePackage();
        if (!className.startsWith(basePackage)) {
            return classfileBuffer;
        }
        System.out.println("正在加载类：" + className);
        CtClass cl = null;
        try {
            ClassPool classPool = ClassPool.getDefault();
            cl = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod[] ctMethods = cl.getMethods();
            for (CtMethod ctMethod : ctMethods) {
                // native,abstract方法没有方法体，会抛异常
                if ((ctMethod.getModifiers() & Modifier.NATIVE) != 0
                    || Modifier.isAbstract(ctMethod.getModifiers())
                    || Modifier.isInterface(ctMethod.getModifiers())
                    || ctMethod.getLongName().contains("main")) {
                    continue;
                }

//                System.out.println("获取方法名称：" + ctMethod.getLongName());

                /** 直接这样生命变量，编译不过
                 * long myAgentCurrentTimeMillis = System.currentTimeMillis();
                 */
                ctMethod.addLocalVariable("myAgentCurrentTimeMillis", CtClass.longType);
                ctMethod.insertBefore("{" +
                    "myAgentCurrentTimeMillis = System.currentTimeMillis();" +
                    "System.out.println(\"params : \" + $args);" +
                    "}");
                String methodLongName = ctMethod.getLongName();

                ctMethod.insertAfter("{" +
                    "System.out.println(\"return value : \" + $_);" +
                    "System.out.println(\" " + methodLongName
                    + " time cost: \" + (System.currentTimeMillis() - myAgentCurrentTimeMillis) + \"ms\");"
                    + "}");
            }
            return cl.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }

    private String getBasePackage() {
        String basePackage = System.getProperty(MYAGENT_BASE_PACKAGE);
        if (basePackage == null) {
            throw new IllegalArgumentException("请指定系统属性参数:" + MYAGENT_BASE_PACKAGE);
        }
        basePackage = basePackage.replace('.', '/');
        return basePackage;
    }
}
