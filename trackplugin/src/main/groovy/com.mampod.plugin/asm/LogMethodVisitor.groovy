package com.mampod.plugin.asm

import com.mampod.plugin.bean.LogMethodCell
import com.mampod.plugin.LogHookConfig
import com.mampod.plugin.util.LogAnalyticsUtil
import com.mampod.plugin.util.Logger
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 针对日志采集sdk埋点的方法进行修改
 *
 * @package com.mampod.plugin.asm
 * @author: Jack-Lu
 * @date:
 */
public class LogMethodVisitor extends AdviceAdapter {

    public HashSet<String> visitedFragMethods
    String methodName
    int access
    MethodVisitor methodVisitor
    String methodDesc
    String superName
    String className
    String[] interfaces

    public LogMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc,
                            String superName, String className, String[] interfaces, HashSet<String> visitedFragMethods) {
        super(Opcodes.ASM9, methodVisitor, access, name, desc)
        this.methodName = name
        this.access = access
        this.methodVisitor = methodVisitor
        this.methodDesc = desc
        this.superName = superName
        this.className = className
        this.interfaces = interfaces
        this.visitedFragMethods = visitedFragMethods
        if (methodName.equals())
        Logger.info("||开始扫描方法：${Logger.accCode2String(access)} ${methodName}${desc} ${superName}")
    }

    boolean isAutoTrackViewOnClickAnnotation = false
    boolean isAutoTrackIgnoreTrackOnClick = false
    boolean isHasInstrumented = false
    boolean isHasTracked = false
    boolean isAutoTraceSubPage = false
    boolean isPageEnter = false
    boolean isPageExit = false
    boolean isPageResume = false
    boolean isPageStop = false

    @Override
    void visitEnd() {
        super.visitEnd()
        if (isHasTracked) {
            visitAnnotation("Lcom/mampod/track/sdk/annotation/AutoDataInstrumented;", false)
            Logger.info("||Hooked method: ${methodName}${methodDesc}")
        }
        Logger.info("||结束扫描方法：${methodName}")
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()

        if (isAutoTrackIgnoreTrackOnClick) {
            return
        }

        /**
         * 在 android.gradle 的 3.2.1 版本中，针对 view 的 setOnClickListener 方法 的 lambda 表达式做特殊处理。
         */
        if (methodName.trim().startsWith('lambda$') && LogAnalyticsUtil.isPrivate(access) && LogAnalyticsUtil.isSynthetic(access)) {
            Logger.info("lamda: ${methodDesc}")
            LogMethodCell logMethodCell = LogHookConfig.sLambdaMethods.get(methodDesc)
            //特殊处理lamda表达式多个参数时匹配最后一个参数
            if (logMethodCell == null && methodDesc.endsWith("Landroid/view/View;)V")) {
                Logger.info("lamda--------->: ${methodDesc}")
                Type[] argTypes = Type.getArgumentTypes(methodDesc)
                int count = argTypes.length
                if (count > 0) {
                    int paramsStart = count
                    logMethodCell = new LogMethodCell(
                            methodName,
                            methodDesc,
                            'android/view/View$OnClickListener',
                            'trackViewOnClick',
                            '(Landroid/view/View;)V',
                            paramsStart, 1,
                            [Opcodes.ALOAD])

                }
            }


            if (logMethodCell != null) {
                int paramStart = logMethodCell.paramsStart
                //static方法0不再代表this，代表普通参数
                if (LogAnalyticsUtil.isStatic(access)) {
                    paramStart = paramStart - 1
                }
                LogAnalyticsUtil.visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE,
                        logMethodCell.agentName, logMethodCell.agentDesc,
                        paramStart, logMethodCell.paramsCount, logMethodCell.opcodes)
                isHasTracked = true
                return
            }
        }

