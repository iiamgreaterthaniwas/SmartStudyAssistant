package com.study.main;

import com.study.ui.MainFrame;
import javax.swing.*;

/**
 * 智能学习助手系统 - 主程序入口
 * Smart Study Assistant System
 * 
 * @author 你的学号_你的姓名
 * @version 1.0
 * @date 2025-12-23
 */
public class Main {
    public static void main(String[] args) {
        // 设置系统外观为原生外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 在事件调度线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}