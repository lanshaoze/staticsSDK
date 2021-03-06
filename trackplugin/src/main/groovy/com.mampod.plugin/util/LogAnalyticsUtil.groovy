package com.mampod.plugin.util

import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.MethodVisitor

/**
 * 字节码方法操作符类
 *
 * @package com.mampod.plugin.util* @author: Jack-Lu
 * @date:
 */
public class LogAnalyticsUtil implements Opcodes {
    private static final HashSet<String> targetFragmentClass = new HashSet()
    private static final HashSet<String> targetActivityClass = new HashSet<>()
    private static final HashSet<String> targetMenuMethodDesc = new HashSet()
    private static final HashSet<String> targetAdapterClass = new HashSet()

    static {
        /**
         * Menu
         */
        targetMenuMethodDesc.add("onContextItemSelected(Landroid/view/MenuItem;)Z")
        targetMenuMethodDesc.add("onOptionsItemSelected(Landroid/view/MenuItem;)Z")
        targetMenuMethodDesc.add("onNavigationItemSelected(Landroid/view/MenuItem;)Z")

        /**
         * v4 Fragment
         */
        targetFragmentClass.add('android/support/v4/app/Fragment')
        targetFragmentClass.add('android/support/v4/app/ListFragment')
        targetFragmentClass.add('android/support/v4/app/DialogFragment')

        /**
         * Fragment
         */
        targetFragmentClass.add('android/app/Fragment')
        targetFragmentClass.add('android/app/ListFragment')
        targetFragmentClass.add('android/app/DialogFragment')

        /**
         * For AndroidX Fragment
         */
        targetFragmentClass.add('androidx/fragment/app/Fragment')
        targetFragmentClass.add('androidx/fragment/app/ListFragment')
        targetFragmentClass.add('androidx/fragment/app/DialogFragment')

        /**
         * For Activity
         */

        targetActivityClass.add('android/app/Activity')
        targetActivityClass.add('android/support/v4/app/FragmentActivity')
        targetActivityClass.add('android/support/v7/app/AppCompatActivity')

        /**
         * for AndroidX Activity
         */
        targetActivityClass.add('androidx/fragment/app/FragmentActivity')
        targetActivityClass.add('androidx/appcompat/app/AppCompatActivity')

    }

    static boolean isSynthetic(int access) {
        return (access & ACC_SYNTHETIC) != 0
    }

    static boolean isPrivate(int access) {
        return (access & ACC_PRIVATE) != 0
    }

    static boolean isPublic(int access) {
        return (access & ACC_PUBLIC) != 0
    }

    static boolean isProtect(int access) {
        return (access & ACC_PROTECTED) != 0
    }

    static boolean isStatic(int access) {
        return (access & ACC_STATIC) != 0
    }

    static boolean isTargetMenuMethodDesc(String nameDesc) {
        return targetMenuMethodDesc.contains(nameDesc)
    }

    static boolean isTargetFragmentClass(String className) {
        return targetFragmentClass.contains(className)
    }

    static boolean isInstanceOfFragment(String superName) {
        return targetFragmentClass.contains(superName)
    }

    static boolean isInstanceOfActivity(String superName) {
        return targetActivityClass.contains(superName)
    }

    static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false)
    }
}
