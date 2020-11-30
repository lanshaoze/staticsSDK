package com.mampod.plugin.bean

/**
 * 用户自定义的功能
 *
 * @package com.mampod.plugin.bean
 * @author: Jack-Lu
 * @date:
 */
class AutoClassFilter {
    String ClassName = ''
    String InterfaceName = ''
    String MethodName = ''
    String MethodDes = ''
    Closure MethodVisitor
    boolean isAnnotation = false
}