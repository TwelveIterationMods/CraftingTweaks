package net.blay09.mods.craftingtweaks.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiScreenClassTransformer implements IClassTransformer {

    public static final Logger logger = LogManager.getLogger();

    public static final String OBF_CLASS = "bdw";
    public static final String MCP_CLASS = "net.minecraft.client.gui.GuiScreen";

    public static final String SRG_METHOD = "func_146274_d";
    public static final String MCP_METHOD = "handleMouseInput";

    public static final String METHOD_DESC = "()V";

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
        String methodName;
        if(className.equals(OBF_CLASS)) {
            methodName = SRG_METHOD;
        } else if(className.equals(MCP_CLASS)) {
            methodName = MCP_METHOD;
        } else {
            return bytes;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        for(MethodNode method : classNode.methods) {
            if (method.name.equals(methodName) && method.desc.equals(METHOD_DESC)) {
                logger.info("CraftingTweaks will now patch {} in {}...", methodName, className);
                MethodNode mn = new MethodNode();
                Label notClicked = new Label();
                //mn.visitMethodInsn(Opcodes.INVOKESTATIC, "org/lwjgl/input/Mouse", "getEventButtonState", "()Z", false);
                //mn.visitJumpInsn(Opcodes.IFEQ, notClicked); // if getEventButtonState false, continue after
                mn.visitVarInsn(Opcodes.ILOAD, 1); // push mouseX
                mn.visitVarInsn(Opcodes.ILOAD, 2); // push mouseY
                mn.visitVarInsn(Opcodes.ILOAD, 3); // push button
                mn.visitMethodInsn(Opcodes.INVOKESTATIC, "net/blay09/mods/craftingtweaks/CraftingTweaks", "onGuiClick", "(III)Z", false); // call onGuiClick
                mn.visitJumpInsn(Opcodes.IFEQ, notClicked); // if onGuiClick false, continue after
                mn.visitInsn(Opcodes.RETURN); // otherwise stop here
                mn.visitLabel(notClicked); // continue from here
                AbstractInsnNode insertAfter = null;
                for(int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode node = method.instructions.get(i);
                    if(node instanceof VarInsnNode) {
                        if(node.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) node).var == 3) { // ISTORE 3
                            insertAfter = node;
                            break;
                        }
                    }
                }
                if(insertAfter != null) {
                    method.instructions.insert(insertAfter, mn.instructions);
                    logger.info("CraftingTweaks successfully patched {} in {}!", methodName, className);
                } else {
                    logger.warn("CraftingTweaks failed to patch {0}::{1} ({2} not found) - transfering into crafting grids will not work!", className, methodName, "ISTORE 3");
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