//        if (methodDesc.contains("onBindViewHolder")) {
//            Logger.info("lamda--------->onbindviewholder: ${methodDesc}")
//        }


        /**
         * Method 描述信息
         */
        String methodNameDesc = methodName + methodDesc

        /**
         * Activity
         */
        if (LogAnalyticsUtil.isInstanceOfActivity(superName)) {
            LogMethodCell logMethodCell = LogHookConfig.sActivityMethods.get(methodNameDesc)
            if (logMethodCell != null) {
                visitedFragMethods.add(methodNameDesc)
                LogAnalyticsUtil.visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, logMethodCell.agentName, logMethodCell.agentDesc, logMethodCell.paramsStart, logMethodCell.paramsCount, logMethodCell.opcodes)
                isHasTracked = true
            }
        }

        if (isPageEnter) {
            if (methodDesc == '(Landroid/os/Bundle;)V') {
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackActivityCreate", "(Landroid/app/Activity;Landroid/os/Bundle;)V", false)
                isHasTracked = true
                return
            }
        }

        if (isPageResume) {
            if (methodDesc == '()V') {
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackActivityResume", "(Landroid/app/Activity;)V", false)
                isHasTracked = true
                return
            }
        }


        if (isPageStop) {
            if (methodDesc == '()V') {
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackActivityStop", "(Landroid/app/Activity;)V", false)
                isHasTracked = true
                return
            }
        }

        if (isPageExit) {
            if (methodDesc == '()V') {
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackActivityDestroy", "(Landroid/app/Activity;)V", false)
                isHasTracked = true
                return
            }
        }

        if (!(LogAnalyticsUtil.isPublic(access) && !LogAnalyticsUtil.isStatic(access))) {
            return
        }


        /**
         * 之前已经添加过埋点代码，忽略
         */
        if (isHasInstrumented) {
            return
        }


        /**
         * Fragment
         * 目前支持 android/support/v4/app/ListFragment 和 android/support/v4/app/Fragment
         */
        if (LogAnalyticsUtil.isInstanceOfFragment(superName)) {
            LogMethodCell logMethodCell = LogHookConfig.sFragmentMethods.get(methodNameDesc)
//            Log.info("fragment:methodNameDesc:" + methodNameDesc)
//            Log.info("fragment:logMethodCell:" + logMethodCell)
            if (logMethodCell != null) {
                visitedFragMethods.add(methodNameDesc)
                LogAnalyticsUtil.visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, logMethodCell.agentName, logMethodCell.agentDesc, logMethodCell.paramsStart, logMethodCell.paramsCount, logMethodCell.opcodes)
                isHasTracked = true
            }
        }

        if (isAutoTraceSubPage) {
            if (methodDesc == '(ZLjava/lang/String;Ljava/lang/String;)V') {
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitVarInsn(ILOAD, 1)
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitVarInsn(ALOAD, 3)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackFragmentShow", "(Ljava/lang/Object;ZLjava/lang/String;Ljava/lang/String;)V", false)
                isHasTracked = true
                return
            }
        }

        /**
         * Menu
         * 目前支持 onContextItemSelected(MenuItem item)、onOptionsItemSelected(MenuItem item)
         */
        if (LogAnalyticsUtil.isTargetMenuMethodDesc(methodNameDesc)) {
            methodVisitor.visitVarInsn(ALOAD, 0) //压入this
            methodVisitor.visitVarInsn(ALOAD, 1) //压入参数
            methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackMenuItem", "(Ljava/lang/Object;Landroid/view/MenuItem;)V", false)
            isHasTracked = true
            return
        }

        if (methodNameDesc == 'onDrawerOpened(Landroid/view/View;)V') {
            methodVisitor.visitVarInsn(ALOAD, 1)
            methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackDrawerOpened", "(Landroid/view/View;)V", false)
            isHasTracked = true
            return
        } else if (methodNameDesc == 'onDrawerClosed(Landroid/view/View;)V') {
            methodVisitor.visitVarInsn(ALOAD, 1)
            methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackDrawerClosed", "(Landroid/view/View;)V", false)
            isHasTracked = true
            return
        }

        if (className == 'android/databinding/generated/callback/OnClickListener') {
            if (methodNameDesc == 'onClick(Landroid/view/View;)V') {
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackViewOnClick", "(Landroid/view/View;)V", false)
                isHasTracked = true
                return
            }
        }

        if (className.startsWith('android') || className.startsWith('androidx')) {
            return
        }

        if (methodNameDesc == 'onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V' || methodNameDesc == "onListItemClick(Landroid/widget/ListView;Landroid/view/View;IJ)V") {
            methodVisitor.visitVarInsn(ALOAD, 1)
            methodVisitor.visitVarInsn(ALOAD, 2)
            methodVisitor.visitVarInsn(ILOAD, 3)
            methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackListView", "(Landroid/widget/AdapterView;Landroid/view/View;I)V", false)
            isHasTracked = true
            return
        }

        if (isAutoTrackViewOnClickAnnotation) {
            if (methodDesc == '(Landroid/view/View;)V') {
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackViewOnClick", "(Landroid/view/View;)V", false)
                isHasTracked = true
                return
            }
        }

        if (interfaces != null && interfaces.length > 0) {
            LogMethodCell logMethodCell = LogHookConfig.sInterfaceMethods.get(methodNameDesc)
            if (logMethodCell != null && interfaces.contains(logMethodCell.parent)) {
                LogAnalyticsUtil.visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE
                        , logMethodCell.agentName, logMethodCell.agentDesc, logMethodCell.paramsStart, logMethodCell.paramsCount, logMethodCell.opcodes)
                isHasTracked = true
            }
        }

