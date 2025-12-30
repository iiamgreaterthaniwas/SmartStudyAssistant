package com.study.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 设置窗口 - 模态对话框
 * 用于系统外观设置
 * 学号：你的学号  姓名：你的姓名
 */
public class SettingsWindow extends JDialog {
    private JComboBox<String> themeCombo;
    private JComboBox<String> fontCombo;
    private JButton applyButton;
    private MainFrame mainFrame;  // 引用主窗口

    public SettingsWindow(MainFrame parent) {
        super(parent, "外观设置", true); // 模态对话框
        this.mainFrame = parent;
        initComponents();
        setupUI();
        loadCurrentSettings(); // 加载当前设置
    }

    private void initComponents() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("外观设置");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 设置面板
        JPanel settingsPanel = createSettingsPanel();
        mainPanel.add(settingsPanel, BorderLayout.CENTER);

        // 底部按钮
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 主题设置
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel themeLabel = new JLabel("界面主题：");
        themeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        panel.add(themeLabel, gbc);

        gbc.gridx = 1;
        String[] themes = {"浅色主题", "深色主题"};
        themeCombo = new JComboBox<>(themes);
        themeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        themeCombo.setToolTipText("选择主界面主题 - 学号：你的学号 姓名：你的姓名");
        themeCombo.addActionListener(e -> onSettingChanged());
        panel.add(themeCombo, gbc);

        // 字体大小设置
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel fontLabel = new JLabel("字体大小：");
        fontLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        panel.add(fontLabel, gbc);

        gbc.gridx = 1;
        String[] fontSizes = {"小 (12px)", "中 (14px)", "大 (16px)", "特大 (18px)"};
        fontCombo = new JComboBox<>(fontSizes);
        fontCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        fontCombo.setSelectedIndex(1); // 默认选中"中"
        fontCombo.addActionListener(e -> onSettingChanged());
        panel.add(fontCombo, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton okButton = new JButton("确定");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.setBackground(new Color(52, 152, 219));
        okButton.setFocusPainted(false);
        okButton.setToolTipText("应用设置并关闭窗口 - 学号：你的学号 姓名：你的姓名");
        okButton.addActionListener(e -> saveAndClose());

        JButton cancelButton = new JButton("取消");
        cancelButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setToolTipText("取消设置");
        cancelButton.addActionListener(e -> dispose());

        applyButton = new JButton("应用");
        applyButton.setPreferredSize(new Dimension(80, 30));
        applyButton.setBackground(new Color(46, 204, 113));
        applyButton.setFocusPainted(false);
        applyButton.setToolTipText("应用设置但不关闭窗口");
        applyButton.setEnabled(false); // 初始时禁用
        applyButton.addActionListener(e -> applySettings());

        panel.add(okButton);
        panel.add(cancelButton);
        panel.add(applyButton);

        return panel;
    }

    // 加载当前设置
    private void loadCurrentSettings() {
        if (mainFrame != null) {
            // 设置当前主题
            String currentTheme = mainFrame.getCurrentTheme();
            themeCombo.setSelectedItem(currentTheme);

            // 设置当前字体大小
            int currentFontSize = mainFrame.getCurrentFontSize();
            int fontIndex = 1; // 默认中
            switch (currentFontSize) {
                case 12: fontIndex = 0; break;
                case 14: fontIndex = 1; break;
                case 16: fontIndex = 2; break;
                case 18: fontIndex = 3; break;
            }
            fontCombo.setSelectedIndex(fontIndex);
        }
    }

    // 设置改变时启用应用按钮
    private void onSettingChanged() {
        applyButton.setEnabled(true);
    }

    // 应用设置到主界面
    private void applySettings() {
        String selectedTheme = (String) themeCombo.getSelectedItem();
        int fontSizeIndex = fontCombo.getSelectedIndex();

        // 获取字体大小数值
        int[] fontSizeValues = {12, 14, 16, 18};
        int fontSize = fontSizeValues[fontSizeIndex];

        // 应用主题到主界面
        if (mainFrame != null) {
            mainFrame.applyTheme(selectedTheme, fontSize);
        }

        // 禁用应用按钮
        applyButton.setEnabled(false);

        // 显示成功消息
        JOptionPane.showMessageDialog(this,
                "外观设置已应用到主界面！\n主题：" + selectedTheme +
                        "\n字体大小：" + fontSize + "px\n学号：你的学号 姓名：你的姓名",
                "设置已应用",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 保存并关闭
    private void saveAndClose() {
        applySettings();
        dispose();
    }
}