//        if (LogAnalyticsUtil.isInstanceOfAdapter(superName)) {
            LogMethodCell logMethodCell = LogHookConfig.sAdapterMethods.get(methodNameDesc)
            if (logMethodCell != null) {
                visitedFragMethods.add(methodNameDesc)
                LogAnalyticsUtil.visitMethodWithLoadedParams(methodVisitor, Opcodes.INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, logMethodCell.agentName, logMethodCell.agentDesc, logMethodCell.paramsStart, logMethodCell.paramsCount, logMethodCell.opcodes)
                isHasTracked = true
            }
//        }

        if (!isHasTracked) {
            if (methodNameDesc == 'onClick(Landroid/view/View;)V') {
                methodVisitor.visitVarInsn(ALOAD, 1)
                methodVisitor.visitMethodInsn(INVOKESTATIC, LogHookConfig.LOG_ANALYTICS_BASE, "trackViewOnClick", "(Landroid/view/View;)V", false)
                isHasTracked = true
            }
        }
    }

    /**
     * 该方法是当扫描器扫描到类注解声明时进行调用
     * @param s 注解的类型。它使用的是（“L” + “类型路径” + “;”）形式表述
     * @param b 表示的是，该注解是否在 JVM 中可见
     * 1.RetentionPolicy.SOURCE：声明注解只保留在 Java 源程序中，在编译 Java 类时注解信息不会被写入到 Class。如果使用的是这个配置 ASM 也将无法探测到这个注解。
     * 2.RetentionPolicy.CLASS：声明注解仅保留在 Class 文件中，JVM 运行时并不会处理它，这意味着 ASM 可以在 visitAnnotation 时候探测到它，但是通过Class 反射无法获取到注解信息。
     * 3.RetentionPolicy.RUNTIME：这是最常用的一种声明，ASM 可以探测到这个注解，同时 Java 反射也可以取得注解的信息。所有用到反射获取的注解都会用到这个配置，就是这个原因。
     * @return
     */
    @Override
    AnnotationVisitor visitAnnotation(String s, boolean b) {
        if (s == 'Lcom/mampod/track/sdk/annotation/AutoTrackDataViewOnClick;') {
            isAutoTrackViewOnClickAnnotation = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @AutoTrackDataViewOnClick")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/AutoIgnoreTrackDataOnClick;') {
            isAutoTrackIgnoreTrackOnClick = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @AutoIgnoreTrackDataOnClick")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/AutoDataInstrumented;') {
            isHasInstrumented = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @AutoDataInstrumented")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/SubPageOpen;') {
            isAutoTraceSubPage = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @SubPageOpen")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/PageOpen;') {
            isPageEnter = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @PageOpen")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/PageExit;') {
            isPageExit = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @PageExit")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/PageResume;') {
            isPageResume = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @PageResume")
        }

        if (s == 'Lcom/mampod/track/sdk/annotation/PageStop;') {
            isPageStop = true
            Logger.info("||发现 ${methodName}${methodDesc} 有注解 @PageStop")
        }
        return super.visitAnnotation(s, b)
    }
